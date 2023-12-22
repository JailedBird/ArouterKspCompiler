# KSP系列二：通过案例理解KSP增量编译机制

[![GitHub stars](https://camo.githubusercontent.com/6c31e99c13b3c184ea8bf625258f63c93778481b5b34bdfb8733dedcb71f9271/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f73746172732f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/stargazers) [![GitHub forks](https://camo.githubusercontent.com/907818c4e4e29065d3302f0710d015c454025285749ff57ee43965792c8fc624/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f666f726b732f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/network/members) [![GitHub issues](https://camo.githubusercontent.com/218117ae911964aee614d4f89b8850f1ae662516a3dc3be0af901b94f901e116/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6973737565732f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/issues) [![GitHub license](https://camo.githubusercontent.com/525537538c93207096306e71c320f22bb1cb8e1059d4d63b43b7dcd4f63349b6/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f4a61696c6564426972642f41726f757465724b7370436f6d70696c65722e737667)](https://github.com/JailedBird/ArouterKspCompiler/blob/master/LICENSE)

演示工程： :point_right: [AutoService](https://github.com/JailedBird/AutoService)

文档仓库： :point_right: [Ksp document repo](https://github.com/JailedBird/ArouterKspCompiler/blob/main/doc/)



## 项目简介

本文通过AutoService KSP插件来学习KSP注解处理器的增量编译机制；写过APT注解处理器的应该都用过google提供的[AutoService](https://github.com/google/auto/tree/main/service) ，AutoService是自动为Service Provider Interface（SPI）生成 `META-INF/services` 配置的插件；这里我们使用KSP自行实现[AutoService](https://github.com/JailedBird/AutoService)，然后通过增量编译日志，分析增量编译；



具体示例：

首先自定义接口`cn.jailedbird.spi.AInterface`，在其实现类`cn.jailedbird.spi.AImpl`加上 @AutoService，编译后，会在模块 `META-INF/services` 生成 `cn.jailedbird.spi.AInterface` 文件，内容为 `cn.jailedbird.spi.AImpl`  (注意：本设计中，一个接口只能有一个带AutoService的实现类)



## 代码结构



1、 创建library模块，添加自定义注解；直接创建Java模块即可；

```
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AutoService(val target: KClass<*> = Unit::class)
```



2、 创建compiler模块，导入ksp依赖；注意必须为Java模块；

```
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id 'java-library'
    id 'org.jetbrains.kotlin.jvm'
}

dependencies {
    implementation project(':library')

    // ksp https://github.com/google/ksp/releases/tag/1.7.20-1.0.6
    implementation 'com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.8'
    // // https://square.github.io/kotlinpoet/
    // implementation("com.squareup:kotlinpoet:1.12.0")
}
```

3、 实现核心类，并添加SPI配置；

    class AutoServiceSymbolProcessorProvider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): 
        SymbolProcessor {
            return AutoServiceSymbolProcessor(
                KspLoggerWrapper(environment.logger), environment.codeGenerator
            )
        }
    
        class AutoServiceSymbolProcessor(
            @Suppress("unused") private val logger: KspLoggerWrapper,
            private val codeGenerator: CodeGenerator,
        ) : SymbolProcessor {
    
            override fun process(resolver: Resolver): List<KSAnnotated> {
                val symbol = resolver.getSymbolsWithAnnotation(AUTO_SERVICE_CLASS_NAME)
    
                val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()
    
                elements.forEach { element ->
                    // xxx
                }
            }
    
        }
    }

使用SPI配置插件地址

META-INF/services
/com.google.devtools.ksp.processing.SymbolProcessorProvider文件中，配置插件的全限定路径；

```
cn.jailedbird.spi.compiler.AutoServiceSymbolProcessorProvider
```



支持插件就算是搭起架子；

4、 app模块使用ksp导入插件模块；

```
plugins {
	// xxx
	// 模块导入ksp插件
    id 'com.google.devtools.ksp' 
}

// 将ksp生成代码添加到IDE索引中，默认不添加，但是打包会自动添加进去的；【非必须】
android.sourceSets.all { it.java.srcDir("build/generated/ksp/${it.name}/kotlin/") }


dependencies {
    // Local develop
    ksp project(":compiler") // ksp导入插件
    implementation project(":library")
    // Remote implementation
    //    def VERSION = "1.0.2"
    //    implementation("com.github.JailedBird.AutoService:api:${VERSION}")
    //    ksp("com.github.JailedBird.AutoService:compiler:${VERSION}")
 
}
```

5、 app模块测试用例

```
interface TestInterface1<T> {
    fun hello()
}

@AutoService
open class TestImpl1 : TestInterface1<String> {
    override fun hello() {
        Log.d("TAG", "TestImpl1@${this.hashCode()} hello ")
    }
}
```

然后，你的注解处理器就工作起来了；可以用logger打印下日志看看；

PS：测试阶段最好使用logger.warn打印日志，info需要配置才能打出日志；

## 增量编译

这是本节的重点，先看下处理逻辑process函数处理逻辑；

```
override fun process(resolver: Resolver): List<KSAnnotated> {
    val symbol = resolver.getSymbolsWithAnnotation(AUTO_SERVICE_CLASS_NAME)

    val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()

    elements.forEach { element ->
        val spi: AutoService = element.findAnnotationWithType()
            ?: error("Error ksp process, with [AutoServiceSymbolProcessor]")
        val targetInterfaceClassName = try {
            spi.target.qualifiedName.toString()
        } catch (e: Exception) {
            /**
             * Bug: ksp: com.google.devtools.ksp.KSTypeNotPresentException: java.lang.ClassNotFoundException: cn.jailedbird.spi.test.TestInterface1
             * Official document: https://github.com/google/ksp/issues?q=ClassNotFoundException++KClass%3C*%3E
             * temporary fix method as follows, but it is not perfect!!!
             * TODO completely fix it!
             * */
            ((e as? KSTypeNotPresentException)?.cause as? ClassNotFoundException)?.message.toString()
        }

        val targetImplClassName = element.qualifiedName!!.asString()

        if (targetInterfaceClassName in VOID_LIST) {
            val supers = element.superTypes.toList()
            if (supers.size == 1) {
                val sp: KSDeclaration = supers[0].resolve().declaration
                sp.isInterfaceCheck{
                    generate(
                        element, sp.qualifiedName!!.asString(), targetImplClassName
                    )
                }
            } else {
                error("Please configure target")
            }
        } else {
            val targetKSDeclaration: KSDeclaration? = element.isSubclassOf(targetInterfaceClassName)
            if (targetKSDeclaration != null) {
                targetKSDeclaration.isInterfaceCheck{
                    generate(
                        element, targetInterfaceClassName, targetImplClassName
                    )
                }
            } else {
                error("AutoService.target is ${targetInterfaceClassName}, but ${element.simpleName.asString()} is not a subclass of  $targetInterfaceClassName")
            }
        }

    }

    return emptyList()
}
```

总结起来：

- 从Resolver筛选出带有cn.jailedbird.spi.api.AutoService注解的元素，因为AutoService是类级别注解 ，所以简单做了KSClassDeclaration筛选；得到`elements:List<KSClassDeclaration>`，它描述本轮注解处理中，标记AutoService的类；

- 针对单个标记注解的类，首先获取他的注解类（@Retention(AnnotationRetention.BINARY)， 然后获取其中的 `val target: KClass<*> = Unit::class` ；

  这里有偷懒优化，如果实现类单继承自接口，则target可以省略，默认直接将该接口作为target，否则省略的话会报错；当然还存在一些正确性校验，可以顺便看下；

- target作为SPI的接口，需要将target类（接口）的全限定路径作为SPI文件名，将element本身（实现类）的全限定名作为SPI文件中的配置内容；调用generate函数生成文件；

  ```
  private fun generate(element: KSClassDeclaration, interfaceName: String, implName: String) {
      codeGenerator.createNewFile(
          Dependencies(false, element.containingFile!!),
          "META-INF/services", interfaceName, ""
      ).use {
          it.write(implName.toByteArray())
      }
  }
  ```





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

