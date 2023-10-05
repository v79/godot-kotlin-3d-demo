package Level

import Player.Player
import godot.Area3D
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class DeathPlane : Area3D() {

    @RegisterFunction
    override fun _ready() {
        bodyEntered.connect( this, DeathPlane::onBodyEntered)
    }

    @RegisterFunction
    fun onBodyEntered(body: Node) {
        if (body is Player) {
            body.resetPosition()
        }
    }
}
