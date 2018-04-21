package gameSnake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static java.lang.Math.*;

import multigame.*;

/**
 * Pong game
 */

public class Snake implements Game {

	private KeyHandler kh;
	private boolean gameOver;
	private GameState gameState;
	private MultiGame mg;

	private int windowWidth;
	private int windowHeight;
	static int size = 20;

	// player1 vars
	Player player1;
	double p1dir = 7 * Math.PI / 4;
	static int p1Speed = 1;
	int p1IncSize = 1;
	int p1Score = 0;

	// player2 vars
	// double p2dir = 7 * Math.PI / 4;
	// static int p2Speed = 5;
	// int p2IncSize = 1;
	// int p2Score = 0;
	// Player player2;

	Apple apple;
	int appleSize = 5;

	int tick = 0;

	ArrayList<SnakeBody> player = new ArrayList<SnakeBody>();
	boolean playing = false;

	/**
	 * A Game class must have only one constructor and it must have exactly one
	 * MultiGame parameter.<br>
	 * Initialize all instance variables.
	 */
	public Snake(MultiGame mg) {

		kh = mg.getKeyHandler();
		this.mg = mg;
		gameState = new GameState(mg);
		gameOver = false;

		windowWidth = mg.pWidth();
		windowHeight = mg.pHeight();
		initRound();
	}

	/**
	 * Called at the beginning of each round to init appropriate variables.
	 */
	public void initRound() {
		// invoked at the beginning of every round

		// player1 init()
		player1 = new Player(300, 300, size, p1Speed, 5);
		p1Score = 0;
		p1IncSize = 1;
		p1dir = 0;

		// player2 init
		// player2 = new Player(600, 600, size, p2Speed, 5);
		// p2Score = 0;
		// p2IncSize = 1;
		// p2dir = 0;

		apple = new Apple(windowWidth, windowHeight, appleSize);
		playing = true;

		// sound.play(4, true);

	}

	/**
	 * Required method for implementing Game interface .. gameUpdate must eventually
	 * set gameOver to false
	 */
	public void gameUpdate() {
		gameState.tick();

		if (gameState.inState(gameState.READY)) {

			initRound();
		}

		else if (gameState.inState(gameState.PLAYING)) {

			if (kh.isRightPressed(1)) {
				p1dir -= Math.PI / 20;
			}
			if (kh.isLeftPressed(1)) {
				p1dir += Math.PI / 20;
			}
			if (kh.isBtn1Pressed(1)) {
				p1Speed = 14;
			} else {
				p1Speed = 7;
			}

			if (player1.collidesWithApple(apple)) {
				p1Score++;
				apple = new Apple(windowWidth, windowHeight, appleSize);

				player1.growBy(p1IncSize);
				p1IncSize++;
			}

			if (player1.getMaxHeadX() > windowWidth) {
				playing = false;
			}
			if (player1.getMaxHeadY() > windowHeight) {
				playing = false;
			}
			if (player1.getMinHeadX() < 0) {
				playing = false;
			}
			if (player1.getMinHeadY() < 0) {
				playing = false;
			}
			if (player1.collidesWithSelf()) {
				playing = false;
			}

			if (playing == false) {
				gameState.toState(gameState.DEAD);
			}
			player1.move(p1dir);
			player1.setSpeed(p1Speed);

			// player 2 update
			// if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
			// p2dir -= Math.PI / 20;
			// }
			// if (input.isKeyDown(KeyEvent.VK_LEFT)) {
			// p2dir += Math.PI / 20;
			// }
			// if (input.isKeyDown(KeyEvent.VK_UP)) {
			// p2Speed = 14;
			// } else {
			// p2Speed = 7;
			// }
			//
			//
			// if (player2.collidesWithApple(apple)) {
			// p2Score++;
			// apple = new Apple(windowWidth, windowHeight, appleSize);
			//
			// player2.growBy(p2IncSize);
			// p2IncSize++;
			// }
			//
			// if (player2.getMaxHeadX() > windowWidth) {
			// playing = false;
			// }
			// if (player2.getMaxHeadY() > windowHeight) {
			// playing = false;
			// }
			// if (player2.getMinHeadX() < 0) {
			// playing = false;
			// }
			// if (player2.getMinHeadY() < 0) {
			// playing = false;
			// }
			// if (player2.collidesWithSelf()) {
			// playing = false;
			// }
			// if (player1.collidesWithPlayer(player2)) {
			// playing = false;
			// }
			//
			//
			// player2.move(p2dir);
			// player2.setSpeed(p2Speed);
			tick++;

		} else if (gameState.inState(gameState.DEAD)) {
			gameOver = true;
			return;

		}
		// end for loop .. extra updates
	}

	/**
	 * Required method for implementing Game interface.. renders all Game elements
	 * to the Graphics2D variable
	 */
	public void gameRender(Graphics2D g) {
		// clear the screen
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, mg.pWidth(), mg.pHeight());

		if (gameState.inState(gameState.READY)) {

			g.setColor(Color.WHITE);
			g.drawString("READY!", 300, 300);

		}

		else if (gameState.inState(gameState.PLAYING)) {

			g.setColor(Color.WHITE);
			g.drawString("Score1: " + p1Score, windowWidth - 100, size);
			player1.draw(g, tick);

			// player2 draw
			// g.setColor(Color.WHITE);
			// g.drawString("Score2: " + p2Score, windowWidth - 200, size);
			// player2.draw(g, Color.BLUE);
			
			apple.draw(g);

		}

		else if (gameState.inState(gameState.DEAD)) {

			g.setColor(Color.WHITE);
			g.drawString("GameOver!", 300, 300);

		}
		// render ship in all gameStates
		// ship.render(g);

	}

	/**
	 * Required method for implementing Game interface
	 */
	public boolean isGameOver() {
		return gameOver;
	}

}