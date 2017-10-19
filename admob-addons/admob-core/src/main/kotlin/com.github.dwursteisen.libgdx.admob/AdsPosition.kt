package com.github.dwursteisen.libgdx.admob

typealias AdsPosition = Int

val LEFT: AdsPosition = 1
val CENTER: AdsPosition = LEFT.shl(1)
val RIGHT: AdsPosition = LEFT.shl(2)

val DOWN: AdsPosition = LEFT.shl(3)
val UP: AdsPosition = LEFT.shl(4)
