package general;

import static org.lwjgl.opengl.GL11C.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11C.glGetIntegerv;

import java.nio.IntBuffer;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

public final class Constants {

  public static final int VertexSizeFloats = 9;
  public static final int SpriteSizeFloats = 36;
  public static final Vector2i screenSize;

  static {
    IntBuffer b = BufferUtils.createIntBuffer(4);
    glGetIntegerv(GL_VIEWPORT, b);
    screenSize = new Vector2i(b.get(2), b.get(3));
  }

  private Constants() {
  }
}
