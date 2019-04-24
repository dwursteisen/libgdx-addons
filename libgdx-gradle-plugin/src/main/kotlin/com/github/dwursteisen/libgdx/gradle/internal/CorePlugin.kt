package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project

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
    }

}