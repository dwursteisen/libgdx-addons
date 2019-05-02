package com.github.dwursteisen.libgdx.gradle.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateClassReference : DefaultTask() {

    @Input
    var mainClassFile: File? = null

    @OutputFile
    var mainClassReference: File? = null

    @TaskAction
    fun perform() {
        mainClassReference ?: return
        mainClassFile ?: return

        mainClassReference?.writeText(mainClassFile?.relativeTo(project.projectDir)?.path ?: "")
    }
}

open class GenerateCodeTask : DefaultTask() {

    @Input
    var mainClassReference: File? = null

    @OutputFile
    var mainClassFile: File? = null

    var content: String = ""

    @TaskAction
    fun perform() {
        mainClassReference ?: return
        mainClassFile ?: return

        if (mainClassFile?.exists() == true) return
        mainClassFile?.writeText(content)

    }
}
