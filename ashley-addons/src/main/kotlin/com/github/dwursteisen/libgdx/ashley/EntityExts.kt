package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

inline operator fun <reified T : Component> Entity.get(mapper: ComponentMapper<T>): T = mapper.get(this)

inline fun <reified T : Component> Entity.getNullable(mapper: ComponentMapper<T>): T? = mapper.get(this)
