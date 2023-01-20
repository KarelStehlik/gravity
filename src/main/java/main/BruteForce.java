package main;

import general.Constants;
import java.util.List;
import java.util.Objects;

public class BruteForce implements GravitySimulator{

  @Override
  public void simulate(List<Planet> world) {
    for(Planet p1:world){
      for(Planet p2:world){
        if(Objects.equals(p1, p2) || (p1.x==p2.x && p1.y==p2.y)){
          continue;
        }
        float distance = (float)Math.hypot(p1.x-p2.x, p1.y-p2.y);
        float totalForce = p1.mass * p2.mass * Constants.G / (distance*distance*distance);
        p2.applyForce((p1.x-p2.x)*totalForce, (p1.y-p2.y)*totalForce);
      }
    }
  }
}
