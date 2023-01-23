package windowStuff;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

  static final Vector3f forward = new Vector3f(0f, 0f, -100f);
  static final Vector3f up = new Vector3f(0f, 1f, 0f);

  private final Matrix4f projection;
  private final Vector3f position;
  private final Matrix4f view;
  private float zoom = 1;

  public Camera(Vector3f position) {
    projection = new Matrix4f();
    view = new Matrix4f();
    this.position = position;
    adjustProjection();
    view.setLookAt(position, new Vector3f(0f, 0f, -100f).add(position),
        up); // position.add(forward) is different from video
  }

  public float getX() {
    return position.x;
  }

  public float getY() {
    return position.y;
  }

  public float getZoom() {
    return zoom;
  }

  public void setZoom(float zoom) {
    this.zoom = zoom;
    adjustProjection();
  }

  public void adjustProjection() {
    projection.setOrtho(0f, 1920 * zoom, 0f, 1080 * zoom, 0f, 100f);
  }

  public Matrix4f getViewMatrix() {
    return view;
  }

  public Matrix4f getProjectionMatrix() {
    return projection;
  }

  public void move(float x, float y, float z) {
    position.x += x;
    position.y += y;
    position.z += z;
    view.setLookAt(position, new Vector3f(0f, 0f, -100f).add(position),
        up); // position.add(forward) is different from video
  }

  public void moveTo(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
    view.setLookAt(position, new Vector3f(0f, 0f, -100f).add(position),
        up); // position.add(forward) is different from video
  }
}
