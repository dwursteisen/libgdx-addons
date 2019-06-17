package com.github.dwursteisen.libgdx

import com.badlogic.gdx.math.Vector2

/**
 * Return true if the x and y coordinates are close to the vector modulo the delta value.
 */
fun Vector2.isCloseTo(x: Float, y: Float, delta: Float): Boolean {
    return x.isBetween(x - delta, x + delta) && y.isBetween(y - delta, y + delta)
}

/**
 * See [isCloseTo] with x,y parameters.
 */
fun Vector2.isCloseTo(vector2: Vector2, delta: Float) = isCloseTo(vector2.x, vector2.y, delta)

/**
 * Factory to create a new Vector2 class.
 *
 * Example:
 *
 * ```
 * 12 v2 34 // will create a Vector2(12f, 34f)
 * ```
 */
infix fun Number.v2(other: Number): Vector2 {
    return Vector2(this.toFloat(), other.toFloat())
}
