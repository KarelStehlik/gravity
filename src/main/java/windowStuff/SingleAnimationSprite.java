package windowStuff;

public class SingleAnimationSprite extends Sprite {

  private boolean ended = false;

  public SingleAnimationSprite(String anim, float duration, float sizeX, float sizeY, int layer,
      String shader) {
    this(anim, duration, 0, 0, sizeX, sizeY, layer, shader);
  }

  public SingleAnimationSprite(String anim, float duration, float sizeX, float sizeY, int layer) {
    this(anim, duration, 0, 0, sizeX, sizeY, layer, "basic");
  }

  public SingleAnimationSprite(String anim, float duration, float x, float y, float sizeX,
      float sizeY,
      int layer, String shader) {
    super(anim + "-0", x, y, sizeX, sizeY, layer, shader);
    playAnimation(new BasicAnimation(anim, duration));
  }

  public boolean animationEnded() {
    return ended;
  }

  @Override
  protected void onAnimationEnd() {
    ended = true;
    delete();
  }
}
