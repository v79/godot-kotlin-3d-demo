package shared

import godot.core.Vector3

interface Damageable {
    fun damage(impactPoint: Vector3, velocity: Vector3)
}