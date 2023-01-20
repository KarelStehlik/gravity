package windowStuff;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class KeyListener {

  private final boolean[] keysPressed = new boolean[350];

  public void keyCallback(long window, int key, int scancode, int action, int mods) {
    keysPressed[key] = action == GLFW_PRESS;
    onKeyPress(key, scancode, action, mods);
  }

  private void onKeyPress(int key, int scancode, int action, int mods) {
  }

  public boolean isPressed(int key) {
    return keysPressed[key];
  }

  @Override
  public String toString() {
    return "KeyListener{}";
  }
}
