package Enemies.smoke_puff

import godot.AnimationPlayer
import godot.AudioStreamPlayer3D
import godot.Node
import godot.Node3D
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.core.Callable
import godot.core.asStringName
import godot.signals.signal

@RegisterClass
class SmokePuff : Node3D() {

    @RegisterSignal
    val full by signal()

    @Export
    @RegisterProperty
    lateinit var smokeSoundsRoot: Node

    @Export
    @RegisterProperty
    lateinit var player: AnimationPlayer

    private val poofName = "poof".asStringName()
    private val queueFreeName = "queue_free".asStringName()

    @RegisterFunction
    override fun _ready() {
        (smokeSoundsRoot.getChildren().random() as AudioStreamPlayer3D).play()

        player.play(poofName)
        val callable = Callable(this, queueFreeName).unbind(1)
        player.animationFinished.connect(callable)
    }

    @RegisterFunction
    fun smokeAtFullDensity() {
        full.emit()
    }
}