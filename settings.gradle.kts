rootProject.name = "godot-kotlin-3d-demo"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "com.utopia-rise.godot-kotlin-jvm") {
            useModule("com.utopia-rise:godot-gradle-plugin:0.7.0-4.1.2")
        }
    }
}
