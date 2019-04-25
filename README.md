# LibGDX Addons

Addons for LibGDX, for ease game development

## Gradle Plugins

- [libgdx-gradle-plugin](./libgdx-gradle-plugin): Ease the setup of a libgdx project;
- [assets-gradle-plugin](./assets-gradle-plugin): Create an Assets class with all assets filename;
- [packr-gradle-plugin](./packr-gradle-plugin): Add Packr tasks to bundle a JVM with your game;
- [aseprite-gradle-plugin](./aseprite-gradle-plugin): Export Aseprite file to spritesheet.

## LibGDX libraries

- [core-addons](./core-addons):  Extensions methods for libgdx core;
- [ashley-addons](./ashley-addons): Extensions methods and several base component/systems for Ashley;
- [aseprite-addons](./aseprite-addons): Allow to load Aseprite JSON and texture as Animation or extract slices;
- [admob-addons](./admob-addons): Add support to Admob to your libgdx game;
- [libgdx-test](./libgdx-test): Test library to control your game from a test.

## How to use it?

Your build needs to use `gradle 4.10` or above. Then add this repository
as [dependency sources](https://blog.gradle.org/introducing-source-dependencies)
in your `settings.gradle.kts` / `settings.gradle`
and add select every modules you wants to use using `producesModule`

```kotlin
sourceControl {
    gitRepository(uri("https://github.com/dwursteisen/libgdx-addons.git")) {
        // replace a dependency with a module of this repository
        producesModule("com.github.dwursteisen.libgdx-addons:libgdx-gradle-plugin")
    }
}
```

In your project, just use the module as it's describe in each module `README.md`.
The version to use can be a tag name or `latest.integration` to use the code from `master`.

Example:

```
dependencies {
   classpath("com.github.dwursteisen.libgdx-addons:libgdx-gradle-plugin:latest.integration")
}
```

## Notes
Please notes that everything is developed in Kotlin

See also: 
- ktx : https://github.com/libktx/ktx