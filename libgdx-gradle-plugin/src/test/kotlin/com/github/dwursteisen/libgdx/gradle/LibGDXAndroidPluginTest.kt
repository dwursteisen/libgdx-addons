package com.github.dwursteisen.libgdx.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@Ignore
class LibGDXAndroidPluginTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    lateinit var buildFile: File

    @Before
    fun setUp() {
        buildFile = temporaryFolder.newFile("build.gradle.kts")
        buildFile.writeText(
            """
            plugins {

    id("libgdx")
    kotlin("jvm") version "1.3.30"
}

buildscript {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}

allprojects {

    group = "com.github.dwursteisen"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
    }

}
        """.trimIndent()
        )
    }

    @Test
    fun `it should create Android main class`() {
        temporaryFolder.newFolder("android")
        temporaryFolder.newFolder("core")

        val settings = temporaryFolder.newFile("settings.gradle.kts")
        settings.writeText(
            """
            include("core")
            include("android")
        """.trimIndent()
        )

        temporaryFolder.newFile("local.properties").writeText("sdk.dir=/Users/david/Library/Android/sdk")

        val result = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments(":android:libgdx-android --stacktrace")
            .withPluginClasspath()
            .build()

        assert(result.task(":android:mainClass")?.outcome == TaskOutcome.SUCCESS)

        val generated = File(temporaryFolder.root, "src/main/kotlin/libgdx/Main.kt")
        assert(generated.isFile)
    }
}
