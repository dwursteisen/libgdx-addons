package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

interface RenderStrategy {
    fun zLevel(entity: Entity, delta: Float): Float
    fun render(entity: Entity, batch: SpriteBatch)
}

inline fun <reified T : Component> RenderStrategy.get(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)

abstract class TexturedStrategy : RenderStrategy {
    private val renderMapper = get<Textured>()
    private val position = get<Position>()
    private val size = get<Size>()
    private val rotation = get<Rotation>()

    override fun render(entity: Entity, batch: SpriteBatch) {
        val entityRender = entity[renderMapper]
        if (entityRender.texture == NO_TEXTURE) return

        val position = entity[position].value
        val size = entity[size].value
        val rotation = entity.getNullable(rotation)
        val offset = entityRender.offset

        val (originX, originY, degree) = if (rotation != null) {
            listOf(rotation.origin.x, rotation.origin.y, rotation.degree)
        } else {
            listOf(0f, 0f, 0f)
        }

        val (x, sizeX) = if (entityRender.hFlip) {
            (position.x + offset.x + size.x) to (-size.x)
        } else {
            (position.x + offset.x) to (size.x)
        }
        batch.setColor(1f, 1f, 1f, entityRender.alpha)
        batch.draw(
            entityRender.texture,
            x, position.y + offset.y,
            originX, originY,
            sizeX, size.y,
            1f, 1f,
            degree
        )
    }
}

class RenderSystem(
    private val viewport: Viewport,
    private val strategies: Map<Int, RenderStrategy>
) : IteratingSystem(Family.all(Render::class.java).get()) {

    private val batch = SpriteBatch()

    private val buffer = mutableListOf<Entity>()

    private val render = get<Render>()

    override fun update(deltaTime: Float) {
        viewport.apply()
        buffer.clear()
        super.update(deltaTime)
        buffer.sortBy { zLevel(it, deltaTime) }
        batch.begin()
        batch.projectionMatrix = viewport.camera.combined
        buffer.forEach { renderEntity(it) }
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        buffer.add(entity)
    }

    private fun zLevel(entity: Entity, deltaTime: Float): Float {
        val id = entity[render].strategy
        return strategies[id]?.zLevel(entity, deltaTime) ?: 0.0f
    }

    private fun renderEntity(entity: Entity) {
        val id = entity[render].strategy
        strategies[id]?.render(entity, batch)
    }
}
