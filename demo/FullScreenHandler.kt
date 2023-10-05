import godot.Input
import godot.InputEvent
import godot.InputEventKey
import godot.InputEventMouseButton
import godot.Key
import godot.Node
import godot.OS
import godot.Window
import godot.annotation.RegisterClass

@RegisterClass
class FullScreenHandler : Node() {

    init {
        processMode = ProcessMode.PROCESS_MODE_ALWAYS
    }

    override fun _input(event: InputEvent) {
        if (OS.hasFeature("HTML5")) {
            if (event is InputEventMouseButton && Input.getMouseMode() != Input.MouseMode.MOUSE_MODE_CAPTURED) {
                Input.setMouseMode(Input.MouseMode.MOUSE_MODE_CAPTURED)
            }
        } else {
            if (event is InputEventKey && event.isPressed()) {
                if (event.keycode == Key.KEY_F11 || (event.keycode == Key.KEY_ENTER && event.altPressed)) {
                    getTree()?.root?.mode = if (getTree()?.root?.mode == Window.Mode.MODE_FULLSCREEN) {
                        Window.Mode.MODE_WINDOWED
                    } else {
                        Window.Mode.MODE_FULLSCREEN
                    }
                }
            }
        }
    }
}
