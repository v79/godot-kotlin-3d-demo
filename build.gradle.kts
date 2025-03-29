plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.12.1-4.4"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

godot {
    registrationFileBaseDir.set(projectDir.resolve("scripts"))
    isRegistrationFileHierarchyEnabled.set(true)

    isGodotCoroutinesEnabled.set(true)
}

kotlin.sourceSets.main {
    kotlin.srcDirs("demo")
}
