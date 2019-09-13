package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.github.dwursteisen.libgdx.ashley.fsm.EntityState
import com.github.dwursteisen.libgdx.emptyGdxArray

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

val NO_ANIMATION = Animation<TextureRegion>(0f, emptyGdxArray())

/**
 * Hold animation of your entity.
 * The time of the current animation is set to 0 when the animation is changed.
 *
 * To update the time of the animation, the [AnimationSystem] needs to be added to the engine.
 */
class Animated(
    animation: Animation<TextureRegion> = NO_ANIMATION,
    var time: Float = 0f
) : Component {
    var animation: Animation<TextureRegion> = animation
        set(value) {
            time = 0f
            field = value
        }
}

/**
 * Hold the information that this entity should be rendered and with which strategy.
 * The [RenderSystem] will use this strategy to render the entity on screen.
 */
class Render(var strategy: Int) : Component

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

class MapLayerComponent(var zLevel: Int, var layer: MapLayer) : Component
