package com.github.dwursteisen.libgdx.packr

import org.gradle.api.Plugin
import org.gradle.api.Project


class PackrPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("packr", PackrTask::class.java)
    }


}