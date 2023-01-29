package main;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import windowStuff.BatchSystem;
import windowStuff.Sprite;

public class Tree implements GravitySimulator {

  static final int maxDepth = 9;
  static final int planetsBeforeSplit = 10;
  static int maxSize = 25000;
  final BatchSystem bs;
  final List<Sprite> boxes = new ArrayList<>(1);
  private boolean visible = false;
  private int spritesUsed = 0;

  public Tree(BatchSystem bs) {
    this.bs = bs;
  }

  private static Planet quickSelect(List<Planet> planets, int index, Key key) {
    Planet pivot = planets.get(0);
    List<Planet> lesser = new ArrayList<>(planets.size() / 2), equal = new ArrayList<>(
        1), greater = new ArrayList<>(planets.size() / 2);
    for (Planet p : planets) {
      if (key.get(p) > key.get(pivot)) {
        greater.add(p);
      } else if (key.get(p) < key.get(pivot)) {
        lesser.add(p);
      } else {
        equal.add(p);
      }
    }
    if (lesser.size() > index) {
      return quickSelect(lesser, index, key);
    }
    if (lesser.size() + equal.size() > index) {
      return equal.get(0);
    }
    return quickSelect(greater, index - (lesser.size() + equal.size()), key);
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  private void addTempSprite(float x, float y, float size) {
    if (boxes.size() <= spritesUsed) {
      Sprite s = new Sprite("Box", x, y, size * 2, size * 2, 0, "basic").addToBs(bs);
      //s.setColors(new float[]{
      //    .3f, .3f, .3f, 1,
      //    .3f, .3f, .3f, 1,
      //    1, 1, 1, 1,
      //    0, 0, 0, 1
      //});
      boxes.add(s);
    } else {
      boxes.get(spritesUsed).setPosition(x, y);
      boxes.get(spritesUsed).setSize(size * 2, size * 2);
    }
    spritesUsed += 1;
  }

  @Override
  public void simulate(List<? extends Planet> world, float speed, float G, int worldSize,
      int collisionEvent) {
    for (int i = spritesUsed; i < boxes.size(); i++) {
      boxes.get(i).setSize(0, 0);
    }
    spritesUsed = 0;

    if (world.isEmpty()) {
      return;
    }
    for (Planet p : world) {
      if (p.x > worldSize || p.x < -worldSize || p.y > worldSize || p.y < -worldSize) {
        p.delete();
      }
    }
    world.removeIf(Planet::WasDeleted);
    maxSize = worldSize;
    Node main = new Node(0, 0, 0);
    for (Planet p : world) {
      main.add(p);
    }
    main.finish();
    for (Planet p : world) {
      Vector2f gravity = main.step(p, 100 + p.size, G, collisionEvent);
      if (Float.isNaN(gravity.x)) {
        System.out.println(p.x + "  " + p.y);
        System.out.println(main);
        assert false;
      }
      p.accelerate(gravity.x * speed, gravity.y * speed);
      p.onGameTick(speed);
    }
  }


  @FunctionalInterface
  private interface Key {

    float get(Planet planet);
  }

  class Node {

    private final float x, y, size;
    private final int depth;
    private final List<Planet> planets = new ArrayList<>(planetsBeforeSplit);
    private final Vector2f centreOfMass = new Vector2f(0, 0);
    private Node bottomLeft, bottomRight, topLeft, topRight;
    private float mass = 0;

    Node(int depth, float x, float y) {
      this.x = x;
      this.y = y;
      this.size = maxSize >> depth;
      this.depth = depth;
    }

    @Override
    public String toString() {
      if (bottomLeft == null) {
        StringBuilder result = new StringBuilder("contains planets at: ");
        for (Planet p : planets) {
          result.append((int) p.x).append(',').append((int) p.y).append(' ');
        }
        return result.toString();
      }
      String result = x + "," + y + "{\n";
      result += "    " + bottomLeft;
      result += "\n    " + bottomRight;
      result += "\n    " + topLeft;
      result += "\n    " + topRight;
      return result + "\n}";
    }

    void subdivide() {
      bottomLeft = new Node(depth + 1, x - size / 2, y - size / 2);
      bottomRight = new Node(depth + 1, x + size / 2, y - size / 2);
      topLeft = new Node(depth + 1, x - size / 2, y + size / 2);
      topRight = new Node(depth + 1, x + size / 2, y + size / 2);
      for (Planet p : planets) {
        if (p.x < x && p.y < y) {
          bottomLeft.add(p);
        } else if (p.x < x) {
          topLeft.add(p);
        } else if (p.y < y) {
          bottomRight.add(p);
        } else {
          topRight.add(p);
        }
      }
      planets.clear();
    }

    Vector2f step(Planet target, float highAccuracyRange, float G, int collision) {
      if (mass == 0) {
        return new Vector2f(0, 0);
      }
      if (Math.abs(target.x - x) < highAccuracyRange + size
          && Math.abs(target.y - y) < highAccuracyRange + size) {
        if (bottomLeft != null) { //node is split
          return bottomLeft.step(target, highAccuracyRange, G, collision).add(
              bottomRight.step(target, highAccuracyRange, G, collision)).add(
              topLeft.step(target, highAccuracyRange, G, collision)).add(
              topRight.step(target, highAccuracyRange, G, collision));
        }
        Vector2f result = new Vector2f(0, 0);
        for (Planet p : planets) {
          if (p != target) {
            if (Math.hypot(p.x - target.x, p.y - target.y) * 2 < p.size + target.size) {
              target.collide(p, collision);
            } else {
              double dist = Math.hypot(p.x - target.x, p.y - target.y);
              float totalForce = (float) (p.mass / (dist * dist * dist)) * G;
              result = result.add(
                  new Vector2f(totalForce * (p.x - target.x), totalForce * (p.y - target.y)));
            }
          }
        }
        return result;
      }
      double dist = Math.hypot(centreOfMass.x - target.x, centreOfMass.y - target.y);
      float totalForce = (float) (mass / (dist * dist * dist)) * G;
      return new Vector2f(totalForce * (centreOfMass.x - target.x),
          totalForce * (centreOfMass.y - target.y));
    }

    void add(Planet p) {
      centreOfMass.x += p.x * p.mass;
      centreOfMass.y += p.y * p.mass;
      mass += p.mass;

      if (bottomLeft != null) {  //node is split
        if (p.x < x && p.y < y) {
          bottomLeft.add(p);
        } else if (p.x < x) {
          topLeft.add(p);
        } else if (p.y < y) {
          bottomRight.add(p);
        } else {
          topRight.add(p);
        }
        return;
      }

      if (planets.size() < planetsBeforeSplit || depth == maxDepth) {
        planets.add(p);
      } else {
        subdivide();
        add(p);
      }
    }

    void finish() {
      if (visible && bottomLeft == null) {
        addTempSprite(x, y, size);
      }
      if (mass == 0) {
        return;
      }
      centreOfMass.div(mass);
      if (bottomLeft != null) {
        bottomLeft.finish();
        bottomRight.finish();
        topLeft.finish();
        topRight.finish();
      }
    }
  }
}
