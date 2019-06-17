package com.github.dwursteisen.libgdx

fun Float.isNotBetween(a: Float, b: Float): Boolean = !isBetween(a, b)

/**
 * Is the number between the number a and b (included).
 * Return true if it's the case.
 */
fun Float.isBetween(a: Float, b: Float): Boolean {
    return this in a..b
}
