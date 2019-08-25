package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.Packr
import com.badlogicgames.packr.PackrConfig
import com.github.dwursteisen.libgdx.gradle.createProperty
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PackrTask : DefaultTask() {

    init {
        group = "distribution"
        description = "Package your application with a bundled JRE"
    }

    val platform = project.createProperty<PackrConfig.Platform>()
    val jdk = project.createProperty<String>()
    val executable = project.createProperty<String>()

    val mainClass = project.createProperty<String>()
    val vmArgs = project.createProperty<List<String>>()
    val minimizeJre = project.createProperty<String>()

    @InputFile
    val classpath = project.createProperty<File>()

    @OutputDirectory
    val outputDir = project.createProperty<File>()

    val bundleIdentifier = project.createProperty<String>()

    val verbose = project.createProperty<Boolean>()

    @TaskAction
    fun packageIt() {
        val config = PackrConfig()
        config.platform = this.platform.orNull ?: findCurrentSystem()
        config.jdk = this.jdk.orNull ?: currentJavaHome()
        config.executable = this.executable.orNull ?: project.name
        config.classpath = this.classpath.map { listOf(it.absolutePath) }.orNull ?: invalidClasspath()

        config.mainClass = this.mainClass.get()

        config.vmArgs = this.vmArgs.getOrElse(emptyList())

        config.minimizeJre = this.minimizeJre.get()

        config.bundleIdentifier = if (config.platform == PackrConfig.Platform.MacOS) {
            this.bundleIdentifier.orNull ?: invalidBundle()
        } else {
            this.bundleIdentifier.orNull
        }
        config.verbose = this.verbose.getOrElse(false)

        config.outDir = this.outputDir.getOrElse(project.buildDir.resolve("packr-out"))

        Packr().pack(config)
    }

    private fun invalidBundle(): Nothing {
        TODO("No valid bundle found for Mac OS application. Please specify a bundle.")
    }

    private fun invalidClasspath(): Nothing {
        TODO("No JAR to package found. Please specity a bundled JAR to include it in the packed application")
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
        TODO("Current OS type not found. Please configure it using the task configuration")
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
