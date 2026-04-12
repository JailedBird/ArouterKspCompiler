# ArouterKspCompiler
Arouter KSP annotation processor 

[![GitHub stars](https://img.shields.io/github/stars/JailedBird/ArouterKspCompiler.svg)](https://github.com/JailedBird/ArouterKspCompiler/stargazers) [![GitHub forks](https://img.shields.io/github/forks/JailedBird/ArouterKspCompiler.svg)](https://github.com/JailedBird/ArouterKspCompiler/network/members) [![GitHub issues](https://img.shields.io/github/issues/JailedBird/ArouterKspCompiler.svg)](https://github.com/JailedBird/ArouterKspCompiler/issues) [![GitHub license](https://img.shields.io/github/license/JailedBird/ArouterKspCompiler.svg)](https://github.com/JailedBird/ArouterKspCompiler/blob/master/LICENSE) [![](https://jitpack.io/v/JailedBird/ArouterKspCompiler.svg)](https://jitpack.io/#JailedBird/ArouterKspCompiler)


ksp分享笔记：[KSP注解处理器分享](https://www.yuque.com/jailedbird/gbmyp7/bs5t6e5zthrgfaua?singleDoc)

ArouterKspCompiler插件是对ARouter官方注解处理器插件（com.alibaba:arouter-compiler）的[KSP（Kotlin Symbol Processing API）](https://kotlinlang.org/docs/ksp-overview.html)版本实现，旨在使用KSP提升kotlin注解的处理速度；



Kotlin Symbol Processing（KSP）是用于处理Kotlin编程语言中的注解的注解处理器框架；换言之，KSP框架是面向Kotlin语言的  "Java注解处理器（APT，Annotation Processing Tool）"，并且KSP直接兼容Java的注解处理；KSP注解处理器采用增量处理的方式，只处理需要更新的注解和代码，避免重新处理整个编译过程，从而显著加速了注解处理的速度；此外KSP可以避免类似KAPT中将Kotlin文件转化为Java存根的耗时操作；

>  Kotlin Symbol Processing (*KSP*) is an API that you can use to develop lightweight compiler plugins. KSP provides a simplified compiler plugin API that leverages the power of Kotlin while keeping the learning curve at a minimum. Compared to [kapt](https://kotlinlang.org/docs/kapt.html), annotation processors that use KSP can run up to 2 times faster. 



## 接入指南：

### kotlin && ksp配置

1、 导入KSP插件；

因KSP插件和Kotlin版本高度相关， 因此双方需要尽量匹配，附上KSP发布地址[KSP releases](https://github.com/google/ksp/releases)；此外[KSP issues](https://github.com/google/ksp/issues)下面存在很多的BUG修复，因此更加推荐使用高版本的KSP、避免KSP框架本身的问题；这里我会打出各种版本的插件，供大家使用，如果需要其他版本的可以提Issue；

本插件发布在[Jitpack.io](https://jitpack.io/#JailedBird/ArouterKspCompiler)， 版本号可做如下参考:

| kotlin版本号 | 最新ArouterKspCompiler | 建议KSP插件版本号 |
| ------------ | ---------------------- | ----------------- |
| 1.6.10       | 1.6.10-1.0.7        | 1.6.10-1.0.4      |
| 1.7.20       | 1.7.20-1.0.7        | 1.7.20-1.0.8      |
| 1.7.22       | 1.7.22-1.0.7       | 1.7.22-1.0.8      |
| 1.8.0        | 1.8.0-1.0.7         | 1.8.0-1.0.9       |
| 1.8.10       | 1.8.10-1.0.7        | 1.8.10-1.0.9      |
| 1.8.20       | 1.8.20-1.0.7       | 1.8.20-1.0.10     |
| 1.9.10       | 1.9.10-1.0.7 | 1.9.10-1.0.13 |
| 1.9.20 | 1.9.20-1.0.7 | 1.9.20-1.0.14 |
| 2.0.0 | 2.0.0-1.0.24 | 2.0.0-1.0.24 |
| 2.0.20 | 2.0.20-1.0.25 | 2.0.20-1.0.25 |
| 2.1.0 | 2.1.0-1.0.29 | 2.1.0-1.0.29 |
| 2.1.20 |  2.1.20-1.0.31  | 2.1.20-1.0.31  |
| 2.2.0 | 2.2.0-2.0.2            | 2.2.0-2.0.2       |
| 2.2.10       | 2.2.10-2.0.2           | 2.2.10-2.0.2      |
| 2.2.20       | 2.2.20-2.0.4           | 2.2.20-2.0.4 |



> 升级说明：
>
> 1.0.2->1.0.3
>
> ​	1、使用KotlinPoet %S优化Autowired长文案格式
>
> ​	2、修复继承多个IProvider接口时，收集的信息缺失
>
> 1.0.3->1.0.4
>
> ​	1、 [issues8 ](https://github.com/JailedBird/ArouterKspCompiler/issues/8) 修复泛型类中使用@Autowired注解时，出现的泛型类型转换错误；
>
> 1.0.4->1.0.5
>
> ​	1、 [issues9 ](https://github.com/JailedBird/ArouterKspCompiler/issues/9) 修复Autowired 注入lateinit String的崩溃问题，见String注入[代码规则](https://github.com/JailedBird/ArouterKspCompiler/issues/9#issuecomment-1715268031)
>
> 1.0.5->1.0.7
>
> ​	1、 [issues13 ](https://github.com/JailedBird/ArouterKspCompiler/issues/13)修复Autowired注入IProvider的bug；
>
> 1.0.7->xxx
> 	1、  添加KSP2.0（koltin2.0）版本，版本号ArouterKspCompiler版本号跟随KSP版本号；



**ArouterKspCompiler中master分支默认使用kotlin 1.6.10、ksp1.6.10-1.0.4**， 下文中kotlin版本号暂时使用1.6.10替代， 实际使用请按照项目kotlin版本、上述表格灵活替换；

根目录下配置ksp的版本号

```kotlin
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.6.10' apply false
    id 'com.google.devtools.ksp' version '1.6.10-1.0.4' apply false
}
```

在需要使用Arouter的模块中添加插件和参数配置：

老方法配置kapt的方式如下：

```plain
apply plugin: 'kotlin-kapt'

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.getName())
    }
}
```

使用ArouterKspCompiler之后：

```kotlin
plugins {
    id 'kotlin-android'
    id 'com.google.devtools.ksp'
}
// or--> apply plugin: 'kotlin-android'
// or--> apply plugin: 'com.google.devtools.ksp'
ksp {
    arg("AROUTER_MODULE_NAME", project.getName())
}
```

其他参数配置类似即可



**注意：纯Java模块（没有'kotlin-android'插件）建议直接使用annotationProcess导入Arouter原生处理器（com.alibaba:arouter-compiler）即可； 原因如下：**

- 对纯Java项目而言使用Ksp没有很大的优势，相反KSP可能花费更多的时间去兼容、解析Java；
- Ksp虽然兼容Java， 但是兼容性不一定完美； 如可空，可变等细节上的差异，对某些java信息的获取不一定精准；
- KSP不断在迭代， 不可避免会存在潜在的问题，因此在java模块中直接Arouter注解处理器更加安全；



**但是只要使用KSP的话, 就需要添加'kotlin-android'插件, 否则无法触发代码生成**



最后如果需要模块内的生成的KSP代码可以被IDE识别， 请在模块的build.gradle中任意一行进行如下的配置：

```kotlin
android.sourceSets.all { it.java.srcDir("build/generated/ksp/${it.name}/kotlin/") }
```

注：ksp生成的代码默认会被编译到app壳中，但是默认对IDE不可见， Arouter中生成的代码也不需要对开发者可见，这句话不是必要的；



### ArouterKspCompiler接入

项目添加jitpack仓库

```kotlin
maven { url 'https://jitpack.io' }
```

Arouter模块中导入Ksp插件：

```kotlin
ksp 'com.github.JailedBird:ArouterKspCompiler:1.6.10-1.0.3'
```

此时编译项目，可以在模块build文件找到对应生成的文件， 对此大家可以通过 生成文件源码、Json路由表文件（需开启Doc配置生成）等对比去检验插件功能是否正常；

### 增量编译及其日志

建议在 `gradle.properties` 开启如下三个配置

```kotlin
# KSP Incremental processing
# https://kotlinlang.org/docs/ksp-incremental.html#program-elements
ksp.incremental=true
ksp.incremental.log=true
# track classpath
ksp.incremental.intermodule=true
```

三个开关分别是：

- 增量编译， 默认开启 
- 增量编译的相关日志, 保存文件依赖和变更记录等信息（路径：build/kspcache） 
- 我不是很熟，但是建议开启



### 注意事项

0、 常见问题请转到本仓库的Issue中查看（已置顶）

1、 纯Java模块直接使用annotationProcessor导入Arouter原生注解处理器即可， 不要改为ksp导入，避免潜在的bug

2、 如果从纯Java项目迁移为kotlin、Java混编； 就必须使用KSP（或者KAPT）了，不要忘记加入`kotlin-android` 插件， 缺少貌似会导致不生成代码

3、 ArouterKspCompiler内部依赖'com.alibaba:arouter-annotation:1.0.6'， 按照Arouter目前更新状态不存在变化的可能

4、 关于混淆等其他任何配置， 直接按照Arouter的来就可以了

5、 本人会长期维护此代码仓库：[ArouterKspCompiler](https://github.com/JailedBird/ArouterKspCompiler) ， 如果有问题可以提issue、加微信 JailedBird；如果对您有帮助， 请一键三联支持一波🤣



### 参考文献

- https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point



