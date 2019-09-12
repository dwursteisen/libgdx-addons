package com.github.dwursteisen.libgdx.gradle

import org.assertj.core.api.Assertions.*
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TemplatePluginTest {

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
    id("com.github.dwursteisen.libgdx.template")
}

buildscript {
    repositories {
        mavenCentral()
    }
}

allprojects {

    group = "com.github.dwursteisen"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

}
        """.trimIndent()
        )
    }

    @Test
    fun `it should create mandatory folders`() {
        val result = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments("template-core", "template-desktop")
            .withPluginClasspath()
            .build()

        assert(result.task(":template-core")?.outcome == TaskOutcome.SUCCESS)
        assert(result.task(":template-desktop")?.outcome == TaskOutcome.SUCCESS)

        val gameClass = File(temporaryFolder.root, "core/src/main/kotlin/libgdx/GameClass.kt")
        assertThat(gameClass).isFile()

        val assetsDirectory = File(temporaryFolder.root, "core/src/main/assets/")
        assertThat(assetsDirectory).isDirectory()

        val desktopDirectory = File(temporaryFolder.root, "desktop/src/main/kotlin/libgdx/Main.kt")
        assertThat(desktopDirectory).isFile()
    }
}
