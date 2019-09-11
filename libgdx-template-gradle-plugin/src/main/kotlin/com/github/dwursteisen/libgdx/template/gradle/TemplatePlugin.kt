package com.github.dwursteisen.libgdx.template.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class TemplatePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("template-core", GenerateModelTask::class.java)
        project.tasks.create("template-desktop", GenerateModelTask::class.java)
        project.tasks.create("template-android", GenerateModelTask::class.java)
    }
}
