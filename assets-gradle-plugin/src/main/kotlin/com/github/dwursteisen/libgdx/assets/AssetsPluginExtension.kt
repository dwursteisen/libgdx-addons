package com.github.dwursteisen.libgdx.assets

import org.gradle.api.file.FileCollection
import java.io.File

open class AssetsPluginExtension(
    var assetsDirectory: FileCollection? = null,
    var assetsClass: File? = null
)