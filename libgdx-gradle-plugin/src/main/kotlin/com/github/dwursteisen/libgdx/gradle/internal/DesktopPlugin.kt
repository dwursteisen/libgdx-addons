package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import com.github.dwursteisen.libgdx.gradle.internal.tasks.GenerateClassReference
import com.github.dwursteisen.libgdx.gradle.internal.tasks.GenerateCodeTask
import com.github.dwursteisen.libgdx.packr.PackrTask
import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class DesktopPlugin(private val exts: LibGDXExtensions) : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(DesktopPlugin::class.java)

    override fun apply(project: Project) {
        // Add Java gradle to access sourceSets extensions
        project.apply { it.plugin("org.gradle.java") }
        project.apply { it.plugin("org.jetbrains.kotlin.jvm") }
        project.apply { it.plugin("org.jetbrains.gradle.plugin.idea-ext") }

        logger.info("Detecting Desktop module. Will add custom tasks: dist and run.")
        exts.mainClass = exts.mainClass ?: tryFindMainClass(project)
        exts.assetsDirectory = exts.assetsDirectory ?: project.tryFindAssetsDirectory()

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer

        addGenerateMainClass(project)
        addDependencies(project)
        addDistTask(project, sourceSets)
        addRunTask(project, sourceSets)
        addOpenJdkDownloadTask(project)
        addPackr(project)

        setupKotlin(project)
    }

    private fun addGenerateMainClass(project: Project) {
        val ref = project.tasks.create("generate-desktop-main-ref", GenerateClassReference::class.java) {
            it.group = "libgdx"
            it.mainClassFile = exts.mainClass?.replace(".", "/").let { f -> project.file("$f.kt") }
                ?: project.file("src/main/kotlin/libgdx/MainClass.kt")
            it.mainClassReference = File(project.buildDir, "generated/main-class-reference.txt")
        }

        val gen = project.tasks.create("generate-desktop-main", GenerateCodeTask::class.java) {
            it.group = "libgdx"
            it.description = "Generate Default Main class"

            it.content = """
package libgdx

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration


    object MainClass {
        @JvmStatic
        fun main(args: Array<String>) {
            LwjglApplication(TODO("Replace With Your Game Class"), LwjglApplicationConfiguration().apply {
                width = 600
                height = 600
            })
        }
}
 """
            val classpath = exts.mainClass?.replace(".", "/")?.let { f -> "$f.kt" }
                ?: "src/main/kotlin/libgdx/MainClass.kt"
            it.mainClassFile = project.file(classpath)
            it.mainClassReference = File(project.buildDir, "generated/main-class-reference.txt")
        }

        project.tasks.getByName("classes").dependsOn("generate-desktop-main")
        gen.dependsOn(ref)
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

    private fun addDependencies(project: Project) {
        val version = exts.version

        project.repositories.mavenCentral()

        project.dependencies.add("implementation", project.dependencies.project(mapOf("path" to ":core")))
        project.dependencies.add("implementation", "com.badlogicgames.gdx:gdx-backend-lwjgl:$version")
        project.dependencies.add("implementation", "com.badlogicgames.gdx:gdx-platform:$version:natives-desktop")
        project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib")
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
        // TODO: Create extensions and apply the Packr plugin instead.
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
        return project.tryFindClassWhichMatch { lines ->
            lines.filter { line -> line.contains("fun main(") || line.contains("import com.badlogic.gdx.backends.lwjgl.LwjglApplication") }
                .count() >= 2
        }
    }
}
