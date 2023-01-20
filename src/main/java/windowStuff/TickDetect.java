package windowStuff;

public interface TickDetect {

  void onGameTick(int tick);

  void delete();

  boolean WasDeleted();
}
