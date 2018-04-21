package multigame;

import java.awt.*;

/**
 * <pre>
 * All Games written for MultiGameUprite2013 must implement the Game interface
 * Important!!!
 * These classes must have exaclty one constructor ... which has 
 * exactly one MultiGame parameter ... e.g. 
 * public class Gamename(MultiGame mg) {
 * 
 * 
 * </pre>
 */
public interface Game {
  /**
   * gameUpdate() is called fps (default at 150) times per second<br>
   * All updates to all instance variables should be programmed in this method.
   */
  public void gameUpdate();

  /**
   * gameRender() is called as often as possible depending on the hardware and
   * how busy the operating system is with other processes<br>
   * All game elements that need to be rendered should be drawn to Graphics
   * variable g
   */
  public void gameRender(Graphics2D g);

  /**
   * isGameOver() returns true if the Game has terminated so MultiGame knows to
   * go back to the Menu state<br>
   * It is recommended that isGameOver return some boolean flag variable
   * gameOver that is set to true somewhere in gameUpdate()
   */
  public boolean isGameOver();
  
  //not sure if this is needed or not
  //public Graphics2D getGraphics2D();
}