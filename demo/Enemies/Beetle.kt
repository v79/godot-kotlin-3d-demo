package Enemies

import Enemies.beetleBot.BeetleBotSkin
import Enemies.smoke_puff.SmokePuff
import Player.Coin.Coin
import Player.Player
import godot.AnimationPlayer
import godot.Area3D
import godot.AudioStreamPlayer3D
import godot.CollisionShape3D
import godot.NavigationAgent3D
import godot.Node
import godot.Node3D
import godot.PackedScene
import godot.ResourceLoader
import godot.RigidBody3D
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
import godot.core.asStringName
import godot.extensions.instantiateAs
import godot.extensions.loadAs
import shared.Damageable

@RegisterClass
class Beetle : RigidBody3D(), Damageable {

    @Export
    @RegisterProperty
    var shootTimer = 1.5

    @Export
    @RegisterProperty
    var bulletSpeed = 6.0

    @Export
    @RegisterProperty
    var coinsCount = 5

    @Export
    @RegisterProperty
    lateinit var reactionAnimationPlayer: AnimationPlayer

    @Export
    @RegisterProperty
    lateinit var detectionArea: Area3D

    @Export
    @RegisterProperty
    lateinit var beetleSkin: BeetleBotSkin

    @Export
    @RegisterProperty
    lateinit var navigationAgent: NavigationAgent3D

    @Export
    @RegisterProperty
    lateinit var deathCollisionShape: CollisionShape3D

    @Export
    @RegisterProperty
    lateinit var defeatSound: AudioStreamPlayer3D

    private val puffScene = ResourceLoader.loadAs<PackedScene>("res://demo/Enemies/smoke_puff/smoke_puff.tscn")!!
    private val coinScene = ResourceLoader.loadAs<PackedScene>("res://demo/Player/Coin/Coin.tscn")!!

    private val foundPlayerName = "found_player".asStringName()
    private val lostPlayerName = "lost_player".asStringName()
    private val disabledName = "disabled".asStringName()

    var target: Node3D? = null
    var alive = true

    @RegisterFunction
    override fun _ready() {
        detectionArea.bodyEntered.connect(this, Beetle::onBodyEntered)
        detectionArea.bodyExited.connect(this, Beetle::onBodyExited)
        beetleSkin.idle()
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        if (!alive) return
        target?.let {
            beetleSkin.walk()
            val targetLookPosition = it.globalPosition
            targetLookPosition.y = globalPosition.y
            if (targetLookPosition != Vector3.ZERO) {
                lookAt(targetLookPosition)
            }

            navigationAgent.targetPosition = it.globalPosition

            val nextLocation = navigationAgent.getNextPathPosition()

            if (!navigationAgent.isTargetReached()) {
                var direction = (nextLocation - globalPosition)
                direction.y = 0.0
                direction = direction.normalized()

                val collision = moveAndCollide(direction * delta * 3)
                if (collision != null) {
                    val collider = collision.getCollider()
                    if (collider is Player) {
                        val impactPoint: Vector3 = globalPosition - collider.globalPosition
                        var force = -impactPoint
                        // Throws player up a little bit
                        force.y = 0.5
                        force *= 10.0
                        collider.damage(impactPoint, force)
                        beetleSkin.attack()
                    }
                }
            }
        }
    }

    @RegisterFunction
    override fun damage(impactPoint: Vector3, force: Vector3) {
        lockRotation = false
        applyImpulse(force.limitLength(3.0), impactPoint)

        if (!alive) {
            return
        }

        defeatSound.play()
        alive = false
        beetleSkin.powerOff()

        detectionArea.bodyEntered.disconnect(this, Beetle::onBodyEntered)
        detectionArea.bodyExited.disconnect(this, Beetle::onBodyExited)
        target = null
        deathCollisionShape.setDeferred(disabledName, false)

        axisLockAngularX = false
        axisLockAngularY = false
        axisLockAngularZ = false
        gravityScale = 1.0f

        getTree()!!.createTimer(2.0)!!.timeout.connect(this, Beetle::onDeathTimerTimeout)
    }

    @RegisterFunction
    fun onDeathTimerTimeout() {
        val puff = puffScene.instantiateAs<SmokePuff>()!!
        getParent()?.addChild(puff)
        puff.globalPosition = globalPosition
        puff.full.connect(this, Beetle::onPuffOver)
    }

    @RegisterFunction
    fun onPuffOver() {
        for (i in 0 until coinsCount) {
            val coin = coinScene.instantiateAs<Coin>()!!
            getParent()?.addChild(coin)
            coin.globalPosition = globalPosition
            coin.spawn()
        }
        queueFree()
    }

    @RegisterFunction
    fun onBodyEntered(body: Node) {
        if (body is Player) {
            target = body
            reactionAnimationPlayer.play(foundPlayerName)
        }
    }

    @RegisterFunction
    fun onBodyExited(body: Node) {
        if (body is Player) {
            target = null
            reactionAnimationPlayer.play(lostPlayerName)
            beetleSkin.idle()
        }
    }
}
