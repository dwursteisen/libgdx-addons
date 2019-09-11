package com.github.dwursteisen.libgdx.template.gradle

import com.github.dwursteisen.libgdx.gradle.createProperty
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateModelTask : DefaultTask() {

    init {
        group = "libgdx"
        description = "Create the module $name using a template."
    }

    @OutputFile
    val module = project.createProperty<File>()

    @TaskAction
    fun generate() {
    }
}
