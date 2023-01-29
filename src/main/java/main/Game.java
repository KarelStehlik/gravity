package main;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import general.Data;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import java.util.LinkedList;
import java.util.List;
import windowStuff.BatchSystem;
import windowStuff.Camera;
import windowStuff.Sprite;
import windowStuff.UserInputHandler;
import windowStuff.UserInputListener;
import windowStuff.Window;

public class Game implements Window.GameplayLoop, UserInputHandler {

  private final BatchSystem bs = new BatchSystem();
  private final Camera camera;
  private final UserInputListener input;
  private final Tree simulator = new Tree(bs);
  private final List<Planet> planets = new LinkedList<>();
  private float timeStep = 0.1f;
  private float targetSpeed = 0.5f;
  private float timeElapsed = 0;
  private boolean placeStaticObjects = false;
  private float mass = 100, size = 50, vx = 0, vy = 0;
  private boolean paused = false;
  private int newCount = 1;
  private float newSpread = 100;
  private float G = 0.5f;
  private int worldSize = 2<<14;
  private int collisions = 1;
  private float deletionRange = 400;
  private final Sprite deletionCircle;

  public Game(Window win) {
    win.getGraphics().addBatchSystem(bs);
    camera = bs.getCamera();
    input = win.getUserInputListener();
    deletionCircle = new Sprite("RedCircle", 0, 0, 0, 0, 2, "basic").addToBs(bs);
  }

  private void testMake(float x, float y) {
    Sprite test = new Sprite("Planet", 100, 100, 100, 100, 5, "basic");
    test.addToBs(bs);
    Planet p = new Planet(test, x, y, mass, size);
    p.vx = vx;
    p.vy = vy;
    p.setLocked(placeStaticObjects);
    planets.add(p);
  }

  private float toWorldX(float screenX) {
    return screenX * camera.getZoom() + camera.getX();
  }

  private float toWorldY(float screenX) {
    return screenX * camera.getZoom() + camera.getY();
  }

  @Override
  public void onMouseMove(double newX, double newY) {
    if (input.isMousePressed(1)) {
      deletionCircle.setPosition(toWorldX((float) newX), toWorldY((float) newY));
      for (Planet p : planets) {
        if (Math.hypot(p.x - toWorldX(input.getX()), p.y - toWorldY(input.getY()))
            < deletionRange) {
          p.delete();
        }
      }
    }
  }

  @Override
  public void onMouseButton(int button, int action, int mods) {
    if (action == 0) {
      if (button == 0) {
        testMake(toWorldX(input.getX()), toWorldY(input.getY()));
        for (int i = 0; i < newCount - 1; i++) {
          float dx, dy;
          do {
            dx = Data.gameMechanicsRng.nextFloat(-newSpread, newSpread);
            dy = Data.gameMechanicsRng.nextFloat(-newSpread, newSpread);
          }
          while (dx * dx + dy * dy > newSpread * newSpread);
          testMake(toWorldX(input.getX()) + dx, toWorldY(input.getY()) + dy);
        }
      } else if (button == 1) {
        deletionCircle.setSize(0, 0);
      }
    } else if (button == 1) {
      for (Planet p : planets) {
        if (Math.hypot(p.x - toWorldX(input.getX()), p.y - toWorldY(input.getY()))
            < deletionRange) {
          p.delete();
        }
      }
      deletionCircle.setSize(deletionRange * 2, deletionRange * 2);
      deletionCircle.setPosition(toWorldX(input.getX()), toWorldY(input.getY()));
    }
  }

  @Override
  public void onScroll(double xOffset, double yOffset) {
    camera.setZoom(camera.getZoom() * (float) Math.pow(1.1, -yOffset));
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    if (key == GLFW_KEY_SPACE && action == 0) {
      paused = !paused;
    }
  }

  @Override
  public void graphicsUpdate(float dt) {
    //ImGui.showDemoWindow();
    ImGui.begin("ui");
    if (ImGui.collapsingHeader("simulation properties")) {
      float[] value = new float[]{timeStep};
      if (ImGui.sliderFloat("time step", value, 0, 1)) {
        timeStep = value[0];
      }
      float[] value2 = new float[]{targetSpeed};
      if (ImGui.sliderFloat("target simulation speed", value2, 0, 1)) {
        targetSpeed = value2[0];
      }
      float[] grav = new float[]{G};
      if (ImGui.sliderFloat("gravity", grav, 0, 1)) {
        G = grav[0];
      }
      int[] ws = new int[]{worldSize};
      if (ImGui.dragInt("world size", ws, 5f, 64f, 1048576f)) {
        worldSize = ws[0];
      }
      ImBoolean pause = new ImBoolean(paused);
      if (ImGui.checkbox("pause (spacebar)", pause)) {
        paused = pause.get();
      }
      ImBoolean vis = new ImBoolean(simulator.isVisible());
      if (ImGui.checkbox("show tree", vis)) {
        simulator.setVisible(vis.get());
      }
      String[] options = new String[]{"pass through", "merge"};
      ImInt i = new ImInt(collisions);
      if (ImGui.combo("on planet collision", i, options)) {
        collisions = i.get();
      }
    }
    if (ImGui.collapsingHeader("new objects (left click)")) {
      ImBoolean locked = new ImBoolean(placeStaticObjects);
      if (ImGui.checkbox("locked position", locked)) {
        placeStaticObjects = locked.get();
      }
      // private float mass = 100, size = 50, vx=0, vy=0;
      float[] ms = new float[]{mass};
      if (ImGui.dragFloat("mass", ms, 10, 1, 1000000)) {
        mass = ms[0];
      }
      float[] siz = new float[]{size};
      if (ImGui.dragFloat("size", siz, 5f, 20f, 500f)) {
        size = siz[0];
      }
      float[] vxy = new float[]{vx, vy};
      if (ImGui.dragFloat2("speed", vxy, 0.05f, -10, 10)) {
        vx = vxy[0];
        vy = vxy[1];
      }
      int[] count = new int[]{newCount};
      if (ImGui.dragInt("count", count, 1, 1, 100)) {
        newCount = count[0];
      }
      float[] spread = new float[]{newSpread};
      if (ImGui.dragFloat("spread", spread, 5, 5, 1000)) {
        newSpread = spread[0];
      }
    }
    float[] del = new float[]{deletionRange};
    if (ImGui.dragFloat("delete radius (right click)", del, 5, 5, 1000)) {
      deletionRange = del[0];
    }
    ImGui.end();

    camera.move(((input.isKeyPressed(GLFW_KEY_RIGHT) || input.isKeyPressed(GLFW_KEY_D)) ? 5
            * camera.getZoom() : 0) -
            ((input.isKeyPressed(GLFW_KEY_LEFT) || input.isKeyPressed(GLFW_KEY_A)) ? 5
                * camera.getZoom() : 0),
        ((input.isKeyPressed(GLFW_KEY_UP) || input.isKeyPressed(GLFW_KEY_W)) ? 5 * camera.getZoom()
            : 0) -
            ((input.isKeyPressed(GLFW_KEY_DOWN) || input.isKeyPressed(GLFW_KEY_S)) ? 5
                * camera.getZoom() : 0),
        0);
  }

  @Override
  public void tick() {
    if (paused) {
      return;
    }
    timeElapsed += targetSpeed;
    while (timeElapsed > timeStep) {
      timeElapsed -= timeStep;
      simulator.simulate(planets, timeStep, G, worldSize, collisions);
    }
  }
}
