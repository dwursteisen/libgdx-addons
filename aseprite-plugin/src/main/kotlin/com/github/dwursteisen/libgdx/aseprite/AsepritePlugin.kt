package com.github.dwursteisen.libgdx.aseprite

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class AsepritePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val exts = target.extensions.create("aseprite", AsepritePluginExtentions::class.java)
        exts.exec = target.properties["aseprite.exec"]?.let { File(it.toString()) }
    }
}