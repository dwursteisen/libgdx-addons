package com.github.dwursteisen.libgdx.assets

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

class AssetsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val exts = project.extensions.create("assets", AssetsPluginExtension::class.java)
        project.afterEvaluate {
            it.tasks.create("assets", AssetsTask::class.java) { task ->
                task.files = exts.assetsDirectory
                task.output = exts.assetsClass
            }
        }

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        sourceSets.getByName("main") {
            it.java.srcDir(File(project.buildDir, "generated"))
        }

    }

}