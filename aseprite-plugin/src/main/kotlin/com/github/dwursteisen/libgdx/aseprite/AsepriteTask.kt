package com.github.dwursteisen.libgdx.aseprite

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.process.internal.ExecActionFactory
import java.io.File
import javax.inject.Inject

sealed class AsepriteFormat(val format: String) {

    object HASH : AsepriteFormat("json-hash")
    object ARRAY : AsepriteFormat("json-array")
}

open class AsepriteTask : DefaultTask() {


    @Inject
    open protected fun getExecActionFactory(): ExecActionFactory {
        throw UnsupportedOperationException()
    }

    @InputFiles
    @SkipWhenEmpty
    var inputFiles: FileCollection? = null

    var outputDirectory: File? = null

    @OutputFiles
    var outputFiles: FileCollection? = null
        get() {
            if (outputDirectory == null) {
                return null
            }
            val inp = inputFiles?.map { File(outputDirectory?.absolutePath + "/" + it.nameWithoutExtension + ".png") }
            return project.files(inp)
        }
        private set

    var scale: Int = 1

    var format: AsepriteFormat = AsepriteFormat.HASH

    var json: Boolean = true

    var verbose: Boolean = false

    @TaskAction
    fun export(input: IncrementalTaskInputs) {
        val exec = getExecActionFactory().newExecAction()

        val exts = project.extensions.getByType(AsepritePluginExtentions::class.java)
        val aseprite = exts.exec ?: invalideAsepritePath()

        inputFiles?.files?.forEach({ file ->
            logger.info("Will run Aseprite to process $file")
            exec.commandLine = args(aseprite, file)
            val result = exec.execute()
            result.assertNormalExitValue()
        })

    }

    private fun invalideAsepritePath(): Nothing {
        TODO("""Missing aseprite executable path.
Please configure it using aseprite.exec property (ie: in your ~/.gradle/gradle.properties)
aseprite.exec=<path to exec>

or using aseprite extension in your build.gradle

aseprite {
    exec=<path to exec>
}


MacOS specific : point to aseprite located into <aseprite directory>/Aseprite.app/Contents/MacOS/aseprite""")
    }

    private fun args(aseprite: File, input: File): List<String> {

        val exec = aseprite.absolutePath
        var args = listOf(exec, "-b")
        if (verbose) {
            args += "--verbose"
        }

        if (json) {
            args += "--data"
            args += outputDirectory?.absolutePath + "/" + input.nameWithoutExtension + ".json"
            args += "--format"
            args += format.format
        }

        args += "--scale"
        args += scale.toString()

        args += "--sheet"
        args += outputDirectory?.absolutePath + "/" + input.nameWithoutExtension + ".png"

        args += input.absolutePath

        logger.debug("Aseprite command line : $args")
        return args
    }
}