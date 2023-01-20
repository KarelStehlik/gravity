package main;

import general.Data;
import imgui.ImGui;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import windowStuff.BatchSystem;
import windowStuff.Sprite;
import windowStuff.UserInputHandler;
import windowStuff.UserInputListener;
import windowStuff.Window;

public class Game implements Window.GameplayLoop, UserInputHandler {
  BatchSystem bs = new BatchSystem();
  private UserInputListener input;
  private final GravitySimulator simulator = new BruteForce();
  private final List<Planet> planets = new LinkedList<>();


  public Game(Window win){
    win.getGraphics().addBatchSystem(bs);
    input = win.getUserInputListener();
  }

  private void testMake(float x,float y, int mass){
    Sprite test = new Sprite("Bowman",100,100,100,100,5,"basic");
    test.addToBs(bs);
    Planet p = new Planet(test,x,y,mass, 100);
    p.applyForce(Data.gameMechanicsRng.nextFloat(-10,10), Data.gameMechanicsRng.nextFloat(-10,10));
    planets.add(p);
  }

  @Override
  public void onMouseMove(double newX, double newY) {

  }

  @Override
  public void onMouseButton(int button, int action, int mods) {
    if(action==0){
      if(button==0) {
        testMake(input.getX(), input.getY(), 10);
      }else if(button==1){
        testMake(input.getX(), input.getY(), 1000);
      }
    }
  }

  @Override
  public void onScroll(double xOffset, double yOffset) {

  }

  @Override
  public void onKeyPress(int key, int action, int mods) {

  }

  @Override
  public void graphicsUpdate(float dt) {
    ImGui.showDemoWindow();
    ImGui.text("test");
  }

  @Override
  public void tick() {
    simulator.simulate(planets);

    Iterator<Planet> iter = planets.iterator();
    while(iter.hasNext()){
      Planet p = iter.next();
      if(p.WasDeleted()){
        iter.remove();
      }else{
        p.onGameTick(0);
      }
    }
  }
}
