package namePong;

import java.util.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static namePong.Pong.*;
import multigame.*;

/**
 * Ball class...
 */
public class Ball {

  private Rectangle2D.Double body;
  private MultiGame mg;
  private KeyHandler kh;
  private boolean alive = true;
  private int ballNum;
  private int radius = 10;
  private int diameter = 2 * radius;
  private double xVelocity = 0.8;
  private double yVelocity = 0.6;
  private double maxVelocity = 1.4;

  /**
   * Constructor has a parameter so the Ball has a reference to the MultiGame
   * Object, and a ballNum parameter
   */
  public Ball(MultiGame mg) {
    this.mg = mg;
    kh = mg.getKeyHandler();
    int buffer = 4;
    body = new Rectangle2D.Double(center.x - radius, center.y - radius,
        diameter, diameter);

    xVelocity = Math.random() * 0.5 * maxVelocity + 0.5 * maxVelocity;
    yVelocity = Math.random() * 0.5 * maxVelocity + 0.5 * maxVelocity;
    if (Math.random() < 0.5) {
      xVelocity = -xVelocity;
    }
    if (Math.random() < 0.5) {
      yVelocity = -yVelocity;
    }
  }

  /**
   * Update the position and other fields if necessary
   */
  public void updatePos() {
    int buffer = 4;

    body.x += xVelocity;
    body.y -= yVelocity;

  }// end updatePos()

  /**
   * render the Ship to Graphics2D g
   */
  public void render(Graphics2D g) {
    g.setColor(Color.GREEN);
    g.fill(body);

  }// end render(Graphics2D g)

  /**
   * Returns a reference to the body Rectangle2D of the Ball
   * 
   * @return
   */
  public Rectangle2D.Double getBody() {
    return body;
  }

  /**
   * Changes the sign of the xVelocity
   */
  public void changeXDirection() {
    xVelocity = -xVelocity;
  }

  /**
   * Changes the sign of the xVelocity
   */
  public void changeYDirection() {
    yVelocity = -yVelocity;
  }

  /**
   * Returns the xVelocity
   */
  public double getXVelocity() {
    return xVelocity;
  }

  /**
   * Returns the yVelocity
   */
  public double getYVelocity() {
    return yVelocity;
  }

  /**
   * Assigns xVel to xVelocity
   */
  public void setXVelocity(double xVel) {
    xVelocity = xVel;
  }

  /**
   * Assigns yVel to yVelocity
   */
  public void setYVelocity(double yVel) {
    yVelocity = yVel;
  }

  /**
   * .....
   */
  public void changeXVelocity(double ratio) {
    xVelocity = ratio * maxVelocity;
  }

  /**
   * .....
   */
  public void changeYVelocity(double ratio) {
    yVelocity = ratio * maxVelocity;
  }

  public double getMaxVelocity() {
    return maxVelocity;
  }

  public Point2D.Double getCenter() {
    return new Point2D.Double(body.x + body.width, body.y + body.height);
  }
}// end class Ball