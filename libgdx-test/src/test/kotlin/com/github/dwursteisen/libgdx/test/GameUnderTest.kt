package com.github.dwursteisen.libgdx.test

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport


class GameUnderTest : ApplicationListener {

    private lateinit var shape: ShapeRenderer
    private val viewport = ExtendViewport(240f, 240f)

    private var size = 50f

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            size += 1f
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            size -= 1f
        }

        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.projectionMatrix = viewport.camera.combined
        shape.color = Color.BLUE
        shape.circle(0f, 0f, size)
        shape.end()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun create() {
        shape = ShapeRenderer()
    }

    override fun dispose() {

    }

}

fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.width = 240
    config.height = 240

    LwjglApplication(GameUnderTest(), config)
}