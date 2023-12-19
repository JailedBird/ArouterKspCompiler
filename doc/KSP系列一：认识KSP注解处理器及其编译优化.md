# KSP系列一：认识KSP注解处理器及其编译优化

[![GitHub stars](https://camo.githubusercontent.com/6c31e99c13b3c184ea8bf625258f63c93778481b5b34bdfb8733dedcb71f9271/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f73746172732f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/stargazers) [![GitHub forks](https://camo.githubusercontent.com/907818c4e4e29065d3302f0710d015c454025285749ff57ee43965792c8fc624/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f666f726b732f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/network/members) [![GitHub issues](https://camo.githubusercontent.com/218117ae911964aee614d4f89b8850f1ae662516a3dc3be0af901b94f901e116/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6973737565732f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/issues) [![GitHub license](https://camo.githubusercontent.com/525537538c93207096306e71c320f22bb1cb8e1059d4d63b43b7dcd4f63349b6/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/blob/master/LICENSE)

演示工程： :point_right: [ArouterKspCompiler](https://github.com/JailedBird/ArouterKspCompiler)

文档仓库： :point_right: [Ksp document repo](https://github.com/JailedBird/ArouterKspCompiler/blob/main/doc/)



## 简介

Kotlin Symbol Processing ([KSP](https://github.com/google/ksp)) 可用于开发kotlin轻量级编译器插件，也就是针对Kotlin语言的注解处理器插件；可以说，Kotlin KSP是完全对标Java APT（AbstractProcessor）的框架；

当然，为啥我们要从APT（KAPT）迁移到KSP呢？原因是性能：官网宣称 *与kapt相比，使用KSP的注释处理器的运行速度可以快两倍* ，从描述看相当的厉害；



## 流程

注解处理器原理是JDK1.6中引入的JSR-269（Pluggable Annotations Processing API）规范，如图：

![image-20231219153440788](https://zhaojunchen-1259455842.cos.ap-nanjing.myqcloud.com//imgimage-20231219153440788.png)

上述流程图落实到Java语言中，输入源文件自然是.java文件， 注解处理器框架自然是Java APT（AbstractProcessor）；

**需要注意的是，Java APT只能解析Java代码中的注解!** 在纯Java时代，上述流程是没有任何问题的；但是Kotlin时代，就存在问题：

- Kotlin文件能够使用APT编译时注解？

- 如果能，谁去解析注解？

官方肯定是要兼容支持Kotlin使用APT注解的，具体的方案是使用 ***Kapt***：

- 基于Kotlin文件生成Java Stub，简单理解为Kotlin转Java
- 基于Java Stub去做APT分析，从而使APT插件兼容Kotlin

使用公式总结kapt的本质： `kapt = generateStubs + apt` ，处理流程如下图：

![image-20231219153419077](https://zhaojunchen-1259455842.cos.ap-nanjing.myqcloud.com//imgimage-20231219153419077.png)

Android Studio自带编译分析柱状图中，搜索kaptGenerateStubs任务你会发现他们的耗时非常之高；如果Kotlin占比很多的情况下，这个耗时应该能占到编译总耗时的10%~20%； 而这个任务，仅仅是为兼容Kotlin文件中APT注解；这个Kotlin转Java操作属实是重量级！



官方的ksp方案则不同，具体有以下几点：

- 直接兼容kotlin代码，从根源消除kapt耗时操作；
- 能够完全解析kotlin中各种语法，如val/var、data class等kotlin灵活特性；

- 直接兼容Java代码的注解处理，意味着编写一次ksp插件，kotlin和java都可以直接使用；

**注意：彻底消除generateStub操作，需要移除所有的kapt插件，确保模块配置不存在 `id kotlin-kapt`** 

如下图：

![image-20231219151820726](https://zhaojunchen-1259455842.cos.ap-nanjing.myqcloud.com//imgimage-20231219151820726.png)

另外官方宣称处理注解处理速度相较于APT也有很大提升，特别是KSP使用了全新增量编译机制；



总结起来，KSP的提升来自两方面：

- 彻底移除Kapt中generateStub耗时操作；PS：实打实肉眼可见，约可减少10~25%编译耗时；
- 相较于APT，自身更好的注解处理性能； PS：这个其实不是很好观测；

至此，我们理清了apt、kapt、ksp三者的关系，以及编译优化的着手点；



## 模型

不管是Java文件、Kotlin文件或者Class字节码，其实都是有对应的数据结构去描述他的文件结构的；KSP需要解析Kotlin文件，那么首先就需要使用数据结构去为Kotlin文件（Kotlin语法）建模型，然后才能实现解析；

从KSP角度看，Kotlin文件结构如下：

```
KSFile
  packageName: KSName
  fileName: String
  annotations: List<KSAnnotation>  (File annotations)
  declarations: List<KSDeclaration>
    KSClassDeclaration // class, interface, object
      simpleName: KSName
      qualifiedName: KSName
      containingFile: String
      typeParameters: KSTypeParameter
      parentDeclaration: KSDeclaration
      classKind: ClassKind
      primaryConstructor: KSFunctionDeclaration
      superTypes: List<KSTypeReference>
      // contains inner classes, member functions, properties, etc.
      declarations: List<KSDeclaration>
    KSFunctionDeclaration // top level function
      simpleName: KSName
      qualifiedName: KSName
      containingFile: String
      typeParameters: KSTypeParameter
      parentDeclaration: KSDeclaration
      functionKind: FunctionKind
      extensionReceiver: KSTypeReference?
      returnType: KSTypeReference
      parameters: List<KSValueParameter>
      // contains local classes, local functions, local variables, etc.
      declarations: List<KSDeclaration>
    KSPropertyDeclaration // global variable
      simpleName: KSName
      qualifiedName: KSName
      containingFile: String
      typeParameters: KSTypeParameter
      parentDeclaration: KSDeclaration
      extensionReceiver: KSTypeReference?
      type: KSTypeReference
      getter: KSPropertyGetter
        returnType: KSTypeReference
      setter: KSPropertySetter
        parameter: KSValueParameter
```



看起来是不是很像反射这一套，看名字后大家应该都能有一个基本的理解👀 其实元编程这种对源文件处理的框架，应该都是大同小异的；

我们需要做的就是在自定义的ksp注解处理器插件中解析其中的关键Node，根据既定的规则来生成自定义代码（后续会有详细的实战）；

## 开发

### 使用

当我们使用别人的ksp插件，如何做呢； 详细可参考 [Use your own processor in a project](https://kotlinlang.org/docs/ksp-quickstart.html#use-your-own-processor-in-a-project)

导入仓库：

```
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

在需要使用自定义注解的**每个**模块，导入ksp插件本身，然后使用ksp导入自定义三方插件；

注意，ksp依赖于具体项目kotlin版本，请保证他们是最匹配的；`1.6.10-1.0.4` 前面的1.6.10代表kotlin版本号，1.0.4是小版本号，尽可能更到最新； :point_right:[ksp官网版本查询](https://github.com/google/ksp/releases)

```
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.4"
}

dependencies {
    ksp 'com.github.JailedBird:ArouterKspCompiler:xxx'
}
```

然后[ArouterKspCompiler](https://github.com/JailedBird/ArouterKspCompiler)插件就生效了



### 开发

自行开发ksp插件时，需要注意的细节就很多了，可参考：

- 官方示例文档：https://kotlinlang.org/docs/ksp-quickstart.html#create-a-processor-of-your-own
- 本人示例工程：https://github.com/JailedBird/ArouterKspCompiler



导入kotlin插件

```
plugins {
    kotlin("jvm") version "1.9.21" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.21"))
    }
}
```

添加ksp依赖

```
plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.15")
}
```

编写插件，process中实现解析和文件生成（后续会详细介绍，本文直接介绍个大概！）

```
class AutowiredSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutowiredSymbolProcessor(
            KSPLoggerWrapper(environment.logger), environment.codeGenerator
        )
    }

    class AutowiredSymbolProcessor(
        private val logger: KSPLoggerWrapper,
        private val codeGenerator: CodeGenerator,
    ) : SymbolProcessor {
      
        override fun process(resolver: Resolver): List<KSAnnotated> {
            val symbol = resolver.getSymbolsWithAnnotation(AUTOWIRED_CLASS_NAME)

            val elements = symbol
                .filterIsInstance<KSPropertyDeclaration>()
                .toList()

            if (elements.isNotEmpty()) {
                logger.info(">>> AutowiredSymbolProcessor init. <<<")
                try {
                    parseAutowired(elements)
                } catch (e: Exception) {
                    logger.exception(e)
                }
            }
        }
    }
}
```



使用SPI配置插件地址

META-INF/services
/com.google.devtools.ksp.processing.SymbolProcessorProvider文件中，配置插件的全限定路径；

```
cn.jailedbird.arouter.ksp.compiler.AutowiredSymbolProcessorProvider
```



至此，基础架子搭建起来了；



## 后续

本文只是对ksp进行了简单的介绍，后续会持续更新：

- ksp系列-ksp增量编译机制详解和示例
- ksp实战-Arouter ksp compiler实战分享

如果本项目对您的学习或工作有帮助，请点亮star支持作者😘

参考文献：

- https://kotlinlang.org/docs/ksp-overview.html
- https://github.com/google/ksp
- https://github.com/JailedBird/ArouterKspCompiler
- https://github.com/JailedBird/ArouterKspCompiler/blob/main/doc/
- https://www.yuque.com/jailedbird/gbmyp7/bs5t6e5zthrgfaua?singleDoc# 

