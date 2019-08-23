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
        val inFolder = File(temporaryFolder.newFolder("src", "main", "assets", "folder"), "in_folder.txt")
        inFolder.writeText("hello folder")

        val result = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments("assets")
            .withPluginClasspath()
            .build()

        assert(result.task(":assets")?.outcome == TaskOutcome.SUCCESS)
        val generated = File(temporaryFolder.root, "build/generated/Assets.kt")
        assert(generated.isFile)
        assert(generated.readText().contains("example.txt"))
        assert(generated.readText().contains("folder/in_folder.txt"))
    }

    @Test
    fun `it should create a Assets object in another directory`() {
        buildFile.writeText("""
// tag::configuration[]
import com.github.dwursteisen.libgdx.assets.AssetsPlugin
import com.github.dwursteisen.libgdx.assets.AssetsPluginExtension
            
plugins {
    id("assets")
}
apply<AssetsPlugin>()

configure<AssetsPluginExtension> {
    assetsClass.set(project.buildDir.resolve("generated/NewAssets.kt"))
}
// end::configuration[]
        """.trimIndent())

        val asset = File(temporaryFolder.newFolder("src", "main", "assets"), "example.txt")
        asset.writeText("hello world")

        val result = GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments("assets")
            .withPluginClasspath()
            .build()

        assert(result.task(":assets")?.outcome == TaskOutcome.SUCCESS)
        val generated = File(temporaryFolder.root, "build/generated/NewAssets.kt")
        assert(generated.isFile)
        assert(generated.readText().contains("example.txt"))
    }
}
