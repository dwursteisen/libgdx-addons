package com.github.dwursteisen.libgdx.admob

import com.badlogic.gdx.math.Vector2

class AdsSize private constructor(val size: Vector2) {

    companion object {
        val BANNER = AdsSize(Vector2(320f, 50f))
        val LARGE_BANNER = AdsSize(Vector2(320f, 100f))
        val MEDIUM_RECTANGLE = AdsSize(Vector2(300f, 250f))
        val FULL_BANNER = AdsSize(Vector2(468f, 60f))
        val LEADERBOARD = AdsSize(Vector2(728f, 90f))
    }
}