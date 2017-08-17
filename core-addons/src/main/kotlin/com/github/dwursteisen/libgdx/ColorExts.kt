package com.github.dwursteisen.libgdx

import com.badlogic.gdx.graphics.Color


fun String.toColor(): Color {
    val code = if (this.startsWith("#")) {
        this.substring(1)
    } else {
        this
    }
    val r = Integer.parseInt(code.substring(0, 2), 16)
    val g = Integer.parseInt(code.substring(2, 4), 16)
    val b = Integer.parseInt(code.substring(4, 6), 16)
    return Color(r / 255f, g / 255f, b / 255f, 1f)
}