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

val NO_TEXTURE = TextureRegion()

/**
 * Hold texture region of your entity.
 * You can later use this texture to draw your entity in the screen.
 * You can also set the alpha (transparency) and horizontal flip
 * of it.
 *
 * The offset can be use if you want to apply an offset when rendering your
 * texture.
 */
class Textured(
    var texture: TextureRegion = NO_TEXTURE,
    val offset: Vector2 = Vector2(),
    var hFlip: Boolean = false,
    var alpha: Float = 1f
) : Component

@Deprecated("see Textured")
data class TextureComponent(var texture: TextureRegion) : Component

data class Position(val value: Vector2 = Vector2()) : Component
data class Size(val value: Vector2 = Vector2()) : Component
data class Direction(val value: Vector2 = Vector2()) : Component

/**
 * Hold rotation of the current entity.
 * Will be use to render the entity.
 *
 * @property degree: rotation of the entity, in degree
 * @property origin: origin of the rotation.
 */
data class Rotation(
    var degree: Float = 0f,
    val origin: Vector2 = Vector2()
) : Component
