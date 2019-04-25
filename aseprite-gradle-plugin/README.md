# Aseprite Gradle exporter

Add new task to gradle which export your aseprite file to png and json

## How to use it?

Add the plugin as build gradle dependencies

Read the main [How to use it](../README.md#how-to-use-it)

Activate the plugin and add an aseprite task

```
apply plugin: "com.github.dwursteisen.libgdx.aseprite.AsepritePlugin"


task aseprite(type: com.github.dwursteisen.libgdx.aseprite.AsepriteTask, group: "aseprite") {
    json = true
    verbose = true
    outputDirectory = project.file(project.getBuildDir().absolutePath + "/aseprite")
    inputFiles = fileTree("./assets").include("**/*.ase")

}

```

Setup Gradle to know where it should locate Aseprite : 


Configure it using `aseprite.exec` property (ie: in your` ~/.gradle/gradle.properties`)
```
aseprite.exec=<path to exec>
```

or using aseprite extension in your `build.gradle
```
aseprite {
    exec=<path to exec>
}
```


MacOS specific : point to aseprite located into `<aseprite directory>/Aseprite.app/Contents/MacOS/aseprite`

## How to configure a task ?

```
task aseprite(type: com.github.dwursteisen.libgdx.aseprite.AsepriteTask, group: "aseprite") {
    inputFiles = <input file to process>
    outputDirectory = <output directory to copy processed files>
    scale = <scale factor (default: 1)>
    format = <json data format (default: AsepriteFormat.HASH)>
    json = <activate json export (default: true)>
    verbose = <verbose (default: false)>
}
```
        
## Limitation
