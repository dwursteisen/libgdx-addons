package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.PackrConfig
import org.gradle.api.Project
import java.io.File

open class PackrPluginExtension(val name: String, project: Project) {

    val platform = project.createProperty<PackrConfig.Platform>()

    val jdk = project.createProperty<String>()
    val executable = project.createProperty<String?>()

    val mainClass = project.createProperty<String>()
    val vmArgs = project.createProperty<List<String>>()
    val minimizeJre = project.createProperty<String>()
        .value("soft")

    val classpath = project.createProperty<File>()

    val outputDir = project.createProperty<File>()

    val bundleIdentifier = project.createProperty<String?>()

    val verbose = project.createProperty<Boolean>()
        .value(false)
}
