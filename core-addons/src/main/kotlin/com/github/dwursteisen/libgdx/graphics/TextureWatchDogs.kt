package com.github.dwursteisen.libgdx.graphics

import com.badlogic.gdx.Gdx

object TextureWatchDogs {

    var enabled: Boolean = true

    private val references = mutableSetOf<RefreshableTexture>()

    private val watchDog = Thread {
        while (enabled) {
            val toRefresh = references.filter { it.shouldRefresh() }
            if (toRefresh.isNotEmpty()) {
                Gdx.app.postRunnable {
                    toRefresh.forEach { it.refresh() }
                }
            }
            Thread.sleep(100)
        }
    }

    init {
        start()
    }

    fun register(texture: RefreshableTexture) {
        references.add(texture)
    }

    fun start() {
        watchDog.start()
    }

    fun stop() {
        enabled = false
    }
}
