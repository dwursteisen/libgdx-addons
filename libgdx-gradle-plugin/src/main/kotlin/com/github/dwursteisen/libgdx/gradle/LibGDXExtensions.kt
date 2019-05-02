package com.github.dwursteisen.libgdx.gradle

import java.io.File
import java.net.URL

open class OpenJdk {
    var macOSX: URL = URL("https://cdn.azul.com/zulu/bin/zulu8.36.0.1-ca-jdk8.0.202-macosx_x64.zip")
    var windows: URL = URL("https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_windows-x64_bin.zip")
    var linux: URL = URL("https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_linux-x64_bin.tar.gz")
}
open class LibGDXExtensions {
    var mainClass: String? = null
    var androidMainClass: String? = null
    var assetsDirectory: File? = null
    var version = "1.9.9"
    var openJdk = OpenJdk()
}
