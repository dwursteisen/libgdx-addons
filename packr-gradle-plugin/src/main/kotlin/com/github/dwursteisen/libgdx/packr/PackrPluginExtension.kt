package com.github.dwursteisen.libgdx.packr

import com.badlogicgames.packr.PackrConfig
import org.gradle.api.Named
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.internal.reflect.Instantiator
import java.io.File

open class PackrPluginExtensionContainer(
    type: Class<PackrPluginExtension>,
    instantiator: Instantiator,
    callbackDecorator: CollectionCallbackActionDecorator
) : AbstractNamedDomainObjectContainer<PackrPluginExtension>(
    type, instantiator, callbackDecorator
) {

    override fun doCreate(name: String): PackrPluginExtension {
        return instantiator.newInstance<PackrPluginExtension>(PackrPluginExtension::class.java, name)
    }
}

open class PackrPluginExtension(private val name: String) : Named {
    override fun getName(): String = name

    var platform: PackrConfig.Platform? = null
    var jdk: String? = null
    var executable: String? = null

    var mainClass: String? = null
    var vmArgs: List<String>? = null
    var minimizeJre: String? = null

    var classpath: File? = null

    var outputDir: File? = null

    var bundleIdentifier: String? = null

    var verbose: Boolean = false
}
