package org.example;



import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public final class Main {
  private static final Font font = new Font("Calibri", Font.PLAIN, 64);
  private static FontMetrics fm;
  private static int height;
  private static int ascent;

  public static void main(String[] arg) throws IOException {
    String key = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789/*-+{}[]()<>?!|.,:'\"_%$#@&=~ \n";

    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics g2d = img.createGraphics();

    g2d.setFont(font);

    fm = g2d.getFontMetrics();
    g2d.dispose();

    height = fm.getHeight();
    ascent = fm.getAscent();

    for(char c : key.toCharArray()){
      saveChar(c);
    }
  }

  public static void saveChar(char c) throws IOException {
    String key = String.valueOf(c);
    int width = fm.stringWidth(key);
    BufferedImage bufferedImage = new BufferedImage(Math.max(width,1), height,
        BufferedImage.TYPE_4BYTE_ABGR);
    ByteBuffer b = ByteBuffer.wrap(((DataBufferByte)bufferedImage.getAlphaRaster().getDataBuffer()).getData());

    Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(new Color(255, 255, 255, 0));
    graphics.fillRect(0, 0, 200, 50);
    graphics.setColor(Color.BLACK);
    graphics.setFont(font);
    graphics.drawString(key, 0, ascent);

    Files.createDirectories(Paths.get("fonts/"+font.getFontName()+"/"));
    String name = Character.getName(key.toCharArray()[0]);
    ImageIO.write(bufferedImage, "png", new File(
        "fonts/"+font.getFontName()+"/"+name+".png"));
    System.out.println(name + " Created");
  }
}