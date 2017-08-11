package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.input.RemoteInput
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.mockito.Mockito


object StateMachineSpecs : Spek({
    val engine = PooledEngine()
    beforeEachTest {
        engine.removeAllEntities()
        engine.update(0f)


    }

    given("a state machine system") {
        Gdx.input = RemoteInput()
        Gdx.app = Mockito.mock(Application::class.java)

        val RANDOM_EVENT = 1
        val familly = Family.all(Position::class.java, StateComponent::class.java).get()
        val eventBus = EventBus()
        val system = object : StateMachineSystem(eventBus, familly) {
            override fun describeMachine() {
                val STATE_NOTHING = object : EntityState() {

                    override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
                        entity.getComponent(StateComponent::class.java).time = -2f
                    }

                    override fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
                        entity.getComponent(StateComponent::class.java).time = 0f
                    }
                }

                val STATE_ANOTHER = object : EntityState() {
                    override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
                        entity.getComponent(StateComponent::class.java).time = -1f
                    }
                }

                startWith(STATE_NOTHING)
                onState(STATE_NOTHING).on(RANDOM_EVENT) { entity, _ -> go(STATE_ANOTHER, entity) }
            }

        }


        engine.addSystem(system)

        on("update") {
            it("should not crash") {
                val entity = engine.createEntity()
                entity.add(Position())
                entity.add(StateComponent())
                engine.addEntity(entity)
                entity.remove(StateComponent::class.java)

                eventBus.emit(RANDOM_EVENT)
                engine.update(0f)
                eventBus.update(0f)
            }

            it("should send event only to specific entity") {
                val entity = engine.createEntity()
                entity.add(Position())
                entity.add(StateComponent())
                engine.addEntity(entity)
                val another = engine.createEntity()
                another.add(Position())
                another.add(StateComponent())
                engine.addEntity(another)

                engine.update(0f) // startwith
                eventBus.update(0f)

                var entityTime = entity.getComponent(StateComponent::class.java).time
                var anotherTime = another.getComponent(StateComponent::class.java).time

                Assertions.assertThat(entityTime).isEqualTo(-2f)
                Assertions.assertThat(anotherTime).isEqualTo(-2f)

                eventBus.emit(RANDOM_EVENT, entity)
                engine.update(0f)
                eventBus.update(0f)

                entityTime = entity.getComponent(StateComponent::class.java).time
                anotherTime = another.getComponent(StateComponent::class.java).time

                Assertions.assertThat(entityTime).isEqualTo(-1f)
                Assertions.assertThat(anotherTime).isEqualTo(-2f)

            }

            it("should broadcast event") {
                val entity = engine.createEntity()
                entity.add(Position())
                entity.add(StateComponent())
                engine.addEntity(entity)
                val another = engine.createEntity()
                another.add(Position())
                another.add(StateComponent())
                engine.addEntity(another)

                engine.update(0f) // startwith
                eventBus.update(0f)

                eventBus.emit(RANDOM_EVENT)
                engine.update(0f)
                eventBus.update(0f)

                val entityTime = entity.getComponent(StateComponent::class.java).time
                val anotherTime = another.getComponent(StateComponent::class.java).time

                Assertions.assertThat(entityTime).isEqualTo(-1f)
                Assertions.assertThat(anotherTime).isEqualTo(-1f)

            }

        }

    }
})