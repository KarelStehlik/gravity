package main;

import windowStuff.Window;

public final class Main {

  public static void main(String[] args) {
    System.out.println("Hello world!");
    Window win = new Window();
    Game g = new Game(win);
    win.setGame(g);
    win.setInputHandler(g);
    win.run();
  }
}