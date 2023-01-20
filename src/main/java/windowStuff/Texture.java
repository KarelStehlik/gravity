package windowStuff;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public class Texture {

  protected final int width, height;
  private final int id;

  public Texture(String path) {
    id = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, id);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // tutorial uses gl_nearest
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // tutorial uses gl_nearest

    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer channels = BufferUtils.createIntBuffer(1);

    ByteBuffer image = stbi_load(path, width, height, channels, 0);

    assert image != null : "image load failed: " + path;

    int format = channels.get(0) == 4 ? GL_RGBA : GL_RGB;

    this.width = width.get(0);
    this.height = height.get(0);

    glTexImage2D(GL_TEXTURE_2D, 0, format, this.width,
        this.height, 0, format, GL_UNSIGNED_BYTE, image);

    stbi_image_free(image);
  }

  public void bind() {
    glBindTexture(GL_TEXTURE_2D, id);
  }
}
