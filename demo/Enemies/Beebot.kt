package Enemies

import Enemies.BeeBot.BeeRoot
import Enemies.smoke_puff.SmokePuff
import Player.Bullet
import Player.Coin.Coin
import Player.Player
import godot.AnimationPlayer
import godot.Area3D
import godot.AudioStreamPlayer3D
import godot.CollisionShape3D
import godot.Node
import godot.Node3D
import godot.PackedScene
import godot.ResourceLoader
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
import godot.core.asStringName
import godot.extensions.instantiateAs
import godot.extensions.loadAs

@RegisterClass
class Beebot : Enemy() {

    @Export
    @RegisterProperty
    override var coinsCount = 7

    @Export
    @RegisterProperty
    var shootTimer = 1.5

    @Export
    @RegisterProperty
    var bulletSpeed = 6.0

    @Export
    @RegisterProperty
    lateinit var reactionAnimationPlayer: AnimationPlayer

    @Export
    @RegisterProperty
    lateinit var flyingAnimationPlayer: AnimationPlayer

    @Export
    @RegisterProperty
    lateinit var detectionArea: Area3D

    @Export
    @RegisterProperty
    lateinit var deathMeshCollider: CollisionShape3D

    @Export
    @RegisterProperty
    lateinit var beeRoot: BeeRoot

    @Export
    @RegisterProperty
    lateinit var defeatSound: AudioStreamPlayer3D

    private val bulletScene =  ResourceLoader.loadAs<PackedScene>("res://demo/Player/Bullet.tscn")!!

    private val foundPlayerName = "found_player".asStringName()
    private val lostPlayerName = "lost_player".asStringName()
    private val disabledName = "disabled".asStringName()

    var shootCount = 0.0
    var target: Node3D? = null
    var alive = true

    @RegisterFunction
    override fun _ready() {
        detectionArea.bodyEntered.connect(this, Beebot::onBodyEntered)
        detectionArea.bodyExited.connect(this, Beebot::onBodyExited)
        beeRoot.playIdle()
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        if (!alive) return
        target?.let {
            transform = transform.run {
                val targetTransform = lookingAt(it.globalPosition)
                interpolateWith(targetTransform, 0.1)
            }

            shootCount += delta
            if (shootCount > shootTimer) {
                beeRoot.playSpitAttack()
                shootCount -= shootTimer

                val bullet = bulletScene.instantiateAs<Bullet>()!!
                bullet.shooter = this
                val origin = globalPosition
                val target = it.globalPosition + Vector3.UP
                val aimDirection = (target - globalPosition).normalized()
                bullet.velocity = aimDirection * bulletSpeed
                bullet.distanceLimit = 14.0f
                getParent()!!.addChild(bullet)
                bullet.globalPosition = origin
            }
        }
    }

    @RegisterFunction
    override fun damage(impactPoint: Vector3, force: Vector3) {
        applyImpulse(force.limitLength(3.0), impactPoint)

        if (!alive) {
            return
        }

        defeatSound.play()
        alive = false

        flyingAnimationPlayer.stop()
        flyingAnimationPlayer.seek(0.0, true)
        detectionArea.bodyEntered.disconnect(this, Beebot::onBodyEntered)
        detectionArea.bodyExited.disconnect(this, Beebot::onBodyExited)
        target = null
        deathMeshCollider.setDeferred(disabledName, false)

        gravityScale = 1.0f
        beeRoot.playPoweroff()

        death()
    }

    @RegisterFunction
    fun onBodyEntered(body: Node) {
        if (body is Player) {
            shootCount = 0.0
            target = body
            reactionAnimationPlayer.play(foundPlayerName)
        }
    }

    @RegisterFunction
    fun onBodyExited(body: Node) {
        if (body is Player) {
            target = null
            reactionAnimationPlayer.play(lostPlayerName)
        }
    }
}
