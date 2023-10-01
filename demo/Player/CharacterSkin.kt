package Player

import godot.AnimationNodeOneShot
import godot.AnimationNodeStateMachinePlayback
import godot.AnimationPlayer
import godot.AnimationTree
import godot.Node3D
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.core.asStringName
import godot.signals.signal

@RegisterClass
class CharacterSkin: Node3D() {

    @RegisterSignal
    val footStep by signal()

    @Export
    @RegisterProperty
    lateinit var mainAnimationPlayer: AnimationPlayer

    @Export
    @RegisterProperty
    lateinit var animationTree: AnimationTree

    private val stateMachine: AnimationNodeStateMachinePlayback by lazy {
        animationTree.get("parameters/StateMachine/playback".asStringName()) as AnimationNodeStateMachinePlayback
    }

    private var movingBlendPath = "parameters/StateMachine/move/blend_position".asStringName()
    private var punchOneShotPath = "parameters/PunchOneShot/request".asStringName()
    private var idleAnimation = "idle".asStringName()
    private var moveAnimation = "move".asStringName()
    private var jumpAnimation = "jump".asStringName()
    private var fallAnimation = "fall".asStringName()

    @RegisterFunction
    override fun _ready() {
        animationTree.active = true
        mainAnimationPlayer.playbackDefaultBlendTime = 0.1
    }

    @RegisterFunction
    fun setMoving(isMoving: Boolean) {
        if (isMoving) {
            stateMachine.travel(moveAnimation)
        } else {
            stateMachine.travel(idleAnimation)
        }
    }

    @RegisterFunction
    fun setMovingSpeed(speed: Double) {
        animationTree.set(movingBlendPath, speed)
    }

    @RegisterFunction
    fun jump() {
        stateMachine.travel(jumpAnimation)
    }

    @RegisterFunction
    fun fall() {
        stateMachine.travel(fallAnimation)
    }

    @RegisterFunction
    fun punch() {
        animationTree.set(punchOneShotPath, AnimationNodeOneShot.OneShotRequest.ONE_SHOT_REQUEST_FIRE.id)
    }
}