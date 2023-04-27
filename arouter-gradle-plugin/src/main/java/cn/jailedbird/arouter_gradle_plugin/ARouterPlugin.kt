@file:Suppress("unused")

package cn.jailedbird.arouter_gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import java.io.File

class ARouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        Logger.make(project)
        Logger.w("Init step!")
        project.run {
            val artifactType = Attribute.of("artifactType", String::class.java)
            dependencies.registerTransform(MyTransform::class.java) {
                it.from.attribute(artifactType, "jar")
                it.to.attribute(artifactType, "my-custom-type")
            }
            val myTaskProvider = tasks.register("myTask", MyTask::class.java) {
                it.inputCount.set(10)
                it.outputFile.set(File("build/myTask/output/file.jar"))
            }
            val includedConfiguration = configurations.create("includedConfiguration")
            dependencies.add(
                includedConfiguration.name,
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10"
            )

            val combinedInputs =
                project.files(includedConfiguration, myTaskProvider.map { it.outputFile })
            val myConfiguration = configurations.create("myConfiguration")
            dependencies.add(
                myConfiguration.name,
                project.files(project.provider { combinedInputs })
            )

            tasks.register("consumerTask", ConsumerTask::class.java) {
                it.artifactCollection = myConfiguration.incoming.artifactView { viewConfiguration ->
                    viewConfiguration.attributes.attribute(artifactType, "my-custom-type")
                }.artifacts
                it.outputFile.set(File("build/consumerTask/output/output.txt"))
            }
        }
    }
}