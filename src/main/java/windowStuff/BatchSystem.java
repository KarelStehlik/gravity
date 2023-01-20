package windowStuff;

import static windowStuff.Batch.MAX_BATCH_SIZE;

import general.Data;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BatchSystem {

  //private final List<Batch> batches;

  protected final List<Batch> batches;

  private final List<Sprite> spritesToAdd = new LinkedList<>();

  private Camera camera;

  private boolean paused = false, visible = true;

  public BatchSystem() {
    //batches = new LinkedList<>(); // is sorted
    Collection<Shader> shaders = Data.getAllShaders();
    batches = new LinkedList<>();
  }

  public void pause() {
    paused = true;
  }

  public void unpause() {
    paused = false;
  }

  public void show() {
    visible = true;
  }

  public void hide() {
    visible = false;
  }

  public void addSprite(Sprite sprite) {
    sprite.bsToJoin = this;
    synchronized (spritesToAdd) {
      spritesToAdd.add(sprite);
    }
  }

  private void _addSprite(Sprite sprite) {
    assert sprite.batch == null : "Sprite already has a batch";
    if (sprite.deleted || !Objects.equals(sprite.bsToJoin, this)) {
      return;
    }
    sprite.bsToJoin = null;
    // find an available batch, if it exists
    int index = 0; // at which index do the batches have the correct layer?
    for (Batch batch : batches) {
      if (batch.layer < sprite.layer) {
        index++;
        continue;
      }
      if (batch.layer > sprite.layer) {
        break;
      }
      if (batch.textureName.equals(sprite.textureName) && !batch.freeSpriteSlots.isEmpty()
          && Objects.equals(
          batch.shader, sprite.shader)) {
        batch.addSprite(sprite);
        return;
      }
    }

    // available batch does not exist, create one
    Batch batch = new Batch(sprite.textureName, MAX_BATCH_SIZE, sprite.shader.name, sprite.layer,
        this);
    batch.addSprite(sprite);
    batches.add(index, batch); // keep the list sorted
  }


  public void draw() {
    synchronized (spritesToAdd) {
      while (!spritesToAdd.isEmpty()) {
        _addSprite(
            spritesToAdd.remove(0)); // do this in the graphics thread so that context is current
      }
    }

    if (!visible) {
      return;
    }

    if (!paused) {
      var iter = batches.iterator();
      while (iter.hasNext()) {
        Batch batch = iter.next();
        batch.update();
        if (batch.isEmpty) {
          batch.delete();
          iter.remove();
        }
      }
    }

    for (Shader shader : Data.getAllShaders()) {
      shader.useCamera(camera);
    }
    for (Batch batch : batches) {
      batch.draw();
    }

  }


  public void useCamera(Camera camera) {
    this.camera = camera;
  }

  public Camera getCamera() {
    return camera;
  }
}