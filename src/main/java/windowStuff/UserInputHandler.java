package windowStuff;

public interface UserInputHandler {

  void onMouseMove(double newX, double newY);

  void onMouseButton(int button, int action, int mods);

  void onScroll(double xOffset, double yOffset);

  void onKeyPress(int key, int action, int mods);
}
