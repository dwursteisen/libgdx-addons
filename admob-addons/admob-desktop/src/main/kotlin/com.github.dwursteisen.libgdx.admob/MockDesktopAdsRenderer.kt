package com.github.dwursteisen.libgdx.admob

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ScreenViewport

/**
 * Created by david on 16/10/2017.
 */



internal class MockDesktopAdsRenderer(val delegate: ApplicationListener) : ApplicationListener {

    private lateinit var batch: ShapeRenderer
    private val viewport: ScreenViewport = ScreenViewport()

    private var query: AdsQuery? = null

    override fun render() {
        delegate.render()
        query?.let {

            val xx = if(it.flags and CENTER == CENTER) {
                (viewport.screenWidth - it.format.size.x) * 0.5f
            } else if(it.flags and RIGHT == RIGHT) {
                viewport.screenWidth - it.format.size.x
            } else {
                0f
            }

            val yy = if(it.flags and UP == UP) {
                viewport.screenHeight - it.format.size.y
            } else {
                0f
            }

            batch.begin(ShapeRenderer.ShapeType.Filled)
            batch.projectionMatrix = viewport.camera.combined
            batch.color = Color.WHITE
            // TODO: draw an fake ads
            batch.rect(xx, yy, it.format.size.x, it.format.size.y)
            batch.end()
        }


    }

    override fun pause() {
        delegate.pause()
    }

    override fun resume() {
        delegate.resume()
    }

    override fun resize(width: Int, height: Int) {
        val w = Gdx.graphics.width * 0.5f
        val h = Gdx.graphics.height * 0.5f

        viewport.update(width, height)
        viewport.camera.position.set(w, h, 0f)
        viewport.camera.update()
        delegate.resize(width, height)
    }

    override fun create() {
        batch = ShapeRenderer()

        delegate.create()

    }

    override fun dispose() {
        delegate.dispose()
    }

    fun load(query: AdsQuery) {
        this.query = query
    }

    fun close(query: AdsQuery) {
        this.query = null
    }

}