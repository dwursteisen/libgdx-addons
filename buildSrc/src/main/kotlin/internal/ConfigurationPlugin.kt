package internal

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.repositories.apply {
            mavenCentral()
        }

        project.plugins.apply("org.jetbrains.kotlin.jvm")
        project.plugins.apply("org.gradle.java-library")

        project.tasks.withType<KotlinCompile>().forEach {
            it.kotlinOptions {
                // force the compilation at 1.8 as it may target Android platform
                this.jvmTarget = "1.8"
                this.freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
    }

}