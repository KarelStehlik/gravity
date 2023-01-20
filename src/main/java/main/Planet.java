package main;

import windowStuff.Sprite;
import windowStuff.TickDetect;

public class Planet implements TickDetect {
  public final float mass, size;
  public float x, y, vx, vy;
  private Sprite sprite;

  public Planet(Sprite sprite, float x, float y, float mass, float size){
    this.sprite = sprite;
    this.x=x;
    this.y=y;
    this.mass=mass;
    this.size=size;
    sprite.setSize(size, size);
    sprite.setPosition(x, y);
  }

  public void applyForce(float xComponent, float yComponent){
    vx+=xComponent/mass;
    vy+=yComponent/mass;
  }

  @Override
  public void onGameTick(int tick) {
    x+=vx;
    y+=vy;
    sprite.setPosition(x,y);
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
