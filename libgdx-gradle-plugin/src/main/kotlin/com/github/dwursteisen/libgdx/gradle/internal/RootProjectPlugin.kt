package com.github.dwursteisen.libgdx.gradle.internal

import com.github.dwursteisen.libgdx.gradle.LibGDXExtensions
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.RunConfiguration
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile


class RootProjectPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        addIntelliJRunConfiguration(project)
    }

    private fun addIntelliJRunConfiguration(project: Project) {
        project.apply { it.plugin("org.jetbrains.gradle.plugin.idea-ext") }
        project.apply { it.plugin("com.github.dwursteisen.libgdx.template") }

        val exts = project.extensions.getByName("libgdx") as LibGDXExtensions

        project.extensions.configure(IdeaModel::class.java) { ideaModel ->

            (ideaModel.project as ExtensionAware).extensions.configure(ProjectSettings::class.java) { projectSettings ->

                (projectSettings as ExtensionAware).extensions.configure<NamedDomainObjectContainer<RunConfiguration>>("runConfigurations") { runs ->
                    runs as PolymorphicDomainObjectContainer<RunConfiguration>
                    runs.create("🎮 Run ${project.name.capitalize()} - Desktop", Application::class.java) {
                        it.mainClass = exts.mainClass
                        it.workingDirectory = exts.assetsDirectory?.absolutePath
                        if(project.childProjects.containsKey("android")) {
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
