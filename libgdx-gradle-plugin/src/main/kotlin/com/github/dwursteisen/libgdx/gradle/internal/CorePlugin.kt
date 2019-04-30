package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.assets.AssetsPluginExtension
import com.github.dwursteisen.libgdx.assets.AssetsTask
import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import java.io.File
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class CorePlugin(private val exts: LibGDXExtensions) : Plugin<Project> {
    override fun apply(project: Project) {
        // Add Java plugin to access sourceSets extensions
        project.apply { it.plugin("org.gradle.java") }
        project.apply { it.plugin("org.jetbrains.kotlin.jvm") }
        project.apply { it.plugin("java-library") }

        exts.assetsDirectory = exts.assetsDirectory ?: project.tryFindAssetsDirectory()

        val version = exts.version
        project.dependencies.add("api", "com.badlogicgames.gdx:gdx:$version")
        project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib")

        addAssetsTask(project)
        setupKotlin(project)
    }

    private fun setupKotlin(project: Project) {
        project.afterEvaluate {
            project.tasks.withType(KotlinCompile::class.java).forEach {
                it.kotlinOptions {
                    this as KotlinJvmOptions
                    // force the compilation at 1.8 as it may target Android platform
                    this.jvmTarget = "1.8"
                    this.freeCompilerArgs = listOf("-Xjsr305=strict")
                }
            }
        }
    }

    private fun addAssetsTask(project: Project) {
        project.apply { it.plugin("assets") }

        project.extensions.configure(AssetsPluginExtension::class.java) {
            it.assetsDirectory = exts.assetsDirectory?.let { f -> project.files(f) }
            it.assetsClass = File(project.buildDir, "generated/Assets.kt")
        }

        project.afterEvaluate {
            val assets = it.tasks.withType(AssetsTask::class.java)
            val compileKotlinTask = it.tasks.withType(KotlinCompile::class.java)
            compileKotlinTask.forEach { compiler ->
                compiler.dependsOn(assets)
            }

        }
    }

}