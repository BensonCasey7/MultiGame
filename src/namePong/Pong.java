package namePong;

import java.awt.*;
import java.util.ArrayList;
import static java.lang.Math.*;

import multigame.*;

/**
 * Pong game
 */
public class Pong implements Game {

  private KeyHandler kh;
  private boolean gameOver;
  private GameState gameState;
  private ArrayList<Paddle> paddles;
  private Ball ball;
  private int lives = 2;
  private int winner;
  private static int winScore = 2;
  public static int scores[] = { winScore, winScore, winScore, winScore };
  Court court;
  public static final Point center = new Point(640, 512);
  public static final int sideLength = 960;
  public static final int apothem = sideLength / 2;

  private Font scoreFont;
  private MultiGame mg;

  private Sound sound;

  /**
   * A Game class must have only one constructor and it must have exactly one
   * MultiGame parameter.<br>
   * Initialize all instance variables.
   */
  public Pong(MultiGame mg) {

    kh = mg.getKeyHandler();

    this.mg = mg;
    gameState = new GameState(mg);
    gameOver = false;
    court = new Court(mg);
    paddles = new ArrayList<Paddle>();
    paddles.add(new Paddle(mg, 1));
    paddles.add(new Paddle(mg, 2));
    paddles.add(new Paddle(mg, 3));
    paddles.add(new Paddle(mg, 4));

    sound = new Sound();
    String soundFiles[] = { "ball_lost.wav", "paddle_hit.wav", "wall_hit.wav",
        "game_over.wav" };
    sound.loadSoundFiles(soundFiles);
    scoreFont = new Font("Lucida Console", Font.BOLD, 30);
    initRound();
  }

  /**
   * Called at the beginning of each round to init appropriate variables.
   */
  public void initRound() {
    // invoked at the beginning of every round

    ball = new Ball(mg);

    // sound.play(4, true);

  }

  /**
   * Required method for implementing Game interface .. gameUpdate must
   * eventually set gameOver to false
   */
  public void gameUpdate() {
    gameState.tick();
    for (int i = 1; i <= 3; i++) {// update 3 times for every call to update
      if (gameState.inState(gameState.READY)) {
        if (gameState.getCurTick() == 1) {// just entered READY state
          if (lives < 0) {
            gameOver = true;
            scores[0] = winScore;
            scores[1] = winScore;
            scores[2] = winScore;
            scores[3] = winScore;
            return;
          }
          initRound();
        }
        for (Paddle p : paddles) {
          p.updatePos();
        }
      }

      else if (gameState.inState(gameState.PLAYING)) {
        ball.updatePos();
        for (Paddle p : paddles) {
          p.updatePos();
          if (p.hitBall(ball)) {
            sound.play(1, false);
          }
        }
        if (court.hitBall(ball)) {
          sound.play(2, false);
        }
        int pNum = court.hitGoal(ball);
        if (pNum > 0) {
          sound.play(0, false);
          scores[pNum - 1] = max(0, scores[pNum - 1] - 1);
          gameState.toState(gameState.DEAD);
          int numPlayersWith0 = 0;
          int winnerNumber = -1;
          for (int k = 0; k < 4; k++) {
            if (scores[k] == 0) {
              numPlayersWith0++;
            }
            if (scores[k] > 0) {
              winnerNumber = k;
            }
          }
          if (numPlayersWith0 >= 3) {
            lives = -1;
            winner = winnerNumber + 1;
          }
        }
      }

      else if (gameState.inState(gameState.DEAD)) {
        for (Paddle p : paddles) {
          p.updatePos();
        }
      }
    }// end for loop .. extra updates
  }

  /**
   * Required method for implementing Game interface.. renders all Game elements
   * to the Graphics2D variable
   */
  public void gameRender(Graphics2D g) {
    // clear the screen
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, mg.pWidth(), mg.pHeight());

    //g.scale(1024.0 / 1280.0, 768.0 / 1024.0);
    // render in all states
    court.render(g);
    for (Paddle p : paddles) {
      p.render(g);
    }
    ball.render(g);
    g.setColor(Color.WHITE);
    g.setFont(scoreFont);
    int x, y;

    // player1
    x = 50; // 0 + 50
    y = 50; // 0 + 50
    g.rotate(PI / 2, x, y);
    g.drawString("~" + scores[0] + "~", x, y);
    g.rotate(-PI / 2, x, y);

    // player2
    x = 1210; // 1280 - 70
    y = 954; // 1024-70
    g.rotate(3 * PI / 2, x, y);
    g.drawString("~" + scores[1] + "~", x, y);
    g.rotate(-3 * PI / 2, x, y);

    // player3
    x = 1230; // 1280-50
    y = 70; // 0 + 70
    g.rotate(PI, x, y);
    g.drawString("~" + scores[2] + "~", x, y);
    g.rotate(-PI, x, y);

    // player4
    x = 50; // 0 + 50
    y = 954; // 1024-70
    // g.rotate(PI / 2, x, y);
    g.drawString("~" + scores[3] + "~", x, y);
    // g.rotate(-PI / 2, x, y);

    if (gameState.inState(gameState.READY)) {
      g.setColor(Color.WHITE);
      g.drawString("READY!", 300, 300);
    }

    else if (gameState.inState(gameState.PLAYING)) {

    }

    else if (gameState.inState(gameState.DEAD)) {
      g.setColor(Color.WHITE);
      g.drawString("POINT!", 300, 300);

      if (lives < 0) {
        g.drawString("Winner: Player " + winner, 400, 400);
      }
    }
    // render ship in all gameStates
    // ship.render(g);

  }

  public Sound getSound() {
    return sound;
  }

  /**
   * Required method for implementing Game interface
   */
  public boolean isGameOver() {
    return gameOver;
  }

}