# Ashley Addons

Add some utility class for the Ashley library

## Event Bus

An simple event bus. It allow to emit simple event.
All events will be sent outside the (ashley) engine 
transactional context

### Configure it

```
class CustomScreen : ScreenAdapter() {

   lateinit var eventBus: EventBus
   
   override fun show() {
        // ...
        eventBus = EventBus()  
   }
   fun render(delta: Float) {
       // ...
       eventBus.update(delta)
   }
}
``` 

### Use it

You can register an event listener, to listen
one or more events.

```
val EVENT_TOUCH: Int = 1

eventBus.register(object: EventListener { event, data -> 
    // event = EVENT_TOUCH
    // event_data = data if event comes with datas
}, EVENT_TOUCH) 
```

You can emit events too. These events will be process
the next loop update 

```
eventBus.emit(EVENT_TOUCH)

val data = eventBus.createEventData()
data.body = "WHAT YOU WANTS"
eventBus.emit(EVENT_TOUCH, data)
```

you can prepare events to be emitted later.
Duration is in milliseconds.

```
eventBus.emitLater(500, EVENT_TOUCH)

val data = eventBus.createEventData()
data.body = "WHAT YOU WANTS"
eventBus.emitLater(1000, EVENT_TOUCH, data)
```

## State Machine

State machine is a custom system designed to create
finite state machin on the ashley entity system

### Configure it

Create a new class which extends `StateMachinSystem`.
You'll need to pass the ashley familly and the event bus (see bellow)

Please note that the system __will automaticaly register itselft to the event bus__

You'll have to __only__ implement one method (`describeStateMachine` ) that will describe your state machine.
Please note that your entity __should__ have the component `StateComponent`.

```
class CustomScreen : ScreenAdapter() {

   lateinit var eventBus: EventBus
   lateinit var engine: PooledEngine
   
   
   override fun show() {
        // ...
        eventBus = EventBus()  
        
        // mandatory to update states
        engine.addSystem(StateSystem())
        engine.addSystem(MyStateSystem())
        
        engine.add {
             add(StateComponent())
        }
        
   }
   fun render(delta: Float) {
       // ...
       engine.update(delta)
       eventBus.update(delta)
   }
}
``` 

### Use it

```
class MyStateSystem(eventBus: EventBus) : StateMachineSystem(eventBus, Familly.add(StateComponent::class.java).get()) {

    override fun describeStateMachine() {
      val STATE_MAIN = object : EntityState() {
          override fun enter(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
              println("enter")
          }

          override fun update(entity: Entity, machine: StateMachineSystem, delta: Float) {
              println("update")
          }

          override fun exit(entity: Entity, machine: StateMachineSystem, eventData: EventData) {
              println("exit")
          }
      }

      val STATE_START_GAME = object : EntityState() {
          
      }

      startWith(STATE_MAIN)
      onState(STATE_MAIN).on(StateMachineSystem.EVENT_TOUCHED) { entity, event ->
          go(STATE_START_GAME, entity)
      }
    
    }

}
```

The method will create states (`EntityState`) and will
create transitions between states.

- `stateWith` : inital state of the `Entity`
- `onState(<state>).on(<events>)` : explain how to react on events and set a new state.


### Example of use

- [SuperJumper](https://github.com/dwursteisen/ashley-superjumper) using this `StateEngineSystem` 
- [PlatformSystem](https://github.com/dwursteisen/ashley-superjumper/blob/master/core/src/main/kotlin/com/github/dwursteisen/superjumper/systems/PlatformSystem.kt)
- [BobSystem](https://github.com/dwursteisen/ashley-superjumper/blob/master/core/src/main/kotlin/com/github/dwursteisen/superjumper/systems/BobSystem.kt)  
