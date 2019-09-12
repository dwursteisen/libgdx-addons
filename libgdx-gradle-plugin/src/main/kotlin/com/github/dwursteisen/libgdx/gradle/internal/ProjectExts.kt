package com.github.dwursteisen.libgdx.gradle.internal

import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

fun Project.tryFindAssetsDirectory(): File? {
    return project.rootDir.resolve("core/src/main/assets")
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

fun Project.tryFindMainClass(): String? {
    return project.subprojects.firstOrNull { it.name == "desktop" }?.tryFindClassWhichMatch { lines ->
        lines.filter { line -> line.contains("fun main(") || line.contains("import com.badlogic.gdx.backends.lwjgl.LwjglApplication") }
            .count() >= 2
    }
}

fun Project.tryFindAndroidMainClass(): String? {
    return project.subprojects.firstOrNull { it.name == "android" }?.tryFindClassWhichMatch { lines ->
        lines.filter { line ->
            line.contains(": AndroidApplication()") || line.contains("import com.badlogic.gdx.backends.android.AndroidApplication")
        }
            .count() >= 2
    }
}

