package com.github.dwursteisen.libgdx.gradle

import com.github.dwursteisen.libgdx.gradle.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LibGDXPlugin : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(LibGDXPlugin::class.java)

    override fun apply(project: Project) {
        val exts = project.extensions.create("libgdx", LibGDXExtensions::class.java)

        exts.assetsDirectory = exts.assetsDirectory ?: project.tryFindAssetsDirectory()

        project.subprojects.forEach {
            when (it.name) {
                "core" -> CorePlugin(exts)
                "desktop" -> DesktopPlugin(exts)
                "android" -> AndroidPlugin()
                else -> NopPlugin()
            }.apply(it)
        }
    }

}