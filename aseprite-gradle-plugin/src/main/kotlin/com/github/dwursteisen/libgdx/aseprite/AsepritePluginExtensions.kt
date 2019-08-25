package com.github.dwursteisen.libgdx.aseprite

import com.github.dwursteisen.libgdx.gradle.createProperty
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.AbstractValidatingNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.internal.reflect.Instantiator
import java.io.File

open class AsepriteExtension(private val name: String, project: Project) : Named {
    override fun getName(): String = name

    val inputFiles = project.createProperty<FileCollection>()

    val outputDirectory = project.createProperty<File>()

    val baseDirectory = project.createProperty<File>()

    val outputFiles = project.createProperty<FileCollection>()

    val scale = project.createProperty<Double>().value(1.0)

    val format = project.createProperty<AsepriteFormat>().value(AsepriteFormat.HASH)

    val json = project.createProperty<Boolean>().value(true)

    val verbose = project.createProperty<Boolean>().value(false)

    val sheetPack = project.createProperty<Boolean>().value(true)

    val sheetHeight = project.createProperty<Int>()
    val sheetWidth = project.createProperty<Int>()
}

open class AsepritePluginExtensions(
    private val project: Project,
    instantiator: Instantiator,
    callbackActionDecorator: CollectionCallbackActionDecorator
) : AbstractValidatingNamedDomainObjectContainer<AsepriteExtension>(
    AsepriteExtension::class.java,
    instantiator,
    callbackActionDecorator
) {

    override fun doCreate(name: String): AsepriteExtension {
        return AsepriteExtension(name, project)
    }

    val exec = project.createProperty<File>()
        .value(project.properties["aseprite.exec"]?.let { File(it.toString()) })
}
