package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.assets.AssetsTask
import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class CorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add Java plugin to access sourceSets extensions
        project.apply { it.plugin("org.gradle.java") }
        project.apply { it.plugin("org.jetbrains.kotlin.jvm") }
        project.apply { it.plugin("java-library") }

        project.rootProject.extensions.configure(LibGDXExtensions::class.java) { exts ->
            val version = exts.version
            project.dependencies.add("api", "com.badlogicgames.gdx:gdx:$version")
            project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib")
            addAssetsTask(project, exts)
        }

        setupKotlin(project)
    }

    private fun setupKotlin(project: Project) {
        project.tasks.withType(KotlinCompile::class.java).forEach {
            it.kotlinOptions {
                this as KotlinJvmOptions
                // force the compilation at 1.8 as it may target Android platform
                this.jvmTarget = "1.8"
                this.freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
    }

    private fun addAssetsTask(project: Project, exts: LibGDXExtensions) {
        project.apply { it.plugin("assets") }
        project.tasks.withType(AssetsTask::class.java) { task ->
            val assetsDirectory = exts.assetsDirectory.orNull ?: project.tryFindAssetsDirectory()
            task.assetsDirectory.set(project.files(assetsDirectory))

            val compileKotlinTask = project.tasks.withType(KotlinCompile::class.java)
            compileKotlinTask.forEach { compiler ->
                compiler.dependsOn(task)
            }
        }
    }
}
