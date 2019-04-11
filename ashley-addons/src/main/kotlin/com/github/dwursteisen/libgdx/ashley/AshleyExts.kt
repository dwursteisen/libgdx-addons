package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem

inline fun <reified T : Component> EntitySystem.get(): ComponentMapper<T> = ComponentMapper.getFor(T::class.java)




