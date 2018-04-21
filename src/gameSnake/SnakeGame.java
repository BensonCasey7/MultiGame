package gameSnake;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

public class SnakeGame extends GameEngine {

	static int size = 20;

	// player1 vars
	Player player1;
	double p1dir = 7 * Math.PI / 4;
	static int p1Speed = 5;
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

	public static void main(String[] args) {
		SnakeGame g = new SnakeGame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		windowWidth = (int) screenSize.getWidth();
		windowHeight = (int) screenSize.getHeight();
		g.setExtendedState(MAXIMIZED_BOTH);
		g.setUndecorated(true);
		g.setVisible(true);
		g.init();
		g.run();
		System.exit(0);
	}

	void update() {
		if (input.isKeyDown(KeyEvent.VK_ESCAPE)) {
			isRunning = false;
		}
		if (!playing) {
			if (input.isKeyDown(KeyEvent.VK_ENTER)) {
				init();
			}
			return;
		}

		// player1 update
		if (input.isKeyDown(KeyEvent.VK_D)) {
			p1dir -= Math.PI / 20;
		}
		if (input.isKeyDown(KeyEvent.VK_A)) {
			p1dir += Math.PI / 20;
		}
		if (input.isKeyDown(KeyEvent.VK_W)) {
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
	}

	void init() {
		// Creats new 16 by 16 blank image
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		getContentPane().setCursor(blankCursor);

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
	}

	void draw(Graphics g) {

		g = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, windowWidth, windowHeight);

		// player1 draw
		g.setColor(Color.WHITE);
		g.drawString("Score1: " + p1Score, windowWidth - 100, size);
		player1.draw(g, tick);

		// player2 draw
		// g.setColor(Color.WHITE);
		// g.drawString("Score2: " + p2Score, windowWidth - 200, size);
		// player2.draw(g, Color.BLUE);

		apple.draw(g);

		if (!playing) {
			g.setFont(new Font("TimesRoman", Font.PLAIN, 72)); 
			g.drawString("Press Enter to Play Again", 550, 500);
		}

	}

}