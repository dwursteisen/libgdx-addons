package com.github.dwursteisen.libgdx.assets

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AssetsPluginTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    lateinit var buildFile: File

    @Before
    fun setUp() {
        buildFile = temporaryFolder.newFile("build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("assets")
            }
        """.trimIndent())
    }

    @Test
    fun `it should create a Assets object`() {
        val asset = File(temporaryFolder.newFolder("src", "main", "assets"), "example.txt")
        asset.writeText("hello world")

        val result = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments("assets")
            .withPluginClasspath()
            .build()

        assert(result.task(":assets")?.outcome == TaskOutcome.SUCCESS)
        assert(File(temporaryFolder.root, "build/generated/Assets.kt").isFile)
    }
}
