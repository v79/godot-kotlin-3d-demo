package Player.Coin.CoinVisuals

import godot.Node3D
import godot.Time
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.global.GD

@RegisterClass
class CoinModel : Node3D() {

    @Export
    @RegisterProperty
    var yAmplitude = 0.04

    @RegisterFunction
    override fun _process(delta: Double) {
        val t = Time.getTicksMsec().toDouble() / 1000.0
        rotation.y += 1.5 * delta
        position.y = GD.sin(t) * yAmplitude
    }
}
