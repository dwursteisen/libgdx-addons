package com.github.dwursteisen.libgdx.assets

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class AssetsTask : DefaultTask() {

    init {
        group = "libgdx"
        description = "Create static class referencing assets file name."
    }

    @InputFiles
    val assetsDirectory = project.createProperty<FileCollection>()

    @OutputFile
    val assetsClass = project.createProperty<File>()

    @TaskAction
    fun generate() {
        val file = FileSpec.builder("", assetsClass.get().nameWithoutExtension)
        val builder = TypeSpec.objectBuilder(assetsClass.get().nameWithoutExtension)

        assetsDirectory.get().files.forEach {
            val base = if (it.isDirectory) it else it.parentFile
            appendDirectory(it, base, builder)
        }

        file.addType(builder.build())
        file.build().writeTo(assetsClass.get().parentFile)
    }

    private fun appendDirectory(current: File, base: File, builder: TypeSpec.Builder, prefix: String = "") {
        if (current.isDirectory) {
            current.listFiles()?.forEach {
                appendDirectory(it, base, builder, prefix + current.nameWithoutExtension + "_")
            }
        } else {
            builder.addProperty(
                PropertySpec.builder(prefix + current.name.replace(".", "_"), String::class)
                    .initializer("%S", current.relativeTo(base).path)
                    .build()
            )
        }
    }
}
