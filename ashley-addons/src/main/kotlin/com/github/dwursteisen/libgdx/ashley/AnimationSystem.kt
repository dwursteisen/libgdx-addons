package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem

class AnimationSystem : IteratingSystem(Family.all(Animated::class.java).get()) {
    private val animation = get<Animated>()
    private val sprite = get<Textured>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity[animation].animation == NO_ANIMATION) return
        entity[animation].time += deltaTime
        val frame = entity[animation].animation.getKeyFrame(entity[animation].time)
        entity[sprite].texture = frame
    }
}
