package windowStuff;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.Arrays;

public class MouseListener {

  private final boolean[] buttonsPressed;
  private double x, y, dx, dy, lastX, lastY, scrollX, scrollY;
  private boolean dragging;

  public MouseListener() {
    x = 0;
    y = 0;
    dx = 0;
    dy = 0;
    lastX = 0;
    lastY = 0;
    scrollX = 0;
    scrollY = 0;
    dragging = false;
    buttonsPressed = new boolean[5];
  }

  public float getX() {
    return (float) x;
  }

  public float getY() {
    return (float) y;
  }

  public float getDx() {
    return (float) dx;
  }

  public float getDy() {
    return (float) dy;
  }

  public float getScrollX() {
    return (float) scrollX;
  }

  public float getScrollY() {
    return (float) scrollY;
  }

  public boolean isDragging() {
    return dragging;
  }

  public boolean isPressed(int button) {
    return buttonsPressed[button];
  }

  public void mousePosCallback(long window, double newX, double newY) {
    lastX = x;
    lastY = y;
    x = newX;
    y = newY;
    dx = newX - lastX;
    dy = newY - lastY;
    dragging = Arrays.asList(buttonsPressed, 5).contains(true);
  }

  public void mouseButtonCallback(long window, int button, int action, int mods) {
    if (button > buttonsPressed.length) {
      return;
    }
    if (action == GLFW_PRESS) {
      buttonsPressed[button] = true;
    } else if (action == GLFW_RELEASE) {
      buttonsPressed[button] = false;
      dragging = false;
    }
  }

  public void scrollCallback(long window, double xOffset, double yOffset) {
    scrollX = xOffset;
    scrollY = yOffset;
  }

  public void endFrame() {
    scrollX = 0;
    scrollY = 0;
    dx = 0;
    dy = 0;
  }


  @Override
  public String toString() {
    return "MouseListener{"
        + "x=" + x
        + ", y=" + y
        + ", dx=" + dx
        + ", dy=" + dy
        + ", lastX=" + lastX
        + ", lastY=" + lastY
        + ", scrollX=" + scrollX
        + ", scrollY=" + scrollY
        + '}';
  }
}
