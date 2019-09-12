package com.github.dwursteisen.libgdx.template.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class TemplatePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("template-core", GenerateModelTask::class.java) {
            it.template.set(Templates.CORE)
            it.lock.set(project.buildDir.resolve("templates").resolve("desktop.template"))
            it.targetDirectory.set(project.projectDir
                .resolve("core")
                .resolve("src")
                .resolve("main")
            )
        }
        project.tasks.create("template-desktop", GenerateModelTask::class.java) {
            it.template.set(Templates.DESKTOP)
            it.lock.set(project.buildDir.resolve("templates").resolve("desktop.template"))
            it.targetDirectory.set(project.projectDir
                .resolve("desktop")
                .resolve("src")
                .resolve("main")
            )
        }
        project.tasks.create("template-android", GenerateModelTask::class.java) {
            it.template.set(Templates.ANDROID)
            it.lock.set(project.buildDir.resolve("templates").resolve("desktop.template"))
            it.targetDirectory.set(project.projectDir
                .resolve("android")
                .resolve("src")
                .resolve("main")
            )
        }
    }
}
