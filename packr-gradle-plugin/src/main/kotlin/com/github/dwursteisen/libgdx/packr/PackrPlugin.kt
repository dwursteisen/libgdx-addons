package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.PackrConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.internal.reflect.Instantiator
import javax.inject.Inject

class PackrPlugin @Inject constructor(
    private val instantiator: Instantiator,
    private val callbackDecorator: CollectionCallbackActionDecorator) : Plugin<Project> {

    override fun apply(project: Project) {
        val exts = project.extensions.create(
            "packr",
            PackrPluginExtensionContainer::class.java,
            PackrPluginExtension::class.java,
            instantiator,
            callbackDecorator
        )

        exts.all { ext ->
            project.tasks.register(ext.name + "Packr", PackrTask::class.java) {
                it.platform = ext.platform
                it.jdk = ext.jdk
                it.executable = ext.executable
                it.mainClass = ext.mainClass
                it.vmArgs = ((ext.vmArgs ?: emptyList()) + macArgs()).toSet().toTypedArray()
                it.minimizeJre = ext.minimizeJre
                it.classpath = ext.classpath
                it.outputDir = ext.outputDir
                it.bundleIdentifier = ext.bundleIdentifier ?: tryDefaultBundleName(project, ext.platform)
                it.verbose = ext.verbose
            }
        }
    }

    private fun macArgs(): List<String> = listOf("-XstartOnFirstThread")

    private fun tryDefaultBundleName(project: Project, targetPlatform: PackrConfig.Platform?) = when (targetPlatform) {
        PackrConfig.Platform.MacOS -> project.name + ".app"
        else -> null
    }
}
