package windowStuff;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_RENDERER;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glGetString;

import general.Constants;
import general.Data;
import java.util.Collection;
import java.util.LinkedList;
import org.joml.Vector3f;

public final class Graphics {

  private final Collection<BatchSystem> batchSystems = new LinkedList<>();

  public void init() {
    Data.init();
    System.out.println(Constants.screenSize); // this is needed to load Constants
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    System.out.println(glGetString(GL_RENDERER));
  }

  public void addBatchSystem(BatchSystem bs) {
    batchSystems.add(bs);
    bs.useCamera(new Camera(new Vector3f(0, 0, 20)));
  }

  public void redraw(double dt) {
    Data.updateShaders();

    for (BatchSystem bs : batchSystems) {
      bs.draw();
    }
  }
}
