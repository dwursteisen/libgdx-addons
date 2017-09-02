package com.github.dwursteisen.libgdx.test

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.input.RemoteInput
import java.util.concurrent.CountDownLatch


internal class DelegateGame(var d: ApplicationListener, var beforeLatch: CountDownLatch, var afterLatch: CountDownLatch) : ApplicationListener {
    override fun render() {
        d.render()
    }

    override fun pause() {
        d.pause()
    }

    override fun resume() {
        d.resume()
    }

    override fun resize(width: Int, height: Int) {
        d.resize(width, height)
    }

    private var alreadySetup = false

    override fun create() {
        if (!alreadySetup) {
            setupDelegate()
            alreadySetup = true
        }

        beforeLatch.countDown()
        d.create()
        afterLatch.countDown()
    }

    private fun setupDelegate() {
        Gdx.input = RemoteInput()
    }

    override fun dispose() {
        d.dispose()
    }

    fun restart(listener: ApplicationListener, before: CountDownLatch, after: CountDownLatch) {
        Gdx.app.postRunnable {
            d.dispose()
            before.countDown()
            listener.create()
            after.countDown()
            listener.resize(Gdx.graphics.width, Gdx.graphics.height)
            d = listener
        }

    }

}