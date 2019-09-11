package com.github.dwursteisen.libgdx

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import kotlin.reflect.KProperty

fun TiledMapTileLayer.scanCells(action: TiledMapTileLayer.(Int, Int, TiledMapTileLayer.Cell) -> Unit) {
    (0 until width).forEach { x ->
        (0 until height).forEach { y ->
            getCell(x, y)?.run { action(x, y, this) }
        }
    }
}

fun MapLayer.scanObjects(action: MapLayer.(Float, Float, MapObject) -> Unit) {
    this.objects.forEach {
        val x = it.properties["x"].toString().toFloat()
        val y = it.properties["y"].toString().toFloat()

        this.action(x, y, it)
    }
}

inline operator fun <reified T> MapProperties.getValue(thisRef: Any?, property: KProperty<*>): T {
    return this[property.name] as T
}
