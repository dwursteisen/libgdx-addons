package com.github.dwursteisen.libgdx.aseprite

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

class Aseprite(val texture: Texture, val json: AsepriteJson) {

    private val animationCache: Map<String, Animation<TextureRegion>>


    init {
        animationCache = toAnimation()
    }

    operator fun get(key: String): Animation<TextureRegion> = animationCache[key] ?: TODO(
            "Wrong animation name: $key. Expected: ${animationCache.keys.joinToString(",")}"
    )


    fun slices(name: String) = json.meta.slices.firstOrNull { it.name == name } ?: invalidSlice(name)

    private fun invalidSlice(name: String): Nothing = TODO("Invalid slice name $name. Other candidates : ${json.meta.slices.map { it.name }}")

    private fun toAnimation(): Map<String, Animation<TextureRegion>> {

        val spriteDef: AsepriteJson = json
        val texture: Texture = texture

        // all frame should have the same size
        val frameData = spriteDef.asFrameIndexedMap()
        val size = frameData.entries.map { it.value.sourceSize }
                .first()

        val splitted = TextureRegion.split(texture, size.w, size.h)


        return spriteDef.meta.frameTags.map {
            // trouver denominateur commun
            val keys = frameData.filterKeys { key -> key in it.from..it.to }
            val allDurations = keys.map { it.value.duration }.sorted()

            val denominateur = if (allDurations.distinct().size > 1) {
                val (a, b) = allDurations.distinct()
                a.gcd(b)
            } else {
                1
            }

            // sum des durations / denominateur = nb image
            val tmp = com.badlogic.gdx.utils.Array<TextureRegion>(allDurations.sum() / denominateur)
            for (index in it.from..it.to) {
                val duration = frameData[index]?.duration ?: 0
                val (x, y) = splitedIndex(index, splitted)
                for (nbCopie in 1..duration / denominateur) {
                    tmp.add(splitted[x][y])
                }
            }
            val direction = when (it.direction) {
                "forward" -> {
                    if (it.name.endsWith("_nr")) {
                        Animation.PlayMode.NORMAL
                    } else {
                        Animation.PlayMode.LOOP
                    }
                }
                "pingpong" -> Animation.PlayMode.LOOP_PINGPONG
                else -> TODO("compute other play mode")
            }
            val animation = Animation(denominateur / 1000f, tmp, direction)
            it.name to animation
        }.toMap()
    }

    fun splitedIndex(index: Int, splitted: kotlin.Array<kotlin.Array<TextureRegion>>): Pair<Int, Int> {
        val x = ((index - index % splitted[0].size) / splitted[0].size)
        val y = index % splitted[0].size
        return Pair(x, y)
    }

    fun frame(i: Int): TextureRegion {
        val frameData = json.asFrameIndexedMap()
        val size = frameData.entries.map { it.value.sourceSize }
                .first()

        val splitted = TextureRegion.split(texture, size.w, size.h)
        val (x, y) = splitedIndex(i, splitted)
        return splitted[x][y]
    }
}


class AsepriteParameter : AssetLoaderParameters<Aseprite>()

class AsepriteLoader(resoler: FileHandleResolver) : AsynchronousAssetLoader<Aseprite, AsepriteParameter>(resoler) {

    private var data: Aseprite? = null

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle, parameter: AsepriteParameter?): Aseprite? {
        val copy = data
        data = null
        return copy
    }

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: AsepriteParameter?): Array<AssetDescriptor<out Any>>? {
        val result = Array<AssetDescriptor<out Any>>(2)
        result.add(AssetDescriptor(file?.pathWithoutExtension() + ".json", AsepriteJson::class.java))
        result.add(AssetDescriptor(file?.pathWithoutExtension() + ".png", Texture::class.java))
        return result
    }

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: AsepriteParameter?) {
        val texture: Texture = manager[file.pathWithoutExtension() + ".png"]
        val json: AsepriteJson = manager[file.pathWithoutExtension() + ".json"]
        data = Aseprite(texture, json)
    }

}