package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem


class StateSystem : IteratingSystem(Family.all(StateComponent::class.java).get()) {

    private val sm: ComponentMapper<StateComponent> = get()

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        sm[entity].time += deltaTime
    }
}