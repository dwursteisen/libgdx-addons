package com.github.dwursteisen.libgdx.admob

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration


class DesktopAdsManager(val config: LwjglApplicationConfiguration) : GdxAdsManager() {
    override fun close(query: AdsQuery) {
        delegate.close(query)
    }

    private lateinit var delegate: MockDesktopAdsRenderer

    override fun load(query: AdsQuery) {
        delegate.load(query)
    }

    override fun initialize(listener: ApplicationListener) {
        delegate = MockDesktopAdsRenderer(listener)
        LwjglApplication(delegate, config)
    }

}