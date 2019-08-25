package com.github.dwursteisen.libgdx.aseprite

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.internal.reflect.Instantiator
import javax.inject.Inject

class AsepritePlugin @Inject constructor(
    private val instantiator: Instantiator,
    private val callbackDecorator: CollectionCallbackActionDecorator
) : Plugin<Project> {

    override fun apply(target: Project) {
        val exts = target.extensions.create(
            "aseprite",
            AsepritePluginExtensions::class.java,
            target,
            instantiator,
            callbackDecorator
        )

        exts.all { ext ->
            target.tasks.register(ext.name + "Aseprite", AsepriteTask::class.java) {
                it.exec.set(exts.exec.orNull)
                it.baseDirectory.set(ext.baseDirectory.orNull)
                it.inputFiles.set(ext.inputFiles.orNull)
                it.outputDirectory.set(ext.outputDirectory.orNull)
                it.outputFiles.set(ext.outputFiles.orNull)
                it.scale.set(ext.scale.orNull)
                it.format.set(ext.format.orNull)
                it.json.set(ext.json.orNull)
                it.verbose.set(ext.verbose.orNull)
                it.sheetPack.set(ext.sheetPack.orNull)
                it.sheetHeight.set(ext.sheetHeight.orNull)
                it.sheetWidth.set(ext.sheetWidth.orNull)
            }
        }
    }
}
