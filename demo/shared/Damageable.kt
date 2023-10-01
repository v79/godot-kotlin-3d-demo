package shared

import godot.annotation.RegisterFunction
import godot.core.Vector3

interface Damageable {
    @RegisterFunction
    fun damage(impactPoint: Vector3, velocity: Vector3)
}