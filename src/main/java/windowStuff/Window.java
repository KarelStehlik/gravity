package windowStuff;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.system.MemoryUtil.NULL;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class Window {

  private final long window;
  private final UserInputListener userInputListener = new UserInputListener(null);
  private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
  private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
  private final Graphics graphics;
  private volatile boolean running = false;
  private GameplayLoop game = new NoGame();

  public Window() {
    System.out.println("Running LWJGL ver.. " + Version.getVersion());

    GLFWErrorCallback.createPrint(System.err).set();
    if (!glfwInit()) {
      throw new IllegalStateException("LWJGL not initialized");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    String title = "hm";
    int height = 1080;
    int width = 1920;
    window = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);

    if (window == NULL) {
      throw new IllegalStateException("No win?");
    }

    glfwMakeContextCurrent(window);

    graphics = new Graphics();

    glfwSetCursorPosCallback(window, userInputListener::mousePosCallback);
    glfwSetScrollCallback(window, userInputListener::scrollCallback);
    glfwSetMouseButtonCallback(window, userInputListener::mouseButtonCallback);
    glfwSetKeyCallback(window, userInputListener::keyCallback);

    glfwSwapInterval(1);

    GL.createCapabilities();

    graphics.init();

    ImGui.createContext();
    imGuiGlfw.init(window, true);
    imGuiGl3.init("#version 330 core");
  }

  public void setGame(GameplayLoop game) {
    this.game = game;
  }

  public UserInputListener getUserInputListener() {
    return userInputListener;
  }

  public void setInputHandler(UserInputHandler h) {
    userInputListener.setInputHandler(h);
  }

  public Graphics getGraphics() {
    return graphics;
  }

  public void run() {
    running = true;
    glfwShowWindow(window);

    Thread gameThread = new Thread(this::gameLoop);

    gameThread.start();

    float dt = 0;
    float frameStartTime = System.nanoTime();
    while (!glfwWindowShouldClose(window)) {
      loop(dt);
      float frameEndTime = System.nanoTime();
      dt = frameEndTime - frameStartTime;
      frameStartTime = frameEndTime;
    }

    running = false;

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);
    glfwTerminate();
  }

  // dt is nanoseconds
  private void loop(float dt) {
    glfwPollEvents();

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    imGuiGlfw.newFrame();
    ImGui.newFrame();

    game.graphicsUpdate(dt / 1000000000);

    graphics.redraw(dt);
    ImGui.render();
    imGuiGl3.renderDrawData(ImGui.getDrawData());
    userInputListener.endFrame();

    glfwSwapBuffers(window);
  }

  private void gameLoop() {
    while (running) {
      try {
        Thread.sleep(0, 1); // prevents synchronized game.tick from hogging the lock
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      userInputListener.handleEvents();
      game.tick();
    }
  }

  @Override
  public String toString() {
    return "Window{"
        + "glfwWindow=" + window
        + '}';
  }

  public interface GameplayLoop {

    void graphicsUpdate(float dt);

    void tick();
  }

  private static class NoGame implements GameplayLoop {

    @Override
    public void graphicsUpdate(float dt) {
    }

    @Override
    public void tick() {
    }
  }
}
