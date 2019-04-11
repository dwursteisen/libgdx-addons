package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.Packr
import com.badlogicgames.packr.PackrConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

open class PackrTask : DefaultTask() {

    init {
        group = "distribution"
        description = "Package your application with a bundled JRE"
    }

    var platform: PackrConfig.Platform? = null
    var jdk: String? = null
    var executable: String? = null

    var mainClass: String? = null
    var vmArgs: Array<String>? = null
    var minimizeJre: String? = null

    @InputFile
    var classpath: File? = null

    @OutputDirectory
    var outputDir: File? = null

    var bundleIdentifier: String? = null

    var verbose = false

    @TaskAction
    fun packageIt(inputs: IncrementalTaskInputs) {

        val config = PackrConfig()
        config.platform = this.platform ?: findCurrentSystem()
        config.jdk = this.jdk ?: currentJavaHome()
        config.executable = this.executable ?: project.name
        config.classpath = this.classpath?.let { listOf(it.absolutePath) } ?: invalidClasspath()

        config.mainClass = this.mainClass

        config.vmArgs = (this.vmArgs ?: emptyArray()).toList()

        config.minimizeJre = this.minimizeJre

        config.bundleIdentifier = if (config.platform == PackrConfig.Platform.MacOS) {
            this.bundleIdentifier ?: invalidBundle()
        } else {
            this.bundleIdentifier
        }
        config.verbose = this.verbose

        val outputDirPath = this.outputDir ?: "build/packr-out"

        config.outDir = project.file(outputDirPath)

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