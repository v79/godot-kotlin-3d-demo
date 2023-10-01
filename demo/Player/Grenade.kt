package Player

import godot.Area3D
import godot.AudioStreamPlayer3D
import godot.CharacterBody3D
import godot.Node3D
import godot.PackedScene
import godot.ProjectSettings
import godot.Timer
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
import godot.core.asStringName
import godot.global.GD
import shared.Damageable

@RegisterClass
class Grenade: CharacterBody3D() {
    @Export
    @RegisterProperty
    lateinit var explosionScene: PackedScene

    @Export
    @RegisterProperty
    lateinit var explosionArea3D: Area3D

    @Export
    @RegisterProperty
    lateinit var explosionSound: AudioStreamPlayer3D

    @Export
    @RegisterProperty
    lateinit var explosionStartTimer: Timer



    private val gravity: Double by lazy {
        ProjectSettings.getSetting("physics/3d/default_gravity") as Double
    }

    @RegisterFunction
    override fun _ready() {
        explosionStartTimer.timeout.connect(this, Grenade::explode)
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        velocity += Vector3.DOWN * gravity * delta
        val collision = moveAndCollide(velocity * delta)

        if (collision != null) {
            velocity = velocity.bounce(collision.getNormal(0)) * 0.7

            if (explosionStartTimer.isStopped()) {
                explosionStartTimer.start()
            }
        }
    }

    @RegisterFunction
    fun `throw`(throwVelocity: Vector3) {
        velocity = throwVelocity
    }

    @RegisterFunction
    fun explode() {
        setPhysicsProcess(false)

        explosionSound.pitchScale = GD.randfRange(2f, 0.1f)
        explosionSound.play()

        explosionArea3D
            .getOverlappingBodies()
            .filter { body -> !body.isClass("Player") }
            .filter { body -> body is Damageable || body.isInGroup("damageable".asStringName()) }
            .forEach { body ->
                val impactPoint = (globalPosition - body.globalPosition)
                    .normalized()
                    .let {  impactPoint ->
                        (impactPoint + Vector3.DOWN).normalized() * 0.5
                    }
                val force = -impactPoint * 10.0

                when {
                    body is Damageable -> body.damage(impactPoint, force)
                    body.isInGroup("damageable".asStringName()) ->body.call("damage".asStringName(), impactPoint, force)
                }
            }

        explosionScene.instantiate()?.let { explosion ->
            getParent()?.addChild(explosion)
            (explosion as? Node3D)?.let { it.globalPosition = globalPosition }
        }

        hide()
        explosionSound.finished.connect(this, Grenade::queueFree, ConnectFlags.CONNECT_ONE_SHOT.id.toInt())
    }
}