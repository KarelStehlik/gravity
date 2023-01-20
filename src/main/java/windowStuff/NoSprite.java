package windowStuff;

import windowStuff.Sprite.Animation;

public class NoSprite implements AbstractSprite {

  boolean deleted = false;
  float x,y,w,h;

  @Override
  public float getWidth() {
    return w;
  }

  @Override
  public float getHeight() {
    return h;
  }

  @Override
  public NoSprite playAnimation(Animation anim) {
    return this;
  }

  @Override
  public NoSprite setShader(String shader) {
    return this;
  }

  @Override
  public float getRotation() {
    return 0;
  }

  @Override
  public NoSprite setRotation(float r) {
    return this;
  }

  @Override
  public NoSprite setImage(String name) {
    return this;
  }

  @Override
  public NoSprite setPosition(float X, float Y) {
    x=X;y=Y;return this;
  }

  @Override
  public NoSprite scale(float multiplier) {
    w*=multiplier;h*=multiplier;return this;
  }

  @Override
  public NoSprite setSize(float w, float h) {
    this.w=w/2;this.h=h/2;return this;
  }

  @Override
  public NoSprite setColors(float[] colors) {
    return this;
  }

  @Override
  public void delete() {
    deleted = true;
  }

  @Override
  public float getX() {
    return x;
  }

  @Override
  public NoSprite setX(float x) {
    this.x=x;return this;
  }

  @Override
  public float getY() {
    return y;
  }

  @Override
  public NoSprite setY(float y) {
    this.y=y;return this;
  }

  @Override
  public Batch getBatch() {
    return null;
  }

  @Override
  public int getLayer() {
    return 0;
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  @Override
  public NoSprite addToBs(BatchSystem bs) {
    return this;
  }

  @Override
  public void unBatch() {

  }
}
