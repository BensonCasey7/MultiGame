package namePong;

import java.util.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import static namePong.Pong.*;

import multigame.*;

/**
 * Court class...
 */
public class Court {

  ArrayList<Rectangle> xWalls;
  ArrayList<Rectangle> yWalls;
  ArrayList<Rectangle> goals;
  int                  wallThickness = 20;
  int                  goalLength    = 360;
  int                  wallLength    = (2 * apothem - goalLength) / 2;
  Color                color         = Color.WHITE;

  /**
   * Constructor has a parameter so the Paddle has a reference to the MultiGame
   * Object, and a playerNum parameter
   */
  public Court(MultiGame mg) {
    xWalls = new ArrayList<Rectangle>();
    yWalls = new ArrayList<Rectangle>();
    goals = new ArrayList<Rectangle>();
    // upper left .. player 3
    yWalls.add(new Rectangle(center.x - apothem, center.y - apothem - wallThickness,
        wallLength, wallThickness));
    // lower left .. player 4
    yWalls.add(new Rectangle(center.x - apothem, center.y + apothem, wallLength,
        wallThickness));
    // upper right ..
    yWalls.add(new Rectangle(center.x - apothem + wallLength + goalLength, center.y
        - apothem - wallThickness, wallLength, wallThickness));
    // lower right
    yWalls.add(new Rectangle(center.x - apothem + wallLength + goalLength, center.y
        + apothem, wallLength, wallThickness));

    // upper left .. player 1
    xWalls.add(new Rectangle(center.x - apothem - wallThickness, center.y - apothem,
        wallThickness, wallLength));
    // upper right .. player 2
    xWalls.add(new Rectangle(center.x + apothem, center.y - apothem, wallThickness,
        wallLength));
    // lower left ..
    xWalls.add(new Rectangle(center.x - apothem - wallThickness, center.y - apothem
        + wallLength + goalLength, wallThickness, wallLength));
    // lower right
    xWalls.add(new Rectangle(center.x + apothem, center.y - apothem + wallLength
        + goalLength, wallThickness, wallLength));

    int buffer = 8;
    // left goal .. player 1
    goals.add(new Rectangle(center.x - apothem - wallThickness - buffer, center.y
        - goalLength / 2, wallThickness, goalLength));

    // right goal .. player 2
    goals.add(new Rectangle(center.x + apothem + buffer, center.y - goalLength / 2,
        wallThickness, goalLength));

    // top goal .. player 3
    goals.add(new Rectangle(center.x - goalLength / 2, center.y - apothem - wallThickness
        - buffer, goalLength, wallThickness));

    // bottom goal .. player 4
    goals.add(new Rectangle(center.x - goalLength / 2, center.y + apothem + buffer,
        goalLength, wallThickness));

  }

  /**
   * Update the position and other fields if necessary
   */
  public void updatePos() {

  }// end updatePos()

  /**
   * changes the Ball direction if contact is made.. this can be made fancier
   */
  public boolean hitBall(Ball b) {
    for (Rectangle r : xWalls) {
      if (b.getBody().intersects(r)) {
        b.changeXDirection();
        return true;
      }
    }
    for (Rectangle r : yWalls) {
      if (b.getBody().intersects(r)) {
        b.changeYDirection();
        return true;
      }
    }
    return false;
  }

  /**
   * if a goal is hit by Ball b the player number of the goal is returned
   * otherwise 0 is returned
   */
  public int hitGoal(Ball b) {
    int playerGoal = -1;
    for (int i = 0; i < goals.size(); i++) {
      if (b.getBody().intersects(goals.get(i))) {
        playerGoal = i;
        if (Pong.scores[playerGoal] == 1) {
          if (playerGoal == 0)
            xWalls.get(0).height += goalLength;
          if (playerGoal == 1)
            xWalls.get(1).height += goalLength;
          if (playerGoal == 2)
            yWalls.get(0).width += goalLength;
          if (playerGoal == 3)
            yWalls.get(1).width += goalLength;
        }
        break;
      }
    }
    return playerGoal + 1;
  }

  /**
   * render the Court to Graphics2D g
   */
  public void render(Graphics2D g) {
    g.setColor(Color.WHITE);
    for (Rectangle r : xWalls) {
      g.fill(r);
    }
    // g.setColor(Color.YELLOW);
    for (Rectangle r : yWalls) {
      g.fill(r);
    }
    g.setColor(Color.BLACK);
    for (Rectangle r : goals) {
      g.fill(r);
    }
    // 
    // } else {
    // g.draw(body);
    // }
  }// end render(Graphics2D g)

}// end class Court