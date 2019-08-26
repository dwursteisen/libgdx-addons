object Version {
    val ashley = "1.7.3"
    val assertJ = "3.8.0"
    val android = "3.4.0"
    val mockito = "2.8.47"
    val ktx = "1.9.9-b1"
    val gdx = "1.9.9"
    val junit = "4.12"
    val gif = "1.4"
    val intellij = "0.5"
    val download = "0.5"
    val kotlin = "1.3.30"
    val kotlinpoet = "1.2.0"
}

object Dependencies {
    val ashley = "com.badlogicgames.ashley:ashley:${Version.ashley}"
    val ktx_log = "io.github.libktx:ktx-log:${Version.ktx}"
    val gdx = "com.badlogicgames.gdx:gdx:${Version.gdx}"
    val gdx_lwjgl = "com.badlogicgames.gdx:gdx-backend-lwjgl:${Version.gdx}"
    val gdx_desktop = "com.badlogicgames.gdx:gdx-platform:${Version.gdx}:natives-desktop"
    val gif = "com.madgag:animated-gif-lib:${Version.gif}"
    val kotlinpoet = "com.squareup:kotlinpoet:${Version.kotlinpoet}"
}

object TestDependencies {
    val assertJ = "org.assertj:assertj-core:${Version.assertJ}"
    val mockito = "org.mockito:mockito-core:${Version.mockito}"
    val junit = "junit:junit:${Version.junit}"
}

object Plugins {
    val kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
    val intellij = "org.jetbrains.gradle.plugin.idea-ext:org.jetbrains.gradle.plugin.idea-ext.gradle.plugin:${Version.intellij}"
    val download = "fi.linuxbox.gradle:gradle-download:${Version.download}"
    val android = "com.android.tools.build:gradle:${Version.android}"
}
