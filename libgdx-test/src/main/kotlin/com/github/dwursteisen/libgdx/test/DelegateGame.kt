package com.github.dwursteisen.libgdx.test

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.input.RemoteInput
import java.util.concurrent.CountDownLatch


internal class DelegateGame(val d: ApplicationListener, val beforeLatch: CountDownLatch, val afterLatch: CountDownLatch) : ApplicationListener {
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

    override fun create() {
        Gdx.input = RemoteInput()
        beforeLatch.countDown()
        d.create()
        afterLatch.countDown()
    }

    override fun dispose() {
        d.dispose()
    }

}