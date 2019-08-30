package com.github.dwursteisen.libgdx.ashley.fsm

import com.badlogic.ashley.core.Entity
import com.github.dwursteisen.libgdx.ashley.EventData

/**
 * Implement this class to represent a state.
 *
 * The lifecycle of a state is always in the same order:
 *
 * [enter] -> [update] -> [exit]
 *
 * [enter] will be called when the entity will enter in this new state.
 * [update] will be called for each render call when in this state.
 * [exit] will be called when the entity before being in another state.
 */
abstract class EntityState {

    /**
     * Called when an entity is going into this state.
     */
    open fun enter(entity: Entity, eventData: EventData) = Unit

    /**
     * Called by the main loop when in this state.
     *
     * [entity] the current entity.
     * [time] the time since the entity is in this new state.
     *
     */
    // TODO: add delta as third parameter
    open fun update(entity: Entity, time: Float) = Unit

    /**
     * Called when an entity is going into another state.
     */
    open fun exit(entity: Entity, eventData: EventData) = Unit

    companion object {
        val STATE_NOP = object : EntityState() {}
    }
}
