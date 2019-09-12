package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import com.github.dwursteisen.libgdx.packr.PackrPluginExtension
import fi.linuxbox.gradle.download.Download
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DesktopPlugin : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(DesktopPlugin::class.java)

    override fun apply(project: Project) {
        // Add Java gradle to access sourceSets extensions
        project.apply { it.plugin("org.gradle.java") }
        project.apply { it.plugin("org.jetbrains.kotlin.jvm") }
        project.apply { it.plugin("org.jetbrains.gradle.plugin.idea-ext") }

        project.rootProject.extensions.configure(LibGDXExtensions::class.java) { exts ->
            logger.info("Detecting Desktop module. Will add custom tasks: dist and run.")

            val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer

            addDependencies(project, exts)
            addDistTask(project, sourceSets, exts)
            addRunTask(project, sourceSets, exts)
            addOpenJdkDownloadTask(project, exts)
            addPackr(project, exts)

            setupKotlin(project)
        }
    }

    private fun setupKotlin(project: Project) {
        project.afterEvaluate {
            project.tasks.withType(KotlinCompile::class.java).forEach {
                it.kotlinOptions {
                    this as KotlinJvmOptions
                    // force the compilation at 1.8 as it may target Android platform
                    this.jvmTarget = "1.8"
                    this.freeCompilerArgs = listOf("-Xjsr305=strict")
                }
            }
        }
    }

    private fun addDependencies(project: Project, exts: LibGDXExtensions) {
        val version = exts.version.get()

        project.repositories.mavenCentral()

        project.dependencies.add("implementation", project.dependencies.project(mapOf("path" to ":core")))
        project.dependencies.add("implementation", "com.badlogicgames.gdx:gdx-backend-lwjgl:$version")
        project.dependencies.add("implementation", "com.badlogicgames.gdx:gdx-platform:$version:natives-desktop")
        project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib")
    }

    private fun addOpenJdkDownloadTask(project: Project, exts: LibGDXExtensions) {
        project.apply { it.plugin("fi.linuxbox.download") }

        project.tasks.create("open-jdk-mac", Download::class.java) { dl ->
            dl.group = "packr"
            dl.to(project.file("build/open-jdk/open-jdk-mac.zip"))
            dl.from(exts.openJdk.macOSX.toString())
        }

        project.tasks.create("open-jdk-windows", Download::class.java) { dl ->
            dl.group = "packr"
            dl.to(project.file("build/open-jdk/open-jdk-windows.zip"))
            dl.from(exts.openJdk.windows.toString())
        }

        project.tasks.create("open-jdk-linux", Download::class.java) { dl ->
            dl.group = "packr"
            dl.to(project.file("build/open-jdk/open-jdk-linux.zip"))
            dl.from(exts.openJdk.linux.toString())
        }
    }

    private fun addPackr(project: Project, exts: LibGDXExtensions) {
        project.apply { it.plugin("com.github.dwursteisen.libgdx.packr.PackrPlugin") }
        project.extensions.configure<NamedDomainObjectContainer<PackrPluginExtension>>("packr") { container ->
            val mainClass = exts.mainClass.get()
            mainClass.forEach { m ->
                val className = m
                container.create("macosx-$className") {
                    it.outputDir.set(project.buildDir.resolve("packr/macosx/${project.rootProject.name}.app"))
                    it.bundleIdentifier.set(project.rootProject.name)
                    it.jdk.set(project.buildDir.resolve("open-jdk/open-jdk-mac.zip").absolutePath)
                    it.mainClass.set(m)
                    it.classpath.set(project.tasks.getByPath("dist").outputs.files.singleFile)
                }

                container.create("windows-$className") {
                    it.outputDir.set(project.buildDir.resolve("packr/windows"))
                    it.jdk.set(project.buildDir.resolve("open-jdk/open-jdk-windows.zip").absolutePath)
                    it.mainClass.set(m)
                    it.classpath.set(project.tasks.getByPath("dist").outputs.files.singleFile)
                }

                container.create("linux-$className") {
                    it.outputDir.set(project.buildDir.resolve("packr/linux"))
                    it.jdk.set(project.buildDir.resolve("open-jdk/open-jdk-linux.zip").absolutePath)
                    it.mainClass.set(m)
                    it.classpath.set(project.tasks.getByPath("dist").outputs.files.singleFile)
                }
                project.afterEvaluate {
                    it.tasks.getByName("macosx-${className}Packr").dependsOn("dist", "open-jdk-mac")
                    it.tasks.getByName("windows-${className}Packr").dependsOn("dist", "open-jdk-windows")
                    it.tasks.getByName("linux-${className}Packr").dependsOn("dist", "open-jdk-linux")
                }
            }
        }
    }

    private fun addRunTask(project: Project, sourceSets: SourceSetContainer, exts: LibGDXExtensions) {
        val mainClasses = if (exts.mainClass.get().isEmpty()) {
            project.tryFindMainClass()
        } else {
            exts.mainClass.get()
        }

        mainClasses.forEach { mainClass ->
            val className = mainClass.split(".").last()
            project.tasks.create("run-$className", JavaExec::class.java) { exec ->
                exec.group = "libgdx"
                exec.doFirst {
                    it as JavaExec
                    it.main = mainClass
                    it.classpath = sourceSets.getByName("main").runtimeClasspath
                    it.standardInput = System.`in`
                    it.workingDir = exts.assetsDirectory.orNull ?: project.tryFindAssetsDirectory()!!
                }
            }
        }
    }

    private fun addDistTask(project: Project, sourceSets: SourceSetContainer, exts: LibGDXExtensions) {
        val mainClasses = if (exts.mainClass.get().isEmpty()) {
            project.tryFindMainClass()
        } else {
            exts.mainClass.get()
        }

        mainClasses.forEach { mainClass ->
            val className = mainClass.last()
            val dist = project.tasks.create("dist-$className", Jar::class.java) { jar ->
                jar.group = "libgdx"

                jar.archiveFileName.set("${project.rootProject.name}-desktop.jar")

                jar.doFirst {
                    jar.from(project.files(sourceSets.getByName("main").output.classesDirs))
                    jar.from(project.files(sourceSets.getByName("main").output.resourcesDir))

                    val files = project.files(sourceSets.getByName("main").runtimeClasspath)
                    jar.from(files.filter { f -> f.exists() }.map { f -> if (f.isDirectory) f else project.zipTree(f) })

                    jar.from(project.files(exts.assetsDirectory.orNull ?: project.tryFindAssetsDirectory()))
                    jar.manifest { m -> m.attributes(mapOf("Main-Class" to (mainClass))) }
                }
            }
            dist.dependsOn(project.tasks.getByName("classes"))
        }
    }
}
