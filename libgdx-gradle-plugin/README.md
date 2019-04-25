# libGDX Gradle Plugin

## How to use it?

1. Read the main [How to use it](../README.md#how-to-use-it)
2. Your gradle project should have a `core` and a `desktop` directory.
3. In your `build.gradle.kts`:
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

### Example

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
- [x] Add IntelliJ run configuration.

### Android
- [ ] Copy native libs on Android;
- [ ] Add run task on Android;
- [ ] Add tasks to increment version on Android.