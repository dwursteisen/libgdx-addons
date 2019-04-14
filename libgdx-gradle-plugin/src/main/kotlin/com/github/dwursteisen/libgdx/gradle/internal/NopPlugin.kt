package com.github.dwursteisen.libgdx.gradle.internal

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NopPlugin : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(DesktopPlugin::class.java)

    override fun apply(project: Project) {
        logger.info("Skipping LibGDX configuration for project ${project.name}")
    }

}