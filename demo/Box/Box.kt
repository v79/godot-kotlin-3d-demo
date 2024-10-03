package Box

import Player.Coin.Coin
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector3
import godot.coroutines.await
import godot.coroutines.awaitLoadAs
import godot.coroutines.awaitMainThread
import godot.coroutines.godotCoroutine
import godot.extensions.getNodeAs
import godot.extensions.instantiateAs
import godot.global.GD
import shared.Damageable

const val COIN_SCENE_PATH = "res://demo/Player/Coin/Coin.tscn"
const val COINS_COUNT = 5
const val DESTROYED_BOX_SCENE_PATH = "res://demo/Box/DestroyedBox.tscn"

@RegisterClass
class Box : RigidBody3D(), Damageable {
    private lateinit var destroySound: AudioStreamPlayer3D
    private lateinit var collisionShape: CollisionShape3D

    @RegisterFunction
    override fun _ready() {
        destroySound = getNodeAs("DestroySound")!!
        collisionShape = getNodeAs("CollisionShape3d")!!
    }

    @RegisterFunction
    override fun damage(impactPoint: Vector3, velocity: Vector3) {
        godotCoroutine {
            val destroyedBox = ResourceLoader.awaitLoadAs<PackedScene>(DESTROYED_BOX_SCENE_PATH)!!.instantiateAs<DestroyedBox>()!!
            awaitMainThread {
                getParent()?.addChild(destroyedBox)
                destroyedBox.globalPosition = globalPosition
            }

            val coinScene = ResourceLoader.awaitLoadAs<PackedScene>(COIN_SCENE_PATH)!!

            for (i in 0 until COINS_COUNT) {
                val coin = coinScene.instantiateAs<Coin>()!!
                awaitMainThread {
                    getParent()?.addChild(coin)
                    coin.globalPosition = globalPosition
                    coin.spawn()
                }
            }

            awaitMainThread {
                collisionShape.disabled = true

                destroySound.pitchScale = GD.randfn(1.0f, 0.1f)
                destroySound.play()
            }

            destroySound.finished.await()
            queueFree()
        }
    }
}