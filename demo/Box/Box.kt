package Box

import Player.Coin.Coin
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector3
import godot.core.asCachedStringName
import godot.coroutines.GodotCoroutine
import godot.coroutines.awaitDeferred
import godot.coroutines.awaitLoadAs
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
    override fun damage(impactPoint: Vector3, force: Vector3) {
        GodotCoroutine {
            val destroyedBox = ResourceLoader.awaitLoadAs<PackedScene>(DESTROYED_BOX_SCENE_PATH)!!.instantiateAs<DestroyedBox>()!!
            awaitDeferred {
                getParent()?.addChild(destroyedBox)
                destroyedBox.globalPosition = globalPosition
            }

            val coinScene = ResourceLoader.awaitLoadAs<PackedScene>(COIN_SCENE_PATH)!!

            for (i in 0 until COINS_COUNT) {
                val coin = coinScene.instantiateAs<Coin>()!!
                awaitDeferred {
                    getParent()?.addChild(coin)
                    coin.globalPosition = globalPosition
                    coin.spawn()
                }
            }
        }

        collisionShape.setDeferred("disabled".asCachedStringName(), true)
        destroySound.pitchScale = GD.randfn(1.0f, 0.1f)
        destroySound.play()
        destroySound.finished.connect(this, Box::queueFree)
    }
}