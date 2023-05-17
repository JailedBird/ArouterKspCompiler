plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

gradlePlugin {
    plugins {
        register("TransformActionPlugin") {
            id = "com.zj.transform.action"
            implementationClass = "cn.jailedbird.arouter_gradle_plugin.ARouterPlugin"
        }
    }
}

repositories {
    // maven(url = "https://maven.aliyun.com/repository/public")
    // maven(url = "https://maven.aliyun.com/repository/google")
    // maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:7.4.0")
    compileOnly("commons-io:commons-io:2.8.0")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.4")
    compileOnly("org.ow2.asm:asm-tree:9.4")
}

group = "com.zj.transform.action"
version = "1.0.0"