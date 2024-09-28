package Enemies

import Enemies.smoke_puff.SmokePuff
import Player.Coin.Coin
import godot.PackedScene
import godot.ResourceLoader
import godot.RigidBody3D
import godot.annotation.Export
import godot.annotation.RegisterProperty
import godot.coroutines.GodotCoroutine
import godot.coroutines.await
import godot.coroutines.awaitDeferred
import godot.extensions.instantiateAs
import godot.extensions.loadAs
import shared.Damageable

abstract class Enemy : RigidBody3D(), Damageable {
    @Export
    @RegisterProperty
    var coinsCount = 5

    private val puffScene = ResourceLoader.loadAs<PackedScene>("res://demo/Enemies/smoke_puff/smoke_puff.tscn")!!
    private val coinScene = ResourceLoader.loadAs<PackedScene>("res://demo/Player/Coin/Coin.tscn")!!

    fun death() {
        val timer = getTree()!!.createTimer(2.0)!!
        GodotCoroutine {
            timer.timeout.await()

            val puff = puffScene.instantiateAs<SmokePuff>()!!
            awaitDeferred {
                getParent()?.addChild(puff)
                puff.globalPosition = globalPosition
            }

            for (i in 0 until coinsCount) {
                val coin = coinScene.instantiateAs<Coin>()!!
                awaitDeferred {
                    getParent()?.addChild(coin)
                    coin.globalPosition = globalPosition
                    coin.spawn()
                }
            }
            queueFree()
        }
    }
}