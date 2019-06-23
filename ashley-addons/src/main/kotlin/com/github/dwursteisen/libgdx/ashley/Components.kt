package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.github.dwursteisen.libgdx.ashley.fsm.EntityState

/**
 * An entity can hold states using [StateComponent].
 * A state can be assign for one component.
 *
 */
open class StateComponent() : Component, Pool.Poolable {

    var time: Float = 0f
        internal set

    internal var status: MutableMap<Class<out StateComponent>, EntityState> = mutableMapOf()
    internal var timeReset: MutableMap<Class<out StateComponent>, Float> = mutableMapOf()

    override fun reset() {
        time = 0f
        status.clear()
        timeReset.clear()
    }
}

data class TextureComponent(var texture: TextureRegion) : Component

data class Position(val value: Vector2 = Vector2()) : Component
data class Size(val value: Vector2 = Vector2()) : Component
data class Direction(val value: Vector2 = Vector2()) : Component
data class Rotation(var degree: Float = 0f, val origin: Vector2 = Vector2()) : Component
