package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.*

inline fun <reified T : Component> EntitySystem.get(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)

inline operator fun <reified T : Component> Entity.get(mapper: ComponentMapper<T>): T = mapper.get(this)

inline fun <reified T : Component> Entity.getNullable(mapper: ComponentMapper<T>): T? = mapper.get(this)

inline fun <reified T : Component> PooledEngine.createComponent() : T = this.createComponent(T::class.java)

inline fun <reified T : Component> PooledEngine.createComponentWith(block: T.() -> Unit) : T {
    val component = this.createComponent(T::class.java)
    block.invoke(component)
    return component
}


