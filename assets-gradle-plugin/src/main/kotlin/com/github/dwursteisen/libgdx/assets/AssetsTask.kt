package com.github.dwursteisen.libgdx.assets

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
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
    var files: FileCollection? = null

    @OutputFile
    var output: File? = null

    @TaskAction
    fun generate() {

        val objectName = output?.nameWithoutExtension ?: expected("Output file name")
        val file = FileSpec.builder("", objectName)
        val builder = TypeSpec.objectBuilder(objectName)

        files?.files?.forEach {
            val base = if (it.isDirectory) it else it.parentFile
            appendDirectory(it, base, builder)
        }

        file.addType(builder.build())
        file.build().writeTo(output?.parentFile ?: expected("Output file name"))

    }

    private fun appendDirectory(current: File, base: File, builder: TypeSpec.Builder, prefix: String = "") {
        if (current.isDirectory) {
            current.listFiles().forEach {
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

    private fun expected(message: String): Nothing = throw GradleException(message)
}