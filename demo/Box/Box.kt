package Box

import Player.Coin.Coin
import godot.AudioStreamPlayer3D
import godot.CollisionShape3D
import godot.PackedScene
import godot.ResourceLoader
import godot.RigidBody3D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.StringName
import godot.core.Vector3
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.extensions.loadAs
import godot.global.GD
import shared.Damageable

const val COIN_SCENE_PATH = "res://demo/Player/Coin/Coin.tscn"
const val COINS_COUNT = 5
const val DESTROYED_BOX_SCENE_PATH = "res://demo/Box/DestroyedBox.tscn"

@RegisterClass
class Box : RigidBody3D(), Damageable {
    private val disableName = StringName("disabled")

    private lateinit var destroySound: AudioStreamPlayer3D
    private lateinit var collisionShape: CollisionShape3D

    @RegisterFunction
    override fun _ready() {
        destroySound = getNodeAs("DestroySound")!!
        collisionShape = getNodeAs("CollisionShape3d")!!
    }

    @RegisterFunction
    override fun damage(impactPoint: Vector3, force: Vector3) {
        for (i in 0 until COINS_COUNT) {
            val coin = ResourceLoader.loadAs<PackedScene>(COIN_SCENE_PATH)!!.instanceAs<Coin>()!!
            getParent()?.addChild(coin)
            coin.globalPosition = globalPosition
            coin.spawn()
        }

        val destroyedBox = ResourceLoader.loadAs<PackedScene>(DESTROYED_BOX_SCENE_PATH)!!.instanceAs<DestroyedBox>()!!
        getParent()?.addChild(destroyedBox)
        destroyedBox.globalPosition = globalPosition

        collisionShape.setDeferred(disableName, true)
        destroySound.pitchScale = GD.randfn(1.0f, 0.1f)
        destroySound.play()
        destroySound.finished.connect(this, Box::queueFree)
    }
}