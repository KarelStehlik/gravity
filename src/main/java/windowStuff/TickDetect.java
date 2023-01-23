package windowStuff;

public interface TickDetect {

  void onGameTick(float dt);

  void delete();

  boolean WasDeleted();
}
