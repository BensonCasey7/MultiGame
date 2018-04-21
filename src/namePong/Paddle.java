package namePong;

import java.util.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static namePong.Pong.*;
import multigame.*;

/**
 * Paddle class...
 */
public class Paddle {

  private Rectangle2D.Double body;
  private MultiGame mg;
  private KeyHandler kh;
  private boolean alive = true;
  private int playerNum;
  private int paddleLength = 50, paddleThickness = 20;
  private double velocity = 0.8;

  /**
   * Constructor has a parameter so the Paddle has a reference to the MultiGame
   * Object, and a playerNum parameter
   */
  public Paddle(MultiGame mg, int playerNum) {
    this.playerNum = playerNum;
    int buffer = 4;
    if (playerNum == 1) {
      body = new Rectangle2D.Double(center.x - apothem + buffer, center.y
          - paddleLength / 2, paddleThickness, paddleLength);
    } else if (playerNum == 2) {
      body = new Rectangle2D.Double(center.x + apothem - paddleThickness
          - buffer, center.y - paddleLength / 2, paddleThickness, paddleLength);
    } else if (playerNum == 3) {
      body = new Rectangle2D.Double(center.x - paddleLength / 2, center.y
          - apothem + buffer, paddleLength, paddleThickness);
    } else if (playerNum == 4) {
      body = new Rectangle2D.Double(center.x - paddleLength / 2, center.y
          + apothem - paddleThickness - buffer, paddleLength, paddleThickness);
    }

    alive = true;
    this.mg = mg;
    kh = mg.getKeyHandler();
  }

  /**
   * Update the position and other fields if necessary
   */
  public void updatePos() {
    int buffer = 4;
    if (playerNum == 2) {
      if (kh.isUpPressed(1)) {
        body.y -= velocity;
        if (body.y < center.y - apothem) {
          body.y = center.y - apothem;
        }
      } else if (kh.isDownPressed(1)) {
        body.y += velocity;
        if (body.y + paddleLength > center.y + apothem) {
          body.y = center.y + apothem - paddleLength;
        }
      }
    } else if (playerNum == 1) {
      if (kh.isDownPressed(2)) {
        body.y += velocity;
        if (body.y + paddleLength > center.y + apothem) {
          body.y = center.y + apothem - paddleLength;
        }
      } else if (kh.isUpPressed(2)) {
        body.y -= velocity;
        if (body.y < center.y - apothem) {
          body.y = center.y - apothem;
        }
      }
    } else if (playerNum == 3) {
      if (kh.isRightPressed(1)) {
        body.x += velocity;
        if (body.x + paddleLength > center.x + apothem) {
          body.x = center.x + apothem - paddleLength;
        }
      } else if (kh.isLeftPressed(1)) {
        body.x -= velocity;
        if (body.x < center.x - apothem) {
          body.x = center.x - apothem;
        }
      }
    } else if (playerNum == 4) {
      if (kh.isLeftPressed(2)) {
        body.x -= velocity;
        if (body.x < center.x - apothem) {
          body.x = center.x - apothem;
        }
      } else if (kh.isRightPressed(2)) {
        body.x += velocity;
        if (body.x + paddleLength > center.x + apothem) {
          body.x = center.x + apothem - paddleLength;
        }
      }
    }
  }// end updatePos()

  /**
   * changes the Ball direction if contact is made.. this can be made fancier by
   * modifying the trajectory of the Ball based on where it hits the Paddle
   */
  public boolean hitBall(Ball b) {

    if (b.getBody().intersects(body)) {
      Rectangle2D.Double ballBody = b.getBody();
      // push ball out in front of paddle
      Point2D.Double ballCenter = new Point2D.Double(ballBody.x
          + ballBody.width / 2, ballBody.y + ballBody.height / 2);

      Point2D.Double paddleCenter = new Point2D.Double(body.x + body.width / 2,
          body.y + body.height / 2);

      double bCtr, pCtr;
      double maxVel = b.getMaxVelocity();
      double ratio = 1.0;
      if (playerNum == 1) {
        ballBody.x = body.x + paddleThickness + 1;
        b.changeXDirection();
        bCtr = b.getCenter().y;
        pCtr = body.y + body.height / 2;
        ratio = (pCtr - bCtr) / (0.5 * body.height);
        b.changeYVelocity(ratio);

      } else if (playerNum == 2) {
        ballBody.x = body.x - ballBody.width - 1;
        b.changeXDirection();
        bCtr = b.getCenter().y;
        pCtr = body.y + body.height / 2;
        ratio = (pCtr - bCtr) / (0.5 * body.height);
        b.changeYVelocity(ratio);
      } else if (playerNum == 3) {
        ballBody.y = body.y + paddleThickness + 1;
        b.changeYDirection();
        bCtr = b.getCenter().x;
        pCtr = body.x + body.width / 2;
        ratio = -(pCtr - bCtr) / (0.5 * body.width);
        b.changeXVelocity(ratio);

      } else if (playerNum == 4) {
        ballBody.y = body.y - ballBody.height - 1;
        b.changeYDirection();
        bCtr = b.getCenter().x;
        pCtr = body.x + body.width / 2;
        ratio = -(pCtr - bCtr) / (0.5 * body.width);
        b.changeXVelocity(ratio);
      }

      return true;

    }
    return false;
  }

  /**
   * render the Ship to Graphics2D g
   */
  public void render(Graphics2D g) {
    g.setColor(Color.CYAN);
    g.fill(body);

  }// end render(Graphics2D g)

}// end class Paddle