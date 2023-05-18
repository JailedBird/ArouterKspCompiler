package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.RegisterTransform
import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import cn.jailedbird.arouter_gradle_plugin.utils.ScanUtil
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

abstract class GetAllClassesTask : DefaultTask() {

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val leftSlash = File.separator == "/"
        allDirectories.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            val root = directory.asFile.absolutePath
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                val path = file.absolutePath.replace(root, "")
                // TODO optimize code
                val processedPath =
                    (if (leftSlash) path else path.replace("\\", "/")).trimStart('/')
                if (file.isFile && ScanUtil.shouldProcessClass(processedPath)) {
                    ScanUtil.scanClass(file)
                }
            }
        }

        allJars.get().forEach { file ->
            println("JarFile : ${file.asFile.absolutePath}")
            if (ScanUtil.shouldProcessPreDexJar(file.asFile.absolutePath)) {
                ScanUtil.scanJar(file.asFile)
            }
        }

        debugCollection(RegisterTransform.registerList)
        // 先对所有Class进行扫描，扫描完毕再寻找遍历Jar寻找对应的注入点[暂时不要去考虑优化问题]
        allJars.get().forEach { file ->
            println("JarFile : ${file.asFile.absolutePath}")
            if (ScanUtil.shouldProcessPreDexJar(file.asFile.absolutePath)) {
                ScanUtil.scanJar(file.asFile)
            }
        }
        // Get jar
        val jar = allJars.get()
            .firstOrNull { it.asFile.absolutePath == RegisterTransform.injectJarName }?.asFile
            ?: error("Can not find inject point, Please import")

        var flag = false
        if (jar.exists()) {
            val file = JarFile(jar)
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement() as JarEntry
                val entryName = jarEntry.name
                if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    flag = true
                    // TODO inject logic to this method
                }
            }
            file.close()
        }

        if (!flag) {
            error("Can not find inject class")
        }
    }


    private fun debugCollection(list: List<ScanSetting>) {
        println("Result:")
        list.forEach { item ->
            println("\n[${item.interfaceName}]")
            item.classList.forEach {
                println("\t $it")
            }
        }
    }
}