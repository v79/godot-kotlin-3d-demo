plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.7.1-4.1.2"
}

repositories {
    mavenCentral()
}

godot {
    registrationFileBaseDir.set(projectDir.resolve("scripts").also { it.mkdirs() })
    isRegistrationFileHierarchyEnabled.set(true)
}

kotlin.sourceSets.main {
    kotlin.srcDirs("demo")
}
