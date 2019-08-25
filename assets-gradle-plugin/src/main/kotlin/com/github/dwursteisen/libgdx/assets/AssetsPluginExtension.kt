package com.github.dwursteisen.libgdx.assets

import com.github.dwursteisen.libgdx.gradle.createProperty
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import java.io.File

open class AssetsPluginExtension(project: Project) {
    /**
     * Which directory should be scan so all files will be referenced in the Assets object.
     */
    val assetsDirectory = project.createProperty<FileCollection>()
        .value(project.files("src/main/assets"))

    /**
     * Which class (aka Assets object) will reference all assets name.
     */
    val assetsClass = project.createProperty<File>()
        .value(project.buildDir.resolve("generated/Assets.kt"))
}
