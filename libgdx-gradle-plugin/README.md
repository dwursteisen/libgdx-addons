# libGDX Gradle Plugin

libGDX Gradle plugin add all libgdx tasks to your project. 
it's rely a lot on a default convention: your project will contains desktop game in a desktop directory, 
the core in a core directory, …
## How to use it?

- Your gradle project should have a `core` and a `desktop` directory.
- In your `settings.gradle.kts`:

```kotlin
sourceControl {
    gitRepository(uri("https://github.com/dwursteisen/libgdx-addons.git")) {
        producesModule("com.github.dwursteisen.libgdx-addons:libgdx-gradle-plugin")
    }
}
```
- In your `build.gradle.kts`:
```
buildscript {
    dependencies {
       classpath("com.github.dwursteisen.libgdx-addons:libgdx-gradle-plugin:latest.integration")
    }
}

apply(plugin = "libgdx")

project.configure<LibGDXExtensions> {
    mainClass = "com.your.package.MainClass"
    assetsDirectory = project.file("core/assets")
}
```

## Features

- Add a IntelliJ running configuration;
- Add `run` gradle task (to run the game from Gradle);
- Add `dist` gradle task (to create a fat jar of your game);
- Add packr-&lt;os&gt; tasks to create a bundled app of your game;
- Add a `assets` task which create a `Assets` class with all your assets name (to avoid naming mistakes);
- Add `libgdx` libraries as dependency;
- Add `Kotlin` libraries as dependency.
## Example

See [libgdx-sample](https://github.com/dwursteisen/libgdx-sample)

## Roadmap

### Core
- [x] Detect Android / Desktop / Core module;
- [x] Add kotlin support by default;
- [x] Generate Assets class with all all assets file name (like `R class in Android);
- [x] Add libgdx dependencies on each modules.

### Desktop
- [x] Add `dist` task on desktop module to build the jar game;
- [x] Add `run` task on desktop module to run the jar;
- [ ] Add task `packr` on desktop to build the executable version;
- [x] Detect main class;
- [ ] Generate main class if missing;
- [x] Add IntelliJ run configuration.

### Android
- [ ] Add needed dependency repositories ⚠️ Didn't find how to do it! Can you help me?
- [x] Copy native libs on Android;
- [ ] Add run task on Android;
- [ ] Generate AndroidManifest.xml if missing;
- [ ] Generate main class if missing;
- [ ] Detect Android SDK location (check ANDROID_SDK_ROOT / local.properties). If missing, try to guess where the SDK is
- [ ] Add tasks to increment version on Android.
