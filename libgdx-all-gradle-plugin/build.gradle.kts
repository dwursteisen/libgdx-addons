plugins {
    `java-library`
    `maven-publish`
}

tasks["jar"].apply {
    this as Jar
    this.isZip64 = true

    doFirst {
        val runtimeClasspath = project.project(":libgdx-gradle-plugin").sourceSets.getByName("main").runtimeClasspath
            .filter { !it.path.contains("generated-gradle-jars") }
            .filter { !it.name.contains("wrapper/dists/") }

        val files = project.files(runtimeClasspath)
        from(files.filter { f -> f.exists() }.map { f -> if (f.isDirectory) f else project.zipTree(f) })
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.SF")


    }
}
