package com.github.dwursteisen.libgdx.graphics

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.TextureData
import com.badlogic.gdx.graphics.glutils.FileTextureData

/**
 * Refreshable texture.
 *
 * The texture can reload using the method `refresh`.
 *
 * It's very convenient when the texture needs to be updated without without reloading a game.
 * The purpose of this class is to be used mainly during development.
 *
 * For performance reason, this type should be avoided in production.
 *
 * [Texture] can be replaced automatically by this class by using the [com.badlogic.gdx.assets.AssetManager] and replacing
 * the texture load with [RefreshableTextureLoader]
 *
 */
class RefreshableTexture(texture: Texture) : Texture(texture.textureData) {

    private var lastUpdate: Long
    private val fileHandle: FileHandle

    init {
        val textureData = textureData
        fileHandle = when (textureData) {
            is FileTextureData -> textureData.fileHandle
            else -> throw IllegalArgumentException("Texture not created using FileHandler are not yet supported.")
        }
        lastUpdate = fileHandle.lastModified()
        TextureWatchDogs.register(this)
    }

    fun shouldRefresh(): Boolean = lastUpdate != fileHandle.lastModified()

    fun refresh() {
        dispose()
        load(TextureData.Factory.loadFromFile(fileHandle, null, false))
        lastUpdate = fileHandle.lastModified()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? RefreshableTexture)?.let {
            fileHandle == other.fileHandle
        } ?: false
    }

    override fun hashCode(): Int {
        return fileHandle.hashCode()
    }
}
