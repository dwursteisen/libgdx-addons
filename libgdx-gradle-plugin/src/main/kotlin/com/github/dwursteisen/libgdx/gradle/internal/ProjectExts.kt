package com.github.dwursteisen.libgdx.gradle.internal

import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

fun Project.tryFindAssetsDirectory(): File? {
    val assetsDirectory = this.projectDir.listFiles { _, name -> name == "assets" }
    return assetsDirectory.firstOrNull()
}

fun Project.tryFindClassWhichMatch(filter: (Sequence<String>) -> Boolean): String? {
    val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
    val mainClassVisitor = MainClassVisitor(filter)
    val main = sourceSets.firstOrNull { it.name == "main" } ?: return null
    main.allSource
        .asFileTree
        .visit(mainClassVisitor)

    return mainClassVisitor.main
}

private class MainClassVisitor(val filter: (Sequence<String>) -> Boolean, var main: String? = null) : FileVisitor {

    override fun visitDir(dirDetails: FileVisitDetails) = Unit

    override fun visitFile(fileDetails: FileVisitDetails) {
        val use = fileDetails.open().use {
            filter.invoke(it.bufferedReader().lineSequence())
        }

        if (use) {
            main = fileDetails.path
                .replace(".kt", "")
                .replace("/", ".")
        }
    }
}

