package com.github.dwursteisen.libgdx.graphics

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture

/**
 * Loader for [AssetManager] to load [RefreshableTexture] instad of [Texture].
 *
 * Example of use:
 *
 * ```
 * assetManager.setLoader(Texture::class.java, RefreshableTextureLoader(InternalFileHandleResolver()))
 * ```
 *
 * After, all texture will be refreshable and so will be reloaded when a change on the file is detected.
 */
class RefreshableTextureLoader(resolver: FileHandleResolver) : TextureLoader(resolver) {
    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameter: TextureParameter?): Texture {
        return RefreshableTexture(super.loadSync(manager, fileName, file, parameter))
    }
}
