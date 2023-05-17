package cn.jailedbird.arouter_gradle_plugin.utils

import java.io.File

/**
 * register setting
 */
class ScanSetting(_interfaceName: String) {
    val interfaceName: String

    init {
        interfaceName = INTERFACE_PACKAGE_NAME + _interfaceName
    }

    /**
     * scan result for {@link #interfaceName}
     * class names in this list
     */
    val classList = mutableListOf<String?>()

    /**
     * jar file which contains class: {@link #GENERATE_TO_CLASS_NAME}
     */
    var fileContainsInitClass: File? = null

    companion object {
        const val PLUGIN_NAME = "com.alibaba.arouter"

        /**
         * The register code is generated into this class
         */
        const val GENERATE_TO_CLASS_NAME = "com/alibaba/android/arouter/core/LogisticsCenter"

        /**
         * you know. this is the class file(or entry in jar file) name
         */
        const val GENERATE_TO_CLASS_FILE_NAME = "$GENERATE_TO_CLASS_NAME.class"

        /**
         * The register code is generated into this method
         */
        const val GENERATE_TO_METHOD_NAME = "loadRouterMap"

        /**
         * The package name of the class generated by the annotationProcessor
         */
        const val ROUTER_CLASS_PACKAGE_NAME = "com/alibaba/android/arouter/routes/"

        /**
         * The package name of the interfaces
         */
        private const val INTERFACE_PACKAGE_NAME = "com/alibaba/android/arouter/facade/template/"

        /**
         * register method name in class: {@link #GENERATE_TO_CLASS_NAME}
         */
        const val REGISTER_METHOD_NAME = "register"
    }

}
