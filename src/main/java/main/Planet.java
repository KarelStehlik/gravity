package main;

import org.joml.Vector2f;
import windowStuff.Sprite;
import windowStuff.TickDetect;

public class Planet implements TickDetect {

  private final Sprite sprite;
  public float mass, size;
  public float x, y, vx, vy;
  private boolean locked = false;

  public Planet(Sprite sprite, float x, float y, float mass, float size) {
    this.sprite = sprite;
    this.x = x;
    this.y = y;
    this.mass = mass;
    this.size = size;
    sprite.setSize(size, size);
    sprite.setPosition(x, y);
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean val) {
    locked = val;
  }

  public void applyForce(float xComponent, float yComponent) {
    vx += xComponent / mass;
    vy += yComponent / mass;
  }

  public void accelerate(float xComponent, float yComponent) {
    vx += xComponent;
    vy += yComponent;
  }

  @Override
  public void onGameTick(float dt) {
    if (locked) {
      vx = 0;
      vy = 0;
    }
    x += vx * dt;
    y += vy * dt;
    sprite.setPosition(x, y);
  }

  @Override
  public void delete() {
    sprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted();
  }

  public void collide(Planet other, int type) {
    if (other.WasDeleted() || this.WasDeleted() || (other.isLocked()&&!isLocked())) {
      return;
    }
    if (type == 0) {
      return;
    }
    other.delete();

    float momentumX = mass * vx + other.mass * other.vx;
    float momentumY = mass * vy + other.mass * other.vy;
    Vector2f centreOfMass = new Vector2f((mass * x + other.mass * other.x) / (mass + other.mass),
        (mass * y + other.mass * other.y) / (mass + other.mass));

    size = (float) Math.hypot(size, other.size);
    sprite.setSize(size, size);
    mass = mass + other.mass;

    vx = momentumX / mass;
    vy = momentumY / mass;
    x = centreOfMass.x;
    y = centreOfMass.y;
  }
}
