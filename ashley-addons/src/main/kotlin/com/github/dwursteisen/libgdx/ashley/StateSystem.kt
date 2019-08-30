package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem

/**
 * System which only increase time on entities
 *
 * The time can be latter on use to update time based properties.
 * (For example: updating animation according to this time)
 */
class StateSystem : IteratingSystem(Family.all(StateComponent::class.java).get()) {

    private val state = get<StateComponent>()

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[state].time += deltaTime
    }
}
