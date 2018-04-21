package screensaver;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import multigame.*;

public class ScreenSaver {

  private MultiGame mg;
  private Point pos;
  private int counter = 0;
  private Font font;

  public ScreenSaver(MultiGame mg) {
    this.mg = mg;
    pos = new Point(200, 200);
    counter = 0;
    font = new Font("Lucida Console", Font.BOLD, 60);
  }

  public void render(Graphics2D g) {
    if (counter % 50 == 0) {
      pos.x = (int) (Math.random() * 300);
      pos.y = (int) (Math.random() * 300) + 300;

    }
    counter++;

    Color c = new Color((int) (Math.random() * 128)+128,
        (int) (Math.random() * 128)+128, (int) (Math.random() * 128)+128);
    g.setColor(c);
    g.setFont(font);
    g.drawString("Lake Forest College: Relay For Life", pos.x, pos.y);
    g.drawString("25 CENTS / GAME", pos.x, pos.y+60);
    
    g.drawString("PUSH BUTTON", pos.x, pos.y + 120);
    g.drawString("FOR  MENU", pos.x, pos.y + 180);

  }
}