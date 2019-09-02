package com.github.dwursteisen.libgdx

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureRegionMap(private val split: Array<Array<TextureRegion>>) {
    operator fun get(column: Int, row: Int): TextureRegion {
        return split[row][column]
    }
}

class TextureSplitter(private val assetManager: AssetManager? = null) {
    fun split(texture: Texture, tileWidth: Int, tileHeight: Int): TextureRegionMap {
        return TextureRegionMap(TextureRegion.split(texture, tileWidth, tileHeight))
    }

    fun split(resourceName: String, tileWidth: Int, tileHeight: Int): TextureRegionMap {
        assetManager ?: throw IllegalStateException("The texture splitter doesn't have any assetManager." +
            "To split a resource from the asset manager, it needs to be pass during the construction of the " +
            "TextureSplitter: TextureSplitter(yourAssetManager).")
        val texture: Texture = assetManager[resourceName]
        return split(texture, tileWidth, tileHeight)
    }
}
