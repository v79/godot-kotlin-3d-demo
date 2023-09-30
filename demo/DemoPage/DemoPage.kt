package DemoPage

import godot.Button
import godot.Control
import godot.GridContainer
import godot.Input
import godot.InputEvent
import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Color
import godot.core.asStringName
import godot.core.callable
import godot.extensions.asNodePath

@RegisterClass
class DemoPage : Node() {
    enum class InstructionType {
        KEYBOARD,
        JOYPAD
    }

    @Export
    @RegisterProperty
    lateinit var demoPageRoot: Control

    @Export
    @RegisterProperty
    lateinit var resumeButton: Button

    @Export
    @RegisterProperty
    lateinit var exitButton: Button

    @Export
    @RegisterProperty
    lateinit var keyboardButton: Button

    @Export
    @RegisterProperty
    lateinit var joypadButton: Button

    @Export
    @RegisterProperty
    lateinit var gridContainerKeyboard: GridContainer

    @Export
    @RegisterProperty
    lateinit var gridContainerJoypad: GridContainer

    private var demoMouseMode: Input.MouseMode = Input.MouseMode.MOUSE_MODE_VISIBLE

    @RegisterFunction
    override fun _ready() {
        val tree = getTree() ?: return
        tree.paused = true
        demoMouseMode = Input.getMouseMode()
        Input.setMouseMode(Input.MouseMode.MOUSE_MODE_VISIBLE)

        resumeButton.pressed.connect(this, ::resumeDemo)
        exitButton.pressed.connect(callable { getTree()?.quit() })
        keyboardButton.pressed.connect(callable { changeInstruction(InstructionType.KEYBOARD) })
        joypadButton.pressed.connect(callable { changeInstruction(InstructionType.JOYPAD) })

        changeInstruction(
            if (Input.getConnectedJoypads().isNotEmpty()) {
                InstructionType.JOYPAD
            } else {
                InstructionType.KEYBOARD
            }
        )
    }

    @RegisterFunction
    override fun _input(event: InputEvent) {
        if (event.isActionPressed("pause".asStringName()) && !event.isEcho()) {
            if (getTree()?.paused == true) {
                resumeDemo()
            } else {
                pauseDemo()
            }
        }
    }

    @RegisterFunction
    fun changeInstruction(type: InstructionType) {
        when(type) {
            InstructionType.KEYBOARD -> {
                keyboardButton.modulate = keyboardButton.modulate.apply { a = 1.0 }
                joypadButton.modulate = keyboardButton.modulate.apply { a = 0.3 }
                gridContainerKeyboard.show()
                gridContainerJoypad.hide()
            }
            InstructionType.JOYPAD -> {
                keyboardButton.modulate = keyboardButton.modulate.apply { a = 0.3 }
                joypadButton.modulate = keyboardButton.modulate.apply { a = 1.0 }
                gridContainerKeyboard.hide()
                gridContainerJoypad.show()
            }
        }

        keyboardButton.releaseFocus()
        joypadButton.releaseFocus()
    }

    private fun pauseDemo() {
        demoMouseMode = Input.getMouseMode()
        getTree()?.let { it.paused = true }
        demoPageRoot.show()
        createTween()?.tweenProperty(demoPageRoot, demoPageRoot::modulate.name.asNodePath(), Color.white, 0.3)
        Input.setMouseMode(Input.MouseMode.MOUSE_MODE_VISIBLE)
    }

    @RegisterFunction
    fun resumeDemo() {
        getTree()?.let { it.paused = false }
        createTween()?.apply {
            tweenProperty(demoPageRoot, demoPageRoot::modulate.name.asNodePath(), Color.transparent, 0.3)
            tweenCallback(callable { demoPageRoot.hide() })
        }
        Input.setMouseMode(demoMouseMode)
    }
}

