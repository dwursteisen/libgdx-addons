package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ktx.log.debug


typealias Transition = (entity: Entity, event: EventData) -> Unit
typealias Event = Int

abstract class EntityState {
    open fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {

    }

    open fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {

    }

    open fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) {

    }

    companion object {
        val STATE_NOP = object : EntityState() {
            override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
                machine.emit(StateMachineSystem.EVENT_NOP)
            }
        }
    }
}



typealias EventFactory = () -> Event


abstract class StateMachineSystem(val eventBus: EventBus, family: Family) : IteratingSystem(family), EventListener {

    private var transitions = emptyMap<EntityState, Map<Event, Transition>>()
    private var defaultTransition = emptyMap<EntityState, Transition>()

    private val state: ComponentMapper<StateComponent> = get()

    private val events: Set<Event>
        get() {
            val allEvents = transitions.values.flatMap { it.keys }.toSet()
            if (defaultTransition.isNotEmpty()) {
                return allEvents + EVENT_NOP
            }
            return allEvents
        }

    companion object {
        val EVENT_NOP = -1
        val EVENT_TOUCHED = -2
        val EVENT_SLIDE = -3
        val EVENT_KEY = -4
    }


    abstract fun describeMachine()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        describeMachine()
        eventBus.register(this, *events.toIntArray())
    }


    private inline fun usingState(entity: Entity, block: (state: StateComponent) -> Unit) {
        val state = entity.getNullable(state)
        if (state == null) {
            ktx.log.error {
                "Your entity SHOULD have the StateComponent component, as the entity is managed by a State Machine System." +
                        "As the current entity doesn't have a state, it will be silently ignored. But you may wants to fix this issue as it is not expected."
            }
        } else {
            block.invoke(state)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        usingState(entity) { state ->
            state.status.update(entity, this, deltaTime)
        }
    }


    class OnState(val state: EntityState, val parent: StateMachineSystem) {

        fun on(vararg events: Int, block: Transition): OnState {
            events.forEach { on(it, block) }
            return this
        }

        fun on(events: List<Int>, block: Transition): OnState {
            events.forEach { on(it, block) }
            return this
        }

        fun on(event: Int, block: Transition): OnState {

            var currentTransitions = parent.transitions[state] ?: emptyMap()
            currentTransitions += event to block

            parent.transitions += state to currentTransitions
            return this
        }

        fun default(block: Transition): Unit {
            parent.defaultTransition += state to block
        }
    }

    fun onState(state: EntityState): OnState {
        return OnState(state, this)
    }

    fun startWith(state: EntityState) {
        startWith({ entity, data -> go(state, entity, data) })
    }

    fun startWith(transition: Transition) {
        onState(EntityState.STATE_NOP).default(transition)
    }


    fun go(newState: EntityState, entity: Entity, event: EventData = eventBus.createEventData()) {
        val entityState = entity[state]

        debug("STATE_MACHINE", { -> "Exit ${entityState.status::class.java.simpleName} on event ${event.event}" })
        entityState.status.exit(entity, this, event)

        entityState.status = newState
        debug("STATE_MACHINE", { -> "Enter ${entityState.status::class.java.simpleName} on event ${event.event}" })
        entityState.status.enter(entity, this, event)
    }


    fun emit(event: Event) {
        emit(event, eventBus.createEventData())
    }

    fun emit(event: Event, eventData: EventData) {
        entities?.forEach { it -> perform(event, it, eventData) }
    }

    private fun perform(event: Event, entity: Entity, eventData: EventData) {
        eventData.event = event
        usingState(entity) { state ->
            val entityState = state.status
            val transition: Transition? = transitions[entityState]?.get(event) ?: defaultTransition[entityState]
            transition?.invoke(entity, eventData)
        }

    }

    override fun onEvent(event: Event, eventData: EventData) {
        if (!checkProcessing()) {
            return
        }
        val target = eventData.target

        if (target == null) {
            emit(event, eventData)
        } else {
            perform(event, target, eventData)
        }
    }
}

