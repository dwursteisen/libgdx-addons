package com.github.dwursteisen.libgdx.gradle.internal

import org.gradle.api.Project
import java.io.File

fun Project.tryFindAssetsDirectory(): File? {
    val assetsDirectory = this.projectDir.listFiles { _, name -> name == "assets" }
    return assetsDirectory.firstOrNull()
}