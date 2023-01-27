package main;

import java.util.List;

public interface GravitySimulator {

  void simulate(List<? extends Planet> world, float speed, float G);
}
