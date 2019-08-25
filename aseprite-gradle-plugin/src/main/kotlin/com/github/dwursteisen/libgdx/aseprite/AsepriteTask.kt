package com.github.dwursteisen.libgdx.aseprite

import com.github.dwursteisen.libgdx.gradle.createProperty
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecActionFactory
import java.io.File
import javax.inject.Inject

open class AsepriteTask @Inject constructor(
    private val execActionFactory: ExecActionFactory
) : DefaultTask() {

    val exec = project.createProperty<File>()
        .value(project.properties["aseprite.exec"]?.let { File(it.toString()) })

    @InputFiles
    @SkipWhenEmpty
    val inputFiles = project.createProperty<FileCollection>()

    val outputDirectory = project.createProperty<File>()

    val baseDirectory = project.createProperty<File>()

    @OutputFiles
    val outputFiles = project.createProperty<FileCollection>()

    val scale = project.createProperty<Double>().value(1.0)

    val format = project.createProperty<AsepriteFormat>().value(AsepriteFormat.HASH)

    val json = project.createProperty<Boolean>().value(true)

    val verbose = project.createProperty<Boolean>().value(false)

    val sheetPack = project.createProperty<Boolean>().value(true)

    val sheetHeight = project.createProperty<Int>()
    val sheetWidth = project.createProperty<Int>()

    @TaskAction
    fun export() {
        val aseprite = this.exec.orNull ?: invalideAsepritePath()

        val exec = execActionFactory.newExecAction()

        inputFiles.get().files.forEach { file ->
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

        if (verbose.get()) {
            args += "--verbose"
        }

        val base = baseDirectory.orNull
        val path = if (base != null) {
            val prefix = input.absolutePath.replace(base.absolutePath, "").replaceAfterLast("/", "")
            File(outputDirectory.get().absolutePath + prefix + "/" + input.nameWithoutExtension)
        } else {
            File(outputDirectory.get().absolutePath + "/" + input.nameWithoutExtension)
        }

        if (json.get()) {
            args += "--data"
            args += path.absolutePath + ".json"
            args += "--format"
            args += format.get().format
            args += "--list-slices"
            args += "--list-tags"
        }


        if (scale.get() != 1.0) {
            args += "--scale"
            args += scale.get().toString()
        }

        if (sheetWidth.isPresent) {
            args += "--sheet-width"
            args += "${sheetWidth.get()}"
        }

        if (sheetHeight.isPresent) {
            args += "--sheet-height"
            args += "${sheetHeight.get()}"
        }

        if (sheetPack.get()) {
            args += "--sheet-pack"
        }

        args += "--sheet"
        args += path.absolutePath + ".png"

        logger.debug("Aseprite command line : $args")
        return args
    }
}
