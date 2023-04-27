package cn.jailedbird.arouter_gradle_plugin

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class MyTask : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val inputCount: Property<Int>

    @TaskAction
    fun action() {
        val outputFile = outputFile.get().asFile
        outputFile.delete()
        outputFile.parentFile.mkdirs()
        Files.write(outputFile.toPath(), ("Count is: " + inputCount.get()).toByteArray())
    }
}

abstract class ConsumerTask : DefaultTask() {
    @get:Internal
    abstract var artifactCollection: ArtifactCollection

    @InputFiles
    fun getMyInputFiles(): FileCollection {
        return artifactCollection.artifactFiles
    }

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun action() {
        val outputFile = outputFile.get().asFile
        outputFile.delete()
        outputFile.parentFile.mkdirs()
        var outputContent = "";
        for (f in getMyInputFiles().files) {
            Logger.w(f.canonicalPath)
            outputContent += f.canonicalPath + "\n"
        }

        Files.write(outputFile.toPath(), outputContent.toByteArray())
    }
}
