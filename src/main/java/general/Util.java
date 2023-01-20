package general;

import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public final class Util {

  // number of table entries per degree
  private static final int sinScale = 32;
  // float array that will store the sine values
  private static final float[] sin = new float[(90 * sinScale) + 1];
  // number of table entries
  private static final int arcSinScale = 2048;
  // float array that will store the sine values
  private static final float[] arcSin = new float[arcSinScale + 1];
  private static final float[] cycleColors = new float[]{
      0, 0, 3, 1,
      0, 1.5f, 1.5f, 1,
      3, 0, 0, 1,
      3, 0, 0, 1,
  };
  private static final float[] cycle2colors = new float[]{
      0, 1, .5f, 1,
      1, 0, .5f, 1,
      0, 0, .5f, 1,
      1, 1, .5f, 1,
  };

  private static final float[] noColors = new float[]{
      0, 0, 0, 1,
      0, 0, 0, 1,
      0, 0, 0, 1,
      0, 0, 0, 1,
  };

  // static initializer block
  // fill the sine look-up table
  static {
    double toRadian = Math.PI / (180.0 * sinScale);
    for (int i = 0; i < sin.length; i++) {
      sin[i] = (float) Math.sin(i * toRadian);
    }
  }

  // static initializer block
  // fill the sine look-up table
  static {
    double toDeg = 180 / Math.PI;
    double step = 1.0 / arcSinScale;
    for (int i = 0; i < arcSin.length; i++) {
      arcSin[i] = (float) (Math.asin(i * step) * toDeg);
    }
  }

  public static float sin(float a) {
    // Limit range if needed.
    if (a > 360) {
      a %= 360;
    } else if (a < 0) {
      a += 360 * ((int) (Math.abs(a) / 360) + 1);
    }
    // compute the index
    int angleIndex = (int) (a * sinScale);
    if (angleIndex < (180 * sinScale) + 1) {
      if (angleIndex < (90 * sinScale) + 1) {
        return sin[angleIndex];
      }
      return sin[(180 * sinScale) - angleIndex];
    }
    if (angleIndex < (270 * sinScale) + 1) {
      return -sin[angleIndex - (180 * sinScale)];
    }
    return -sin[(360 * sinScale) - angleIndex];
  }

  public static float cos(float a) {
    return sin(a + 90);
  }

  public static float arcSin(float a) {
    if (a >= 0) {
      int index = (int) (a * arcSinScale);
      return arcSin[index];
    } else {
      int index = (int) (-a * arcSinScale);
      return -arcSin[index];
    }
  }

  public static float arcCos(float a) {
    return arcSin(a - 90);
  }

  public static FloatBuffer buffer(float[] input) {
    FloatBuffer out = BufferUtils.createFloatBuffer(input.length);
    return out.put(input);
  }

  public static boolean testBit(int input, int bit) {
    return (input & (1 << (bit - 1))) != 0;
  }

  public static IntBuffer buffer(int[] input) {
    IntBuffer out = BufferUtils.createIntBuffer(input.length);
    return out.put(input);
  }

  public static float get_rotation(float x, float y) {
    float inv_hypot = 1 / (float) Math.sqrt(x * x + y * y);
    float asin = arcSin(Math.max(Math.min(y * inv_hypot, 1), -1));
    if (x >= 0) {
      return asin;
    }
    return 180 - asin;
  }

  public static float[] getRandomColors() {
    return new float[]{
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
    };
  }

  public static float[] getCycleColors() {
    return cycleColors;
  }

  public static float[] getCycleColors(float strength) {
    float[] result = cycleColors.clone();
    for (int i = 0; i < 12; i++) {
      result[i + i / 3] *= strength;
    }
    return result;
  }

  public static float[] getCycle2colors() {
    return cycle2colors;
  }

  public static float[] getCycle2colors(float strength) {
    return new float[]{
        0, 10, strength, 1,
        10, 0, strength, 1,
        0, 0, strength, 1,
        10, 10, strength, 1,
    };
  }

  public static boolean rectIntersect(Rectangle a, Rectangle b) {
    return ((a.x > b.x + b.width) != (a.x + a.width > b.y)) &&
        ((a.y > b.y + b.height) != (a.y + a.height > b.y));
  }

  public static float square(float x) {
    return x * x;
  }

  public static float distanceSquared(float dx, float dy) {
    return dx * dx + dy * dy;
  }

  public static float[] getBaseColors(float opacity) {
    if (opacity == 1) {
      return noColors;
    }
    return new float[]{
        0, 0, 0, opacity,
        0, 0, 0, opacity,
        0, 0, 0, opacity,
        0, 0, 0, opacity
    };
  }
}

