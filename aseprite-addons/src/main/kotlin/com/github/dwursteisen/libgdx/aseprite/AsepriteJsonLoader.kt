package com.github.dwursteisen.libgdx.aseprite

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json

class AsepriteJsonParameter : AssetLoaderParameters<AsepriteJson>()
class AsepriteJsonLoader(resoler: FileHandleResolver) : AsynchronousAssetLoader<AsepriteJson, AsepriteJsonParameter>(resoler) {

    private val json = Json().apply {
        setIgnoreUnknownFields(true)
    }

    private var data: AsepriteJson? = null

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameter: AsepriteJsonParameter?): AsepriteJson? {
        val copy = data
        data = null
        return copy
    }

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: AsepriteJsonParameter?): Array<AssetDescriptor<Any>>? = null

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: AsepriteJsonParameter?) {
        data = json.fromJson(file)
    }

}