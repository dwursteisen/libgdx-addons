package com.github.dwursteisen.libgdx.template.gradle

import com.github.dwursteisen.libgdx.gradle.createProperty
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

open class GenerateModelTask : DefaultTask() {

    init {
        group = "libgdx"
        description = "Create the module $name using a template."
    }

    @OutputFile
    val lock = project.createProperty<File>()

    val targetDirectory = project.createProperty<File>()

    val template = project.createProperty<Templates>()

    @TaskAction
    fun generate() {
        val pluginJar = File(GenerateModelTask::class.java.protectionDomain.codeSource.location.toURI()).path

        val target = targetDirectory.get()
        if (!target.exists()) {
            target.mkdirs()
        } else {
            logger.warn("""
Not running the template plugin for the module ${template.get().name} as the directory ${target.absolutePath}
 is already existing. To avoid removing the current resources, the plugin will now just stop.
  
  To run again the templating, delete the directory.
""".trimIndent())
            return
        }

        // select and read files
        ZipInputStream(FileInputStream(pluginJar)).use { zip ->
            var entry: ZipEntry? = zip.nextEntry
            while (entry != null) {
                if (entry.name.startsWith(template.get().templateFolder)) {

                    if (entry.size != 0L) {
                        val content = ByteArray(entry.size.toInt())
                        zip.read(content)

                        val resolvedName = entry.name.removePrefix(template.get().templateFolder)

                        // copy content into target directory
                        val newFile = target.resolve(resolvedName)
                        if (!newFile.parentFile.exists()) {
                            newFile.parentFile.mkdirs()
                        }
                        newFile.createNewFile()
                        newFile.appendBytes(content)
                    }
                }
                entry = zip.nextEntry
            }
        }
    }
}
