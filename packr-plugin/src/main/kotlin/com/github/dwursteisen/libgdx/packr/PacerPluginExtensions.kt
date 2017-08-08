package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.PackrConfig
import java.io.File

open class PacerPluginExtensions(
        var platform: PackrConfig.Platform? = null,
        var jdk: String? = null,
        var executable: String? = null,
        var classpath: Array<String>? = null,
        var mainClass: String? = null,
        var vmArgs: Array<String>? = null,
        var minimizeJre: String? = null,
        var outputDir: File? = null,
        var bundleIdentifier: String? = null
)