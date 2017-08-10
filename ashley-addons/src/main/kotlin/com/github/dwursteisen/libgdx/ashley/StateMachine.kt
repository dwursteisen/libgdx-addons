package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ktx.log.info


typealias Transition = (entity: Entity, event: EventData) -> Unit
typealias Event = Int

private val NO_EVENT_DATA = EventData()

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

    val events: Set<Event>
        get() = transitions.values.flatMap { it.keys }.toSet()

    companion object {
        val EVENT_NOP = -1
        val EVENT_TOUCHED = -2
        val EVENT_KEY = -3
    }

    abstract fun describeMachine()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        describeMachine()
        eventBus.register(this, *events.toIntArray())
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[state].status.update(entity, this, deltaTime)
    }


    class OnState(val state: EntityState, val parent: StateMachineSystem) {

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
        onState(EntityState.STATE_NOP).default({ entity, _ -> go(state, entity) })
    }


    fun go(newState: EntityState, entity: Entity, event: EventData = NO_EVENT_DATA) {
        val entityState = entity[state]

        info("STATE_MACHINE", { -> "Exit ${entityState.status::class.java.simpleName}" })
        entityState.status.exit(entity, this, event)

        entityState.status = newState
        info("STATE_MACHINE", { -> "Enter ${entityState.status::class.java.simpleName}" })
        entityState.status.enter(entity, this, event)
    }


    fun emit(event: Event) = emit(event, EventData())

    fun emit(event: Event, eventData: EventData) {
        for (index in 0..entities.size()-1) {
            val it = entities[index]
            perform(event, it, eventData)
        }
    }

    private fun perform(event: Event, entity: Entity, eventData: EventData): Unit {
        val entityState = entity[state].status
        val transition: Transition? = transitions[entityState]?.get(event) ?: defaultTransition[entityState]
        transition?.invoke(entity, eventData)
    }

    override fun onEvent(event: Event, eventData: EventData) {
        val target = eventData.target
        if (target == null) {
            emit(event, eventData)
        } else {
            perform(event, target, eventData)
        }
    }
}
