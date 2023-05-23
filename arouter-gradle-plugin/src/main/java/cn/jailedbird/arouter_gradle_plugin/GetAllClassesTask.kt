package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import cn.jailedbird.arouter_gradle_plugin.utils.Utils
import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayInputStream
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * TODO LIST
 * 1. closeEntry perhaps cause performance problem, Please use ZipEntry to optmize
 * 2. Perhaps we can only scan & copy(or code inject) in one turn, avoid twice scan jar
 * 3. Optimize file path resolve function, make it graceful
 * 3. Please test build result, can run safely
 * */
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
        val targetList: List<ScanSetting> = listOf(
            ScanSetting("IRouteRoot"),
            ScanSetting("IInterceptorGroup"),
            ScanSetting("IProviderGroup"),
        )

        JarOutputStream(output.asFile.get().outputStream()).use { jarOutput ->
            // Scan directoy (Copy and Collection)
            allDirectories.get().forEach { directory ->
                val directoryPath =
                    if (directory.asFile.absolutePath.endsWith(File.separatorChar)) {
                        directory.asFile.absolutePath
                    } else {
                        directory.asFile.absolutePath + File.separatorChar
                    }
                println("Directory is $directoryPath")
                directory.asFile.walk().forEach { file ->
                    if (file.isFile) {
                        val entryName = if (leftSlash) {
                            file.path.substringAfter(directoryPath)
                        } else {
                            file.path.substringAfter(directoryPath).replace(File.separatorChar, '/')
                        }
                        println("\tDirectory entry name $entryName")
                        if (entryName.isNotEmpty()) {
                            // Copy
                            val entry = JarEntry(entryName)
                            jarOutput.putNextEntry(entry)
                            file.inputStream().use { input ->
                                // Use stream to detect register
                                if (Utils.shouldProcessClass(entryName)) {
                                    Utils.scanClass(input, targetList, false)
                                }
                                input.copyTo(jarOutput)
                            }
                            jarOutput.closeEntry()
                        }
                    }
                }
            }

            debugCollection(targetList)
            var originInject: ByteArray? = null
            // Scan Jar, Copy & Scan & Code Inject
            val jars = allJars.get().map { it.asFile }
            for (sourceJar in jars) {
                println("Jar file is $sourceJar")
                val jar = JarFile(sourceJar)
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    try {
                        // Exclude directory
                        if (entry.isDirectory || entry.name.isEmpty()) {
                            continue
                        }
                        println("\tJar entry is ${entry.name}")
                        if (entry.name != ScanSetting.GENERATE_TO_CLASS_FILE_NAME) {
                            jarOutput.putNextEntry(JarEntry(entry.name))
                            jar.getInputStream(entry).use { inputs ->
                                // Scan and choose
                                if (Utils.shouldProcessClass(entry.name)) {
                                    Utils.scanClass(inputs, targetList, false)
                                }
                                IOUtils.copy(inputs, jarOutput)
                            }
                            jarOutput.closeEntry()
                        } else {
                            // Skip
                            println("Find inject byte code, Skip ${entry.name}")
                            // jarOutput.putNextEntry(JarEntry(entry.name))
                            // jar.getInputStream(entry).use { inputs ->
                            //     originInject = inputs.readAllBytes()
                            //     IOUtils.copy(inputs, jarOutput)
                            // }
                            // jarOutput.closeEntry()
                            jar.getInputStream(entry).use { inputs ->
                                originInject = inputs.readAllBytes()
                                println("Find befor originInject is ${originInject?.size}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Merge jar error entry:${entry.name}, error is $e ")
                    }

                }
                jar.close()
            }
            debugCollection(targetList)
            // Skip
            println("Start inject byte code")
            val registerCodeGenerator = RegisterCodeGenerator(targetList)
            if (originInject == null) {
                error("Can not find arouter inject point")
            }
            val resultByteArray =
                registerCodeGenerator.referHackWhenInit(ByteArrayInputStream(originInject))
            println("resultByteArray size is ${resultByteArray.size}")
            jarOutput.putNextEntry(JarEntry(ScanSetting.GENERATE_TO_CLASS_FILE_NAME))
            IOUtils.copy(ByteArrayInputStream(resultByteArray), jarOutput)
            jarOutput.closeEntry()
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