plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.7.0-4.1.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

godot {
    registrationFileBaseDir.set(projectDir.resolve("scripts").also { it.mkdirs() })
    isRegistrationFileHierarchyEnabled.set(true)
}

kotlin.sourceSets.main {
    kotlin.srcDirs("demo")
}
