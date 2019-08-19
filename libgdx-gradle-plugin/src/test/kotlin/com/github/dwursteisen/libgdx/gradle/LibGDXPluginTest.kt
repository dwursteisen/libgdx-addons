package com.github.dwursteisen.libgdx.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LibGDXPluginTest {

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
    kotlin("jvm") version "1.3.30"
    id("libgdx")
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
    fun `it should create mandatory folders`() {
        val settings = temporaryFolder.newFile("settings.gradle.kts")

        val result = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments(":build")
            .withPluginClasspath()
            .build()

        assert(result.task(":build")?.outcome == TaskOutcome.SUCCESS)

        val coreDirectory = File(temporaryFolder.root, "core/src/main/kotlin/")
        assert(coreDirectory.isDirectory)

        val desktopDirectory = File(temporaryFolder.root, "desktop/src/main/kotlin/")
        assert(desktopDirectory.isDirectory)

        assert(settings.readText().contains("include(\"core\", \"desktop\")"))
    }
}
