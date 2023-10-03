package Enemies.beetleBot

import godot.Animation
import godot.AnimationNodeStateMachinePlayback
import godot.AnimationPlayer
import godot.AnimationTree
import godot.Node3D
import godot.Timer
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.PackedStringArray
import godot.core.asStringName
import godot.global.GD

@RegisterClass
class BeetleBotSkin : Node3D() {

    @Export
    @RegisterProperty
    lateinit var forceLoop: PackedStringArray

    @Export
    @RegisterProperty
    lateinit var animationTree: AnimationTree

    @Export
    @RegisterProperty
    lateinit var player: AnimationPlayer

    @Export
    @RegisterProperty
    lateinit var secondaryActionTimer: Timer

    private lateinit var mainStateMachine: AnimationNodeStateMachinePlayback

    private val playbackName = "parameters/StateMachine/playback".asStringName()

    private val idleName = "Idle".asStringName()
    private val walkName = "Walk".asStringName()
    private val shakeName = "Shake".asStringName()
    private val attackName = "Attack".asStringName()
    private val powerOffName = "PowerOff".asStringName()

    @RegisterFunction
    override fun _ready() {
        animationTree.active = true
        mainStateMachine = animationTree.get(playbackName) as AnimationNodeStateMachinePlayback

        forceLoop.forEach { animationName ->
            val anim = player.getAnimation(animationName.asStringName())!!
            anim.loopMode = Animation.LoopMode.LOOP_LINEAR
        }
    }

    @RegisterFunction
    fun onSecondaryActionTimerTimeout() {
        if (mainStateMachine.getCurrentNode() == idleName) {
            shake()
        }
        secondaryActionTimer.start(GD.randdRange(3.0, 8.0))
    }

    @RegisterFunction
    fun idle() {
        mainStateMachine.travel(idleName)
    }

    @RegisterFunction
    fun walk() {
        mainStateMachine.travel(walkName)
    }

    @RegisterFunction
    fun shake() {
        mainStateMachine.travel(shakeName)
    }

    @RegisterFunction
    fun attack() {
        mainStateMachine.travel(attackName)
    }

    @RegisterFunction
    fun powerOff() {
        mainStateMachine.travel(powerOffName)
        secondaryActionTimer.stop()
    }
}