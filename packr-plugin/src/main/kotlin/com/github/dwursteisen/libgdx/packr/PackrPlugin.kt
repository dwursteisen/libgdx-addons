package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.Packr
import com.badlogicgames.packr.PackrConfig
import org.gradle.api.Plugin
import org.gradle.api.Project


class PackrPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.extensions.create("packr", PacerPluginExtensions::class.java)
        val task = project.task("packr")
        task.group = "distribution"
        task.description = "Package your application with a bundled JRE"

        task.dependsOn("dist") // FIXME: should not depends like this

        task.doLast {

            val exts: PacerPluginExtensions = project.extensions.getByType(PacerPluginExtensions::class.java)

            val config = PackrConfig()
            config.platform = exts.platform ?: findCurrentSystem()
            config.jdk = exts.jdk ?: currentJavaHome()
            config.executable = exts.executable ?: project.name
            config.classpath = (exts.classpath ?: emptyArray<String>()).toList()

            config.mainClass = exts.mainClass

            config.vmArgs = (exts.vmArgs ?: emptyArray()).toList()

            config.minimizeJre = exts.minimizeJre

            config.bundleIdentifier = exts.bundleIdentifier

            config.verbose = true

            config.outDir = if (exts.outputDir != null) {
                project.file(exts.outputDir)
            } else {
                project.file("packr-out")
            }

            Packr().pack(config)
        }
    }

    private fun findCurrentSystem(): PackrConfig.Platform {
        val osName = System.getProperty("os.name").toLowerCase()
        if (osName.contains("win")) {
            return PackrConfig.Platform.Windows64
        } else if (osName.contains("mac")) {
            return PackrConfig.Platform.MacOS
        } else if (osName.contains("nux") || osName.contains("nix")) {
            return PackrConfig.Platform.Linux64
        }
        TODO("Current OS type not found. Please configure it using the plugin configuration")
    }

    private fun noJdkFound(): Nothing = TODO("""No zipped JDK configured.
I try $\JAVA_HOME but found nothing !
Please specify one JDK. It can be your current JDK too.

packr {
   ...
   jdk = "yourJRE.zip"
}
""")

    private fun currentJavaHome(): String = System.getenv("JAVA_HOME") ?: noJdkFound()

}