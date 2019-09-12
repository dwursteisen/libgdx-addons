package com.github.dwursteisen.libgdx.gradle

import com.github.dwursteisen.libgdx.gradle.internal.tryFindAndroidMainClass
import com.github.dwursteisen.libgdx.gradle.internal.tryFindAssetsDirectory
import com.github.dwursteisen.libgdx.gradle.internal.tryFindMainClass
import org.gradle.api.Project
import java.io.File
import java.net.URL

open class OpenJdk {
    var macOSX: URL = URL("https://cdn.azul.com/zulu/bin/zulu8.36.0.1-ca-jdk8.0.202-macosx_x64.zip")
    var windows: URL = URL("https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_windows-x64_bin.zip")
    var linux: URL = URL("https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_linux-x64_bin.tar.gz")
}


open class LibGDXExtensions(project: Project) {
    var assetsDirectory = project.createProperty<File>()

    var version = project.createProperty<String>().value("1.9.10")

    var mainClass = project.createProperty<List<String>>()
        .value(emptyList())

    var androidClass = project.createProperty<String>()

    var openJdk = OpenJdk()
}

