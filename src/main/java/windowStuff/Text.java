package windowStuff;

import general.Constants;
import general.Data;
import general.Util;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Text {

  private static final double textureHeight =
      64d / 4096d; //the height of a glyph sub-texture, in uv coordinates
  private final int layer;
  private final String fontName;
  private final int maxWidth;
  private final String text;
  private final String shader;
  private final BatchSystem bs;
  private final AbstractSprite background;
  public int x, y;
  private float fontSize;
  private float scale;
  private List<Symbol> symbols;
  private float[] colors = new float[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1};
  private boolean deleted = false;
  private boolean hidden = false;
  private int lineCount = 1;

  public Text(String value, String font, int width, int x, int y, int layer, float size,
      BatchSystem bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }

  public Text(String value, String font, int width, int x, int y, int layer, float size,
      BatchSystem bs, String shader, String backgroundImage) {
    fontSize = size;
    this.x = x;
    this.bs = bs;
    this.y = y;
    text = value;
    this.maxWidth = width;
    fontName = font;
    this.layer = layer+1;
    this.shader = shader;
    symbols = new LinkedList<>();
    scale = (float) (fontSize / textureHeight);

    if (backgroundImage == null) {
      background = new NoSprite();
    } else {
      background = new Sprite(backgroundImage, width, fontSize, layer, "Textbox").setColors(
          Util.getCycle2colors());
      background.addToBs(bs);
    }

    for (char c : value.toCharArray()) {
      Symbol symbol = new Symbol(c, x, y, shader);
      symbols.add(symbol);
      bs.addSprite(symbol.sprite);
    }
    arrange();
  }

  public void hide() {
    if (hidden) {
      return;
    }
    for (Symbol s : symbols) {
      s.sprite.unBatch();
    }
    background.unBatch();
    hidden = true;
  }

  public void show() {
    if (!hidden) {
      return;
    }
    background.addToBs(bs);
    for (Symbol s : symbols) {
      bs.addSprite(s.sprite);
    }
    hidden = false;
  }

  public float getFontSize() {
    return fontSize;
  }

  public void setFontSize(float size) {
    fontSize = size;
    scale = (float) (fontSize / textureHeight);
    for (Symbol symbol : symbols) {
      symbol.updateScale();
    }
  }

  public void setText(String value) {
    List<Symbol> newSymbols = new LinkedList<>();
    Iterator<Symbol> existing = symbols.listIterator();
    for (char c : value.toCharArray()) {
      if (existing.hasNext()) {
        Symbol symbol = existing.next();
        newSymbols.add(symbol);
        if (symbol.character != c) {
          symbol.setCharacter(c);
        }
      } else {
        Symbol symbol = new Symbol(c, x, y, shader);
        if (!hidden) {
          bs.addSprite(symbol.sprite);
        }
        newSymbols.add(symbol);
      }
    }
    while (existing.hasNext()) {
      Symbol symbol = existing.next();
      symbol.delete();
      existing.remove();
    }
    symbols.clear();
    symbols = newSymbols;
    arrange();
  }

  public void setColors(float[] colors) {
    for (var symbol : symbols) {
      symbol.sprite.setColors(colors);
    }
    this.colors = colors;
  }

  public void move(int newX, int newY) {
    newX = Math.min(newX, Constants.screenSize.x - maxWidth);
    newY = Math.min(newY + (int)(lineCount*fontSize), Constants.screenSize.y - (int)fontSize/2);
    int dx = newX - x, dy = newY - y;
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.getX() + dx, symbol.sprite.getY() + dy);
    }
    background.setPosition(newX + maxWidth / 2f, newY - lineCount * fontSize / 2);
    x = newX;
    y = newY;
  }

  private void arrange() {
    int line = 0;
    float xOffset = fontSize / 4;
    for (int i = 0, size = symbols.size(); i < size; i++) {
      Symbol symbol = symbols.get(i);
      if(symbol.character == '\n'){
        line++;
        xOffset = fontSize / 4;
      }
      symbol.move(x + xOffset + symbol.width * .5f, y - line * fontSize);
      xOffset += symbol.width;
      if (symbol.character == ' ') {
        float nextWordLen = 0;
        for (int j = i + 1; j < symbols.size() && symbols.get(j).character != ' '; j++) {
          nextWordLen += symbols.get(j).width;
        }
        if (xOffset > maxWidth - nextWordLen) {
          line++;
          xOffset = fontSize / 4;
        }
      }
    }

    lineCount = line;

    background.setPosition(x + maxWidth / 2f, y - line * fontSize / 2);
    background.setSize(maxWidth, (line + 1) * fontSize);
  }

  public void delete() {
    for (var symbol : symbols) {
      symbol.delete();
    }
    symbols.clear();
    deleted = true;
  }

  private class Symbol {

    final Sprite sprite;
    float width;
    char character;

    Symbol(char c, float x, float y, String shader) {
      String imageName = fontName + '-' + Character.getName(c);
      float[] uv = Data.getImageCoordinates(imageName);
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite = new Sprite(imageName, width, fontSize, layer, shader);
      sprite.setX(x + width / 2);
      sprite.setY(y);
      character = c;
      sprite.setColors(colors);
    }

    void updateScale() {
      String imageName= fontName + '-' + Character.getName(character);
      float[] uv = Data.getImageCoordinates(imageName);
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite.setSize(width, fontSize);
    }

    void move(float X, float Y) {
      sprite.setPosition(X, Y);
    }

    void delete() {
      sprite.delete();
    }

    public char getCharacter() {
      return character;
    }

    void setCharacter(char c) {
      String imageName =fontName + '-' + Character.getName(c);
      float[] uv = Data.getImageCoordinates(imageName);
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite.setImage(imageName);
      sprite.setSize(width, fontSize);
      character = c;
    }
  }
}
