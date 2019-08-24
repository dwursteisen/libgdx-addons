package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.PackrConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

class PackrPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val container = project.container(PackrPluginExtension::class.java) { name -> PackrPluginExtension(name, project) }

        project.extensions.add("packr", container)

        container.all { ext ->
            val taskName = ext.name + "Packr"
            project.tasks.register(taskName, PackrTask::class.java) {
                it.platform.set(ext.platform.orNull)
                it.jdk.set(ext.jdk.orNull)
                it.executable.set(ext.executable.orNull)
                it.mainClass.set(ext.mainClass.orNull)
                it.vmArgs.set(ext.vmArgs.getOrElse(emptyList()) + macArgs())
                it.minimizeJre.set(ext.minimizeJre.orNull)
                it.classpath.set(ext.classpath.orNull)
                it.outputDir.set(ext.outputDir.orNull)
                it.bundleIdentifier.set(ext.bundleIdentifier.orNull
                    ?: tryDefaultBundleName(project, ext.platform.orNull))
                it.verbose.set(ext.verbose.getOrElse(false))
            }
        }
    }

    private fun macArgs(): List<String> = listOf("-XstartOnFirstThread")

    private fun tryDefaultBundleName(project: Project, targetPlatform: PackrConfig.Platform?) = when (targetPlatform) {
        PackrConfig.Platform.MacOS -> project.name + ".app"
        else -> null
    }
}
