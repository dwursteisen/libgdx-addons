# libGDX Gradle Plugin


## Core
- [ ] Detect Android / Desktop / Core module;
- [ ] Add kotlin support by default;
- [ ] Add libgdx dependencies on each modules.

## Desktop
- [x] Add `dist` task on desktop module to build the jar game;
- [x] Add `run` task on desktop module to run the jar;
- [ ] Add task `packr` on desktop to build the executable version;
- [x] Detect main class;
- [x] Add IntelliJ run configuration.

## Android
- [ ] Copy native libs on Android;
- [ ] Add run task on Android;
- [ ] Add tasks to increment version on Android.

## Example of use

```
apply(plugin = "libgdx")

project.configure<LibGDXExtensions> {
    mainClass = "com.your.package.MainClass"
    assetsDirectory = project.file("core/assets")
}
```
