package com.github.dwursteisen.libgdx.gradle.internal

import com.android.build.gradle.AppExtension
import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import java.io.File

class AndroidPlugin(private val exts: LibGDXExtensions) : Plugin<Project> {
    override fun apply(project: Project) {
            project.apply { it.plugin("android") }
            project.apply { it.plugin("kotlin-android") }

            setupMainClass(project, exts)
            addDefaultLibraries(project)
            setupAndroidExtension(project)
            createNativesLibCopyTasks(project)
    }

    private fun setupMainClass(project: Project, exts: LibGDXExtensions) {
        project.beforeEvaluate {
            exts.androidMainClass =
                exts.androidMainClass ?: tryFindMainClass(project) ?: createMainClass() ?: tryFindMainClass(project)
        }
    }

    private fun tryFindMainClass(project: Project): String? {
        return project.tryFindClassWhichMatch { lines ->
            lines.filter { line ->
                line.contains(": AndroidApplication()") || line.contains("import com.badlogic.gdx.backends.android.AndroidApplication")
            }
                .count() >= 2
        }
    }

    private fun createMainClass(): String {
        // TODO: 1- create a String with all Kotlin code
        // TODO: 2- write this String on the disk
        // TODO: 3- Return the path of this new file.
        // TODO("return path of the nerwly created class")
        return ""
    }

    private fun addDefaultLibraries(project: Project) {
        project.beforeEvaluate {
            val version = exts.version
            project.configurations.create("natives")
            project.dependencies.add("implementation", project.dependencies.project(mapOf("path" to ":core")))
            project.dependencies.add("implementation", "com.badlogicgames.gdx:gdx-backend-android:$version")
            project.dependencies.add("natives", "com.badlogicgames.gdx:gdx-platform:$version:natives-armeabi")
            project.dependencies.add("natives", "com.badlogicgames.gdx:gdx-platform:$version:natives-armeabi-v7a")
            project.dependencies.add("natives", "com.badlogicgames.gdx:gdx-platform:$version:natives-x86")
        }
    }

    private fun setupAndroidExtension(project: Project) {
        val androidExts = project.extensions.getByName("android") as AppExtension
        project.beforeEvaluate {
            androidExts.compileSdkVersion = androidExts.compileSdkVersion ?: "android-28"
            androidExts.defaultConfig.minSdkVersion(21)
            androidExts.defaultConfig.targetSdkVersion(28)
            androidExts.sourceSets.getByName("main") {
                it.java.srcDirs("src/main/kotlin")
                it.assets.srcDirs(exts.assetsDirectory)
            }
        }
    }

    private fun createNativesLibCopyTasks(project: Project) {
        project.afterEvaluate {
            val files = project.configurations.getByName("natives").files
                .groupBy { it.name.replace("gdx-platform-([0-9]*\\.[0-9]*\\.[0-9]*-)".toRegex(), "") }

            copyLib(
                project,
                "armeabi",
                files.getValue("natives-armeabi.jar").first()
            )

            copyLib(
                project,
                "armeabi-v7a",
                files.getValue("natives-armeabi-v7a.jar").first()
            )

            copyLib(
                project,
                "x86",
                files.getValue("natives-x86.jar").first()
            )
        }

        project.tasks.whenTaskAdded { packageTask ->
            if (packageTask.name.contains("package")) {
                packageTask.dependsOn(
                    "copy-armeabi",
                    "copy-armeabi-v7a",
                    "copy-x86"
                )
            }
        }
    }

    private fun copyLib(
        project: Project,
        directoryName: String,
        nativeFile: File
    ) {
        project.tasks.create("copy-$directoryName", Copy::class.java) {
            it.group = "libgdx"
            it.description = "Copy natives *.so files from ${nativeFile.name}"

            it.from(project.zipTree(nativeFile as Any))
            it.into(project.file("src/main/jniLibs/$directoryName"))
            it.include("*.so")
        }
    }
}
