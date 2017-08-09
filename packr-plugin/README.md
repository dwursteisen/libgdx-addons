# Packr Gradle Support

Add Packr support to gradle.

## How to use it

// TODO: add gradle configuration


// TODO: a task per platform
```
task packr(type: com.github.dwursteisen.libgdx.packr.PackrTask,dependsOn: "dist") {

    // the JDK to bundle with
    jdk = "/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home"
    // the jar to bundle (generally the dist task output)
    classpath = project.tasks.getByName("dist").archivePath
    // bundle identifier (for mac os app)
    bundleIdentifier = "sous.les.apps.sponge"
    mainClass = project.mainClassName
    outputDir = project.file(project.getBuildDir().absolutePath + "/packr/packr-out.app")
    executable = "sponge"
}
```

## All parameters