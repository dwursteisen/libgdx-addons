# LibGDX Test

## LibGdxRule

LibGdxRule is a JUnit Rule which allow to run and control 
your game from your test. 

Thanks to a DSL, you can create a specific scenario to test
your game and take automatic screenshots or recording.


## How to use it?


Add the JUnit Rule `LibGdxRule` to your unit test

```
class TestClass {

    @JvmField
    @Rule
    val gdx = LibGdxRule(GameUnderTest(), LwjglApplicationConfiguration().apply {
        width = 200
        height = 200
    })
    
    // ...
}
```

Then, the rule will expose a DSL that will help you to describe your test case.

```
    @Test
    fun runThenScreenshot() {
        gdx.startGame()
                .screenshot("test1.png")
                .push(Input.Keys.UP)
                .wait(Duration.ofSeconds(1))
                .release(Input.Keys.UP)
                .screenshot("test2.png")
                .wait(Duration.ofSeconds(2))
                .screenshot("test3.png")
    }
```

The DSL expose several methods and control inputs (like pusing keys, touch on the screen, ...)
It help you too to record the game session. It can take screenshots too.

## Example of use

- Automatic screenshot generator for tools like `fastlane` : keep 
screenshots from your application store alway updated !

- Automatic game testing : help you to test edge case.

- Automatic game recording.

## Issues

You may need to update the working directory, to help LibGdx
to found assets. But you can change the jUnit working directory
by updating your gradle build file : 

```
test {
    // path is relative to current project
    workingDir = project.file("../android/src/main/assets")
}
```

