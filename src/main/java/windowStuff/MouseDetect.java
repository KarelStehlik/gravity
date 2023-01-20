package windowStuff;

public interface MouseDetect {

  void onMouseButton(int button, double x, double y, int action, int mods);

  void onScroll(double scroll);

  void onMouseMove(float newX, float newY);

  void delete();

  boolean WasDeleted();
}
