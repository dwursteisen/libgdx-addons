package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import com.github.dwursteisen.libgdx.packr.PackrTask
import de.undercouch.gradle.tasks.download.Download
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
        project.apply { it.plugin("org.jetbrains.gradle.plugin.idea-ext") }

        logger.info("Detecting Desktop module. Will add custom tasks: dist and run.")
        exts.mainClass = exts.mainClass ?: tryFindMainClass(project)
        exts.assetsDirectory = exts.assetsDirectory ?: project.tryFindAssetsDirectory()

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer

        addDistTask(project, sourceSets)
        addRunTask(project, sourceSets)
        addOpenJdkDownloadTask(project)
        addPackr(project)
    }

    private fun addOpenJdkDownloadTask(project: Project) {
        project.apply { it.plugin("de.undercouch.download") }
        project.tasks.create("open-jdk-mac", Download::class.java) { dl ->
            dl.group = "packr"
            dl.dest(project.file("build/open-jdk/open-jdk-mac.zip"))
            dl.overwrite(false)
            dl.doFirst {
                dl.src(exts.openJdk.macOSX)
            }
        }
    }

    private fun addPackr(project: Project) {
        val task = project.tasks.create("packr-macosx", PackrTask::class.java) {
            it.group = "packr"
            it.classpath = project.tasks.getByPath("dist").outputs.files.singleFile
            it.outputDir = project.file("build/parkr/${project.rootProject.name}.app")

            it.doFirst {
                it as PackrTask
                it.jdk = project.file("build/open-jdk/open-jdk-mac.zip").absolutePath
                it.bundleIdentifier = project.rootProject.name
                it.mainClass = exts.mainClass
            }
        }

        task.dependsOn("dist", "open-jdk-mac")
    }

    private fun addRunTask(project: Project, sourceSets: SourceSetContainer) {
        project.tasks.create("run", JavaExec::class.java) { exec ->
            exec.group = "libgdx"
            exec.doFirst {
                it as JavaExec
                it.main = exts.mainClass!!
                it.classpath = sourceSets.getByName("main").runtimeClasspath
                it.standardInput = System.`in`
                it.workingDir = exts.assetsDirectory!!
            }
        }
    }

    private fun addDistTask(project: Project, sourceSets: SourceSetContainer) {
        val dist = project.tasks.create("dist", Jar::class.java) { jar ->
            jar.group = "libgdx"

            jar.archiveFileName.set("${project.rootProject.name}-desktop.jar")

            jar.doFirst {
                jar.from(project.files(sourceSets.getByName("main").output.classesDirs))
                jar.from(project.files(sourceSets.getByName("main").output.resourcesDir))

                val files = project.files(sourceSets.getByName("main").runtimeClasspath)
                jar.from(files.filter { f -> f.exists() }.map { f -> if (f.isDirectory) f else project.zipTree(f) })

                jar.from(project.files(exts.assetsDirectory))
                jar.manifest { m ->
                    m.attributes(mapOf("Main-Class" to exts.mainClass))
                }
            }
        }
        dist.dependsOn(project.tasks.getByName("classes"))
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
            val use = fileDetails.open().use {
                it.bufferedReader()
                        .lineSequence()
                        .filter { line -> line.contains("fun main(") || line.contains("import com.badlogic.gdx.backends.lwjgl.LwjglApplication") }
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