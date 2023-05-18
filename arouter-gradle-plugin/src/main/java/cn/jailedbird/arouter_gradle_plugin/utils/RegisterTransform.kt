package cn.jailedbird.arouter_gradle_plugin.utils

import java.io.File

object RegisterTransform {
    val registerList = listOf(
        ScanSetting("IRouteRoot"),
        ScanSetting("IInterceptorGroup"),
        ScanSetting("IProviderGroup"),
    )

    fun print() {
        registerList.forEach { item ->
            println("Item ${item.interfaceName}")
            item.classList.forEach {
                println("\t${it}")
            }
        }
    }

    var fileContainsInitClass: File? = null
}