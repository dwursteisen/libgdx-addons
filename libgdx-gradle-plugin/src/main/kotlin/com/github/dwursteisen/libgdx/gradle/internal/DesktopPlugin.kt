package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DesktopPlugin(private val exts: LibGDXExtensions) : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(DesktopPlugin::class.java)

    override fun apply(project: Project) {
        // Add Java plugin to access sourceSets extensions
        project.apply { it.plugin("org.gradle.java") }
        project.apply { it.plugin("org.jetbrains.kotlin.jvm") }
        //   project.apply { it.plugin("org.jetbrains.gradle.plugin.idea-ext") }

        logger.info("Detecting Desktop module. Will add custom tasks: dist and run.")
        exts.mainClass = exts.mainClass ?: tryFindMainClass(project)
        exts.assetsDirectory = exts.assetsDirectory ?: project.tryFindAssetsDirectory()

        with(project) {
            val sourceSets = extensions.getByName("sourceSets") as SourceSetContainer

            val dist = tasks.create("dist", Jar::class.java) { jar ->
                jar.group = "libgdx"

                jar.doFirst {
                    jar.from(files(sourceSets.getByName("main").output.classesDirs))
                    jar.from(files(sourceSets.getByName("main").output.resourcesDir))

                    val files = files(sourceSets.getByName("main").runtimeClasspath)
                    jar.from(files)

                    jar.from(files(exts.assetsDirectory))
                    jar.manifest { m ->
                        m.attributes(kotlin.collections.mapOf(
                                "Main-Class" to exts.mainClass,
                                "Class-path" to files.joinToString(" ") { f -> f.name }
                        ))
                    }
                }
            }
            dist.dependsOn(project.tasks.getByName("classes"))
            tasks.create("run", org.gradle.api.tasks.JavaExec::class.java) { exec ->
                exec.group = "libgdx"
                exec.doFirst {
                    it as JavaExec
                    it.main = exts.mainClass!!
                    it.classpath = sourceSets.getByName("main").runtimeClasspath
                    it.standardInput = java.lang.System.`in`
                    it.workingDir = exts.assetsDirectory!!
                }
            }

        }
    }

    private fun tryFindMainClass(project: Project): String? {
        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        val mainClassVisitor = MainClassVisitor()
        sourceSets.first { it.name == "main" }
                .allSource
                .asFileTree
                .visit(mainClassVisitor)

        return mainClassVisitor.main
    }

    inner class MainClassVisitor(var main: String? = null) : FileVisitor {
        override fun visitFile(fileDetails: FileVisitDetails) {
            logger.info(fileDetails.name)
            // TODO: check if it's main class
            val use = fileDetails.open().use {
                it.bufferedReader()
                        .lineSequence()
                        .filter { it -> it.contains("fun main(") || it.contains("import com.badlogic.gdx.backends.lwjgl.LwjglApplication") }
                        .count() >= 2

            }

            if (use) {
                main = fileDetails.path
                        .replace(".kt", "")
                        .replace("/", ".")
            }
        }

        override fun visitDir(dirDetails: FileVisitDetails) {
            logger.info(dirDetails.name)
        }

    }

}