package com.github.dwursteisen.libgdx.gradle

import com.github.dwursteisen.libgdx.gradle.internal.tryFindAssetsDirectory
import com.github.dwursteisen.libgdx.gradle.internal.tryFindMainClass
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.RunConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LibGDXPlugin : Plugin<Project> {

    private val logger: Logger = LoggerFactory.getLogger(LibGDXPlugin::class.java)

    override fun apply(project: Project) {
        project.apply { it.plugin("com.github.dwursteisen.libgdx.template") }

        project.extensions.create("libgdx", LibGDXExtensions::class.java, project)

        if (project.subprojects.isEmpty()) {
            bootstrapProjects(project)
        } else {
            project.subprojects.forEach { sub ->
                when (sub.name) {
                    "core" -> sub.apply { it.plugin("com.github.dwursteisen.libgdx.core") }
                    "desktop" -> sub.apply { it.plugin("com.github.dwursteisen.libgdx.desktop") }
                    "android" -> sub.apply { it.plugin("com.github.dwursteisen.libgdx.android") }
                }
            }

            addIntelliJRunConfiguration(project)
        }
    }

    private fun bootstrapProjects(project: Project) {
        logger.info("No subprojects detected. Will bootstrap the project.")
        project.tasks.getByName("build").dependsOn("template-core", "template-desktop")
    }

    private fun addIntelliJRunConfiguration(
        project: Project
    ) {
        project.apply { it.plugin("org.jetbrains.gradle.plugin.idea-ext") }
        project.extensions.configure(LibGDXExtensions::class.java) { exts ->
            project.extensions.configure(IdeaModel::class.java) { ideaModel ->

                (ideaModel.project as ExtensionAware).extensions.configure(ProjectSettings::class.java) { projectSettings ->

                    (projectSettings as ExtensionAware).extensions.configure<NamedDomainObjectContainer<RunConfiguration>>(
                        "runConfigurations"
                    ) { runs ->
                        runs as PolymorphicDomainObjectContainer<RunConfiguration>
                        val configuredMainClasses = exts.mainClass.get()
                        val toConfigure = if (configuredMainClasses.isEmpty()) {
                            project.tryFindMainClass()
                        } else {
                            configuredMainClasses
                        }
                        toConfigure.forEach { mainClass ->

                            val className = mainClass.capitalize()

                            runs.create("🎮 Run ${project.name.capitalize()} ➡️ $className", Application::class.java) {
                                it.mainClass = mainClass

                                val assetsDirectory = exts.assetsDirectory.orNull ?: project.tryFindAssetsDirectory()
                                it.workingDirectory = assetsDirectory?.absolutePath

                                if (project.childProjects.containsKey("android")) {
                                    // the plugin android trigger some bugs in IntelliJ.
                                    it.moduleName = "${project.name}.desktop"
                                } else {
                                    it.moduleName = "${project.name}.desktop.main" // finger crossed
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
