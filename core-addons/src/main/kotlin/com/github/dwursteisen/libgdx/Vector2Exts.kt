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
 * Return true only if the coordinates are close to the vector.
 * Will return false otherwise OR if the coordinate match perfectly.
 * If [round] is true, the vector coordinates will be round to the target coordinates.
 *
 * It means that if you call the method twice, the first time the result will be [true] then [false].
 */
fun Vector2.onlyCloseTo(x: Float, y: Float, delta: Float, round: Boolean = true): Boolean {
    if (this.x == x && this.y == y) return false
    val isCloseTo = isCloseTo(x, y, delta)
    if (isCloseTo && round) {
        this.set(x, y)
    }
    return isCloseTo
}
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
