package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

data class StateComponent(var state: Int = 0,
                          @JvmField
                          var time: Float = 0f,
                          var status: EntityState = EntityState.STATE_NOP
) : Component {
    fun get() = state
    fun set(s: Int) {
        state = s
    }
}
data class TextureComponent(var texture: TextureRegion) : Component

data class Position(val value: Vector2 = Vector2()) : Component
data class Size(val value: Vector2 = Vector2()) : Component
data class Direction(val value: Vector2 = Vector2()) : Component
data class Rotation(var degree: Float = 0f, val origin: Vector2 = Vector2()) : Component
