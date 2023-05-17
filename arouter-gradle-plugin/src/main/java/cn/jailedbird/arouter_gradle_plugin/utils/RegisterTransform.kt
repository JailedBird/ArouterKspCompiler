package cn.jailedbird.arouter_gradle_plugin.utils

import java.io.File

object RegisterTransform {
    val registerList = listOf(
        ScanSetting("IRouteRoot"),
        ScanSetting("IInterceptorGroup"),
        ScanSetting("IProviderGroup"),
    )
    var fileContainsInitClass: File? = null
}