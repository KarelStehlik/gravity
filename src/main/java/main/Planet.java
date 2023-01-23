package main;

import windowStuff.Sprite;
import windowStuff.TickDetect;

public class Planet implements TickDetect {

  public final float mass, size;
  public float x, y, vx, vy;
  private final Sprite sprite;
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
}
