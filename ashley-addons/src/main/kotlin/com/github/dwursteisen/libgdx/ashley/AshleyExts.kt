package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine

inline fun <reified T : Component> EntitySystem.get(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)

inline operator fun <reified T : Component> Entity.get(mapper: ComponentMapper<T>): T = mapper.get(this)

inline fun <reified T : Component> Entity.getNullable(mapper: ComponentMapper<T>): T? = mapper.get(this)

inline fun <reified T : Component> PooledEngine.createComponent(): T = this.createComponent(T::class.java)

inline fun <reified T : Component> PooledEngine.createComponentWith(block: T.() -> Unit): T {
    val component = this.createComponent(T::class.java)
    block.invoke(component)
    return component
}

fun Engine.entity(component: Class<out Component>): Entity = this.getEntitiesFor(Family.all(component).get()).first()

fun Engine.removeAll(entities: Iterable<Entity>): Unit = entities.forEach { this.removeEntity(it) }



