package com.github.dwursteisen.libgdx

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

fun ShapeRenderer.circle(pos: Vector2, radius: Float) {
    this.circle(pos.x, pos.y, radius)
}

fun ShapeRenderer.circle(pos: Vector2, radius: Float, segment: Int) {
    this.circle(pos.x, pos.y, radius, segment)
}

fun ShapeRenderer.rect(pos: Vector2, width: Float, height: Float) {
    this.rect(pos.x, pos.y, width, height)
}

fun ShapeRenderer.rect(pos: Vector2, size: Vector2) {
    this.rect(pos.x, pos.y, size.x, size.y)
}