package gameAsteroidDrop2Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

import multigame.*;

/**
 * AsteroidDrop game
 */

public class AsteroidDrop2Player implements Game
{
	
	private KeyHandler kh;
	private boolean gameOver;
	private GameState gameState;
	private MultiGame mg;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int windowWidth = (int) screenSize.getWidth();
	int windowHeight = (int) screenSize.getHeight();
	
	//private static int windowWidth = mg.pWidth();
	//private static int windowHeight = mg.pHeight();
	static int size = 20;

	static final int UP = 0;
	static final int RIGHT = 1;
	static final int DOWN = 2;
	static final int LEFT = 3;
	int player1x = windowWidth/3;
	int player1y = windowHeight-30;
	int player2x = 2*windowWidth/3;
	int player2y = windowHeight-30;
	int playerSpeed = (int) (windowWidth*.05);
	int score = 0;
	int lives = 3;
	double fireRate1 = 0, fireRate2 = fireRate1, maxAmmo = 20, ammo = maxAmmo, reloadSpeed = 45, reloadTimer = reloadSpeed;
	int boxWidth = 150, boxHeight = 25;
	boolean canShoot = true;
	int astFreq = 60, astCountdown = 15;
	double astSpeed = 5;
	double pUpFreq = 600, pUpCountdown = 150, pUpChance = 300, pUpTimer = 0, pUpMaxTime = 300;
	int exLifeFreq = 120, exLifeCountdown = 150, exLifeChance = 300;
	double difAstFreq = .9, difAstSpeed = 1.1, difPowUpFreq = .7;
	int quitTimer = 0;
	
	Random random = new Random();
	
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Bullet> pUpBulletsLeft = new ArrayList<Bullet>();
	ArrayList<Bullet> pUpBulletsRight = new ArrayList<Bullet>();
	ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();
	ArrayList<ExtraLife> extraLives = new ArrayList<ExtraLife>();

	int tick = 0;
	
	public AsteroidDrop2Player(MultiGame mg) {

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
	}

	/**
	 * Required method for implementing Game interface .. gameUpdate must eventually
	 * set gameOver to false
	 */
	public void gameUpdate() {
		gameState.tick();

		if (gameState.inState(gameState.READY)) {
			if (kh.isRightPressed(2)) {
				player1x += playerSpeed;
			}
			if (kh.isLeftPressed(2)) {
				player1x -= playerSpeed;
			}
			
			if (kh.isRightPressed(1)) {
				player2x += playerSpeed;
			}
			if (kh.isLeftPressed(1)) {
				player2x -= playerSpeed;
			}

			initRound();
		}

		else if (gameState.inState(gameState.PLAYING)) {

			if (kh.isRightPressed(1)) {
				player2x += playerSpeed;
			}
			if (kh.isLeftPressed(1)) {
				player2x -= playerSpeed;
			}
			if (kh.isBtn1Pressed(1)) {
				if (canShoot && fireRate2 == 0)
				{
					bullets.add(new Bullet(player2x-1,player2y-5));
					fireRate2 = 5;
					if (pUpTimer != 0)
					{
						pUpBulletsLeft.add(new Bullet(player2x-3,player2y-5));
						pUpBulletsRight.add(new Bullet(player2x+2,player2y-5));
						fireRate2 = 2;
						ammo++;
					}
					ammo--;
				}
			}
			
			if (kh.isRightPressed(2)) {
				player1x += playerSpeed;
			}
			if (kh.isLeftPressed(2)) {
				player1x -= playerSpeed;
			}
			if (kh.isBtn1Pressed(2)) {
				if (canShoot && fireRate1 == 0)
				{
					bullets.add(new Bullet(player1x-1,player1y-5));
					fireRate1 = 5;
					if (pUpTimer != 0)
					{
						pUpBulletsLeft.add(new Bullet(player1x-3,player1y-5));
						pUpBulletsRight.add(new Bullet(player1x+2,player1y-5));
						fireRate1 = 2;
						ammo++;
					}
					ammo--;
				}
			}

			if (fireRate1 > 0)
				fireRate1--;
			
			if (fireRate2 > 0)
				fireRate2--;
		
//			if (ammo == 0)
//			{
//				canShoot = false;
//				reloadTimer--;
//				if (reloadTimer == 0)
//				{
//					ammo = maxAmmo;
//					reloadTimer = reloadSpeed;
//					canShoot = true;
//				}
//			}
			
			if (player1x<7) //Prevent ship from leaving the screen
				player1x = 7;
			if (player1x>windowWidth-8)
				player1x = windowWidth-8;
			
			if (player2x<7) //Prevent player 2 from leaving the screen
				player2x = 7;
			if (player2x>windowWidth-8)
				player2x = windowWidth-8;
			
			for (Bullet b : bullets) //Moves bullets
			{
				b.setY(b.getY()-b.getSpeed());
			}
			
			for (Bullet bL : pUpBulletsLeft)
			{
				bL.setY(bL.getY()-bL.getSpeed());
				bL.setX(bL.getX()-bL.getStray());
			}
			
			for (Bullet bR : pUpBulletsRight)
			{
				bR.setY(bR.getY()-bR.getSpeed());
				bR.setX(bR.getX()+bR.getStray());
			}
			
			for (Asteroid a : asteroids) //Moves asteroids
			{
				a.setY(a.getY()+(int)a.getSpeed());
			}
			
			if (pUpCountdown == 0) //Creates new power up
			{
				if (random.nextInt((int) pUpChance) == 1)
				{
					powerUps.add(new PowerUp(random.nextInt((int) (windowWidth*.97)), random.nextInt(windowHeight-300)));
					pUpCountdown = pUpFreq;
				}
			}
			else
				pUpCountdown--;
			
			if (pUpTimer != 0)
				pUpTimer--;
			
			if (random.nextInt((int) exLifeChance) == 1 && exLifeCountdown < 0) //Creates new extra life
			{
				extraLives.add(new ExtraLife(random.nextInt((int) (windowWidth*.95)), random.nextInt(windowHeight-300)));
				exLifeCountdown = exLifeFreq;
			}
			exLifeCountdown--;
			
			if (astCountdown-- == 0) //Creates new asteroid
			{
				asteroids.add(new Asteroid(random.nextInt(windowWidth-(int)(windowWidth*.06+windowWidth*.01)), -(int)(windowWidth*.06+windowWidth*.01), astSpeed));
				astCountdown = astFreq;
			}
			
			for (Bullet b : new ArrayList<Bullet>(bullets)) //Collision for bullets and asteroids
			{
				for (Asteroid a : new ArrayList<Asteroid>(asteroids))
				{
					if (a.getBody().intersects(b.getBody()))
					{
						bullets.remove(b);
						asteroids.remove(a);
						score++;
						if (score%10 == 0) //Increases difficulty every 10 points
						{
							astFreq *= difAstFreq;
							astSpeed *= difAstSpeed;
							pUpFreq *= difPowUpFreq;
						}
					}
				}
			}
			
			for (Bullet bL : new ArrayList<Bullet>(pUpBulletsLeft)) //Collision for power up bullets
			{
				for (Asteroid a : new ArrayList<Asteroid>(asteroids))
				{
					if (a.getBody().intersects(bL.getBody()))
					{
						bullets.remove(bL);
						asteroids.remove(a);
						score++;
						if (score%10 == 0)
						{
							astFreq *= difAstFreq;
							astSpeed *= difAstSpeed;
							pUpFreq *= difPowUpFreq;
						}
					}
				}
				
				for (ExtraLife e : new ArrayList<ExtraLife>(extraLives))
				{
					if (e.getBody().intersects(bL.getBody()))
					{
						bullets.remove(bL);
						extraLives.remove(e);
						lives+=1;
					}
				}
			}
			
			for (Bullet bR : new ArrayList<Bullet>(pUpBulletsRight)) //Collision for power up bullets
			{
				for (Asteroid a : new ArrayList<Asteroid>(asteroids))
				{
					if (a.getBody().intersects(bR.getBody()))
					{
						bullets.remove(bR);
						asteroids.remove(a);
						score++;
						if (score%10 == 0)
						{
							astFreq *= difAstFreq;
							astSpeed *= difAstSpeed;
							pUpFreq *= difPowUpFreq;
						}
					}
				}
				
				for (ExtraLife e : new ArrayList<ExtraLife>(extraLives))
				{
					if (e.getBody().intersects(bR.getBody()))
					{
						bullets.remove(bR);
						extraLives.remove(e);
						lives+=1;
					}
				}
			}
			
			for (Bullet b : new ArrayList<Bullet>(bullets)) //Collision for bullets and Power ups
			{
				for (PowerUp p : new ArrayList<PowerUp>(powerUps))
				{
					if (p.getBody().intersects(b.getBody()))
					{
						bullets.remove(b);
						powerUps.remove(p);
						pUpTimer = pUpMaxTime;
					}
				}
			}
			
			for (Bullet b : new ArrayList<Bullet>(bullets)) //Collision for bullets and extra lives
			{
				for (ExtraLife e : new ArrayList<ExtraLife>(extraLives))
				{
					if (e.getBody().intersects(b.getBody()))
					{
						bullets.remove(b);
						extraLives.remove(e);
						lives+=1;
					}
				}
			}
			
			for (Asteroid a : new ArrayList<Asteroid>(asteroids))
			{
				if (a.getY() >= windowHeight)
				{
					asteroids.remove(a);
					lives--;
				}
			}

			

			if (lives <= 0) {
				tick = 1;
				gameState.toState(gameState.DEAD);
			}
			
			tick++;

		} else if (gameState.inState(gameState.DEAD)) {
			tick++;
			if (tick > 180)
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
			g.setFont(new Font("Verdana", Font.PLAIN, 40));
			g.drawString("READY!", windowWidth/2-100, windowHeight/2);
			
			Color myBlue = new Color(81, 132, 255);
			Color myRed = new Color(255, 74, 74);
			
			g.setColor(myRed);
			int[] xPts = {player1x, player1x+7, player1x , player1x-7};
			int[] yPts = {player1y-15, player1y+5, player1y , player1y+5};
			g.drawPolygon(xPts, yPts, xPts.length);
			
			g.setColor(myBlue);
			int[] x2Pts = {player2x, player2x+7, player2x , player2x-7};
			int[] y2Pts = {player2y-15, player2y+5, player2y , player2y+5};
			g.drawPolygon(x2Pts, y2Pts, x2Pts.length);

		}

		else if (gameState.inState(gameState.PLAYING)) {

			g = (Graphics2D) g;
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, windowWidth, windowHeight);
			g.setFont(new Font("Verdana", Font.PLAIN, 40)); 
			
			Color myBlue = new Color(81, 132, 255);
			Color myRed = new Color(255, 74, 74);
			
			g.setColor(myRed);
			int[] xPts = {player1x, player1x+7, player1x , player1x-7};
			int[] yPts = {player1y-15, player1y+5, player1y , player1y+5};
			g.drawPolygon(xPts, yPts, xPts.length);
			
			g.setColor(myBlue);
			int[] x2Pts = {player2x, player2x+7, player2x , player2x-7};
			int[] y2Pts = {player2y-15, player2y+5, player2y , player2y+5};
			g.drawPolygon(x2Pts, y2Pts, x2Pts.length);
			
			g.setColor(Color.GRAY);
			g.fillRect(10, 110, boxWidth, boxHeight);
			
			g.setColor(Color.CYAN);
			for (Bullet b : bullets)
			{
				g.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
			}
			for (Bullet bL : pUpBulletsLeft)
			{
				g.fillRect(bL.getX(), bL.getY(), bL.getWidth(), bL.getHeight());
			}
			for (Bullet bR : pUpBulletsRight)
			{
				g.fillRect(bR.getX(), bR.getY(), bR.getWidth(), bR.getHeight());
			}
			
//			if (pUpTimer == 0)
//				g.fillRect(10, 110, (int) (ammo/maxAmmo*boxWidth), boxHeight);
//			if (canShoot == false)
//				g.fillRect(10, 110, (int) (boxWidth - (reloadTimer/reloadSpeed)*boxWidth), boxHeight);
			
			g.setColor(Color.WHITE);
			for (Asteroid a : asteroids)
			{
				g.drawOval(a.getX(), a.getY(), a.getAstSize(), a.getAstSize());
			}
			
			g.setColor(Color.RED);
			for (PowerUp p : powerUps)
			{
				g.fillOval(p.getX(), p.getY(), p.getSize(), p.getSize());
			}
			g.fillRect(10, 110, (int) (pUpTimer/pUpMaxTime*boxWidth), boxHeight);
			
			g.setColor(Color.GREEN);
			for (ExtraLife e : extraLives)
			{
				g.fillOval(e.getX(), e.getY(), e.getSize(), e.getSize());
			}
			if (lives < 10)
			for (int i = 0; i < lives; i++)
			{
				g.fillOval(10+i*35, 65, 25, 25);
			}
			else
			{
				g.fillOval(10, 65, 25, 25);
				g.setColor(Color.WHITE);
				g.drawString("x" + lives, 45, 90);
			}
			
			g.setColor(Color.WHITE);
			g.drawString("Score:\t" + score, 10, 45);

		}

		else if (gameState.inState(gameState.DEAD)) {

			g.setColor(Color.WHITE);
			g.setFont(new Font("Verdana", Font.PLAIN, 40));
			g.drawString("Game Over", windowWidth/2-100, windowHeight/2);
			g.drawString("Final Score: " + score, windowWidth/2-150, windowHeight/2+50);

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