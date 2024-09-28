package Level

import Player.Player
import godot.Area3D
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.connect

@RegisterClass
class DeathPlane : Area3D() {

    @RegisterFunction
    override fun _ready() {
        bodyEntered.connect {
            if (it is Player) {
                it.resetPosition()
            }
        }
    }
}
