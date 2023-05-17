package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.RegisterTransform
import cn.jailedbird.arouter_gradle_plugin.utils.ScanUtil
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GetAllClassesTask : DefaultTask() {

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @TaskAction
    fun taskAction() {
        val leftSlash = File.separator == "/"
        allDirectories.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            val root = directory.asFile.absolutePath
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                println("File : ${file.absolutePath}")
                val path = file.absolutePath.replace(root, "")
                val processedPath = (if (leftSlash) path else path.replace("\\", "/")).trimStart('/')
                println("File-SUB : ${processedPath}")
                if (file.isFile && ScanUtil.shouldProcessClass(processedPath)) {
                    ScanUtil.scanClass(file)
                    println("FileScan : ${file.absolutePath}")
                }


            }
        }

        allJars.get().forEach { file ->
            println("JarFile : ${file.asFile.absolutePath}")
            if (ScanUtil.shouldProcessPreDexJar(file.asFile.absolutePath)) {
                ScanUtil.scanJar(file.asFile)
            }
        }

        RegisterTransform.registerList.forEach {
            println("Item ${it.interfaceName}")
            it.classList.forEach {
                println(it)
            }
        }
    }
}
