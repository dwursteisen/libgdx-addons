# libGDX Gradle Plugin

libGDX Gradle plugin add all libgdx tasks to your project. 
it's rely a lot on a default convention: your project will contains desktop game in a desktop directory, 
the core in a core directory, …
## How to use it?

- Your gradle project **SHOULD** have, at least, a `core` and a `desktop` and/or `android` directory.

- In your `build.gradle.kts`:
```
buildscript {
    dependencies {
        // see https://jitpack.io/#dwursteisen/libgdx-addons
        classpath("com.github.dwursteisen.libgdx-addons:libgdx-gradle-plugin:<version>")
    }

    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://jitpack.io") }
    }
}

apply(plugin = "libgdx")

allprojects {

    repositories {
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
    }

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
- [ ] create a fat jar do not depends from external repositories?
- [x] Add libgdx dependencies on each modules.

### Desktop
- [x] Add `dist` task on desktop module to build the jar game;
- [x] Add `run` task on desktop module to run the jar;
- [ ] Add task `packr` on desktop to build the executable version;
- [x] Detect main class;
- [x] Generate main class if missing;
- [x] Add IntelliJ run configuration.

### Android
- [ ] Add needed dependency repositories ⚠️ Didn't find how to do it! Can you help me?
- [x] Copy native libs on Android;
- [ ] Add run task on Android;
- [ ] Generate AndroidManifest.xml if missing;
- [ ] Generate main class if missing;
- [ ] Detect Android SDK location (check ANDROID_SDK_ROOT / local.properties). If missing, try to guess where the SDK is
- [ ] Add tasks to increment version on Android.
