package cn.jailedbird.arouter_gradle_plugin

import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.nio.file.Files

abstract class MyTransform : TransformAction<TransformParameters.None> {
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val file = inputArtifact.get().asFile;
        println("Processing $file. File exists = ${file.exists()}")
        if (file.exists()) {
            val outputFile = outputs.file("copy");
            Files.copy(file.toPath(), outputFile.toPath())
        } else {
            throw RuntimeException("File does not exist: " + file.canonicalPath);
        }
    }
}