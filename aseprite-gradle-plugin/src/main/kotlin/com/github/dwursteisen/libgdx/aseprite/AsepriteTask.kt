package com.github.dwursteisen.libgdx.aseprite

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
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

    var baseDirectory: File? = null

    @OutputFiles
    var outputFiles: FileCollection? = null
        get() {
            if (outputDirectory == null) {
                return null
            }
            val inp = inputFiles?.map {
                val base = baseDirectory
                if (base != null) {
                    val prefix = it.absolutePath.replace(base.absolutePath, "").replaceAfterLast("/", "")
                    File(outputDirectory?.absolutePath + prefix + "/" + it.nameWithoutExtension + ".png")
                } else {
                    File(outputDirectory?.absolutePath + "/" + it.nameWithoutExtension + ".png")
                }
            }
            return project.files(inp)
        }
        private set

    var scale: Double = 1.0

    var format: AsepriteFormat = AsepriteFormat.HASH

    var json: Boolean = true

    var verbose: Boolean = false

    var sheet_pack: Boolean = true

    var sheet_height: Int? = null
    var sheet_width: Int? = null

    @TaskAction
    fun export() {
        val exec = getExecActionFactory().newExecAction()

        val exts = project.extensions.getByType(AsepritePluginExtentions::class.java)
        val aseprite = exts.exec ?: invalideAsepritePath()

        inputFiles?.files?.forEach { file ->
            logger.info("Will run Aseprite to process $file")
            exec.commandLine = args(aseprite, file)
            val result = exec.execute()
            result.assertNormalExitValue()
        }
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
        args += input.absolutePath

        if (verbose) {
            args += "--verbose"
        }

        val base = baseDirectory
        val path = if (base != null) {
            val prefix = input.absolutePath.replace(base.absolutePath, "").replaceAfterLast("/", "")
            File(outputDirectory?.absolutePath + prefix + "/" + input.nameWithoutExtension)
        } else {
            File(outputDirectory?.absolutePath + "/" + input.nameWithoutExtension)
        }

        if (json) {
            args += "--data"
            args += path.absolutePath + ".json"
            args += "--format"
            args += format.format
            args += "--list-slices"
            args += "--list-tags"
        }


        if (scale != 1.0) {
            args += "--scale"
            args += scale.toString()
        }

        if (sheet_width != null) {
            args += "--sheet-width"
            args += "$sheet_width"
        }

        if (sheet_height != null) {
            args += "--sheet-height"
            args += "$sheet_height"

        }

        if (sheet_pack) {
            args += "--sheet-pack"
        }

        args += "--sheet"
        args += path.absolutePath + ".png"


        logger.debug("Aseprite command line : $args")
        return args
    }
}
