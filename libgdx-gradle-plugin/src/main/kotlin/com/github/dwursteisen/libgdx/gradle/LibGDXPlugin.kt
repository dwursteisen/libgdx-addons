package com.github.dwursteisen.libgdx.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LibGDXPlugin : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(LibGDXPlugin::class.java)

    override fun apply(project: Project) {
        val exts = project.extensions.create("libgdx", LibGDXExtensions::class.java)

        project.subprojects.forEach {
            when (it.name) {
                "desktop" -> applyDesktop(it, exts)
                "core" -> applyCore(it)
                "android" -> applyAndroid(it)
                else -> doNothing(it)
            }
        }
    }

    private fun doNothing(project: Project) {
        logger.info("Skipping LibGDX configuration for project ${project.name}")
    }

    private fun applyDesktop(project: Project, exts: LibGDXExtensions) {
        // Add Java plugin to access sourceSets extensions
        project.apply { it.plugin("org.gradle.java") }

        logger.info("Detecting Desktop module. Will add custom tasks: dist and run.")
        with(project) {
            val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer

            val dist = tasks.create("dist", Jar::class.java) { jar ->
                jar.group = "libgdx"

                jar.doFirst {
                    jar.from(files(sourceSets.getByName("main").output.classesDirs))
                    jar.from(files(sourceSets.getByName("main").output.resourcesDir))

                    val files = files(sourceSets.getByName("main").runtimeClasspath)
                    jar.from(files)


                    jar.from(files(exts.assetsDirectory))
                    jar.manifest { m ->
                        m.attributes(mapOf(
                                "Main-Class" to exts.mainClass,
                                "Class-path" to files.joinToString(" ") { f -> f.name }
                        ))
                    }
                }
            }
            dist.dependsOn(project.tasks.getByName("classes"))
            tasks.create("run", JavaExec::class.java) { exec ->
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
    }

    private fun applyCore(project: Project) {
        logger.warn("LIBGDX CORE")
    }


    private fun applyAndroid(project: Project) {
        logger.warn("LIBGDX ANDROID")
    }

}