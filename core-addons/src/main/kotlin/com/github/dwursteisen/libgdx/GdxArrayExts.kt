package com.github.dwursteisen.libgdx

import com.badlogic.gdx.utils.Array

fun <T> gdxArrayOf(vararg obj: T) = Array<T>(obj)
fun <T> emptyGdxArray(): Array<T> = gdxArrayOf()
