package multigame;

import javax.imageio.ImageIO;
import javax.swing.*;

import screensaver.ScreenSaver;
import menu.Menu;

import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;

public class MultiGame extends JFrame implements Runnable {

	private long code = 123456L;

	// used for page flipping
	private static final int NUM_BUFFERS = 2;
	static int DEFAULT_FPS = 150;
	// record stats every 1 second (roughly)
	private static long MAX_STATS_INTERVAL = 1000000000L;
  
	// Number of frames with a delay of 0 ms before the
	// animation thread yields to other running threads.
	private static final int NUM_DELAYS_PER_YIELD = 16;

	// was 2; num of frames that can be skipped in any one anim. loop
	// i.e the games state is updated but not rendered
	private static int MAX_FRAME_SKIPS = 5;

	private static int NUM_FPS = 10;
	// number of FPS values stored to get an average

	// panel dimensions
	private int pWidth, pHeight;

	// used for gathering statistics
	private long statsInterval = 0L;
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	// seconds
	private int timeSpentInGame = 0;
	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private DecimalFormat df = new DecimalFormat("0.##");
	private DecimalFormat timedf = new DecimalFormat("0.####");
	// the thread that performs the animation
	private Thread animator;
	// to stop the animation thread
	private volatile boolean running = false;
	// used period between drawing in nanosecs
	private long period;
	// while in menu screen

	// used at game termination
	private volatile boolean gameOver = false;
	private Font selectGameFont;
	private FontMetrics metrics;
	private boolean finishedOff = false;

	// used for full-screen exclusive mode
	private GraphicsDevice gd;
	// private Graphics gScr;
	private Graphics2D gScr2d;
	private BufferStrategy bufferStrategy;

	// /////////////////////////////////////////////////////////

	private Game curGame;
	private KeyHandler kh;
	private int gameIndex = 0;

	private int fps = 150;

	private ArrayList<String> gameList;
	// private ArrayList<Integer> gameListFreq;

	private ArrayList<BufferedImage> imageList;
	private MultiGameState gameState;
	private ScreenSaver scrSaver;
	private Menu menu;

	private int credits;

	private String logFileName = "";
	private EasyWriter outFileLog;

	private boolean freePlay = false;

	// private ArrayList<Integer>

	// /////////////////////////////////////////////////////////
	/**
	 * Constructor takes a long number of frames per second which corresponds to
	 * the somewhat guaranteed updates per second.<br>
	 * If freePlay is false, initial credits is 0, otherwise is 888
	 */
	public MultiGame(long fps, boolean freePlay) {
		super("MultiGame");
		this.freePlay = freePlay;
		kh = new KeyHandler(this);

		// needed for MultiGame initialization
		period = (long) 1000.0 / fps;
		period *= 1000000L;// ms --> nanosecs
		this.period = period;

		this.fps = (int) fps;

		initFullScreen();
		readyForTermination();

		initStatsVars();
		initKeyListener();

		// set up message font
		selectGameFont = new Font("Arial", Font.BOLD, 32);
		metrics = this.getFontMetrics(selectGameFont);

		initGameList();
		gameState = new MultiGameState(this);
		scrSaver = new ScreenSaver(this);
		menu = new Menu(this);

		logFileName = "mgLog.txt";
		try {
			File f;
			f = new File(logFileName);
			if (!f.exists()) {
				f.createNewFile();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
		outFileLog = new EasyWriter(logFileName, "app");
		outFileLog.println("MultiGame System Launch: - " + new Date());
		outFileLog.close();

		setPeriod(10);
		gameState.toState(gameState.SCREENSAVER);

		animationStart();
	} // end of MultiGame(int fps, boolean freePlay) constructor

	/**
	 * Used for going into screensaver mode to save energy (cpu usage)
	 */
	void setPeriod(long fps) {
		period = (long) 1000.0 / fps;
		period *= 1000000L;// ms --> nanosecs
	}

	/**
	 * returns the fps as an int
	 */
	public int getFps() {
		return this.fps;
	}

	/**
	 * Parameter code is used to validate the KeyEvent for a credit
	 */
	public void addCredit(long code) {
		if (!(gameState.inState(gameState.MENU) || gameState
				.inState(gameState.SCREENSAVER)))
			return;
		if (freePlay)
			return;
		if (code == this.code) {
			menu.playCreditSound();
			credits += 1;
		}
		// from coin door
		if (code == 666L) {
			if (credits == 0) {
				menu.playCreditSound();
				credits += 1;
			}
		}
	}

	/**
	 * Parameter code is used to validate the KeyEvent for a credit
	 */
	public void addCredit(long code, int numCredits) {
		if (!(gameState.inState(gameState.MENU) || gameState
				.inState(gameState.SCREENSAVER)))
			return;
		if (freePlay)
			return;
		if (code == this.code) {
			menu.playCreditSound();
			credits += numCredits;
		}
	}

	/**
	 * Parameter code is used to validate the KeyEvent for a credit
	 */
	public void subtractCredits(int cr) {
		if (!(gameState.inState(gameState.MENU) || gameState
				.inState(gameState.SCREENSAVER)))
			return;
		if (freePlay)
			return;
		// credits - cr == 1 .. should always be true
		credits = kh.getCreditsPerGame();
	}

	/**
	 * Returns the current number of credits
	 */
	public int getCredits() {
		return credits;
	}

	private void initStatsVars() {
		// initialize timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}
	}

	private void initKeyListener() {
		// inner class for handling KeyEvents
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ESCAPE)) {

					Toolkit tk = Toolkit.getDefaultToolkit();
					tk.setLockingKeyState(KeyEvent.VK_NUM_LOCK, true);

					running = false;
				}
				// kh.handleKey(e.getKeyCode(), true);
				kh.handleKey(e.getKeyCode(), e.getKeyChar(), true);
			}

			public void keyReleased(KeyEvent e) {
				// kh.handleKey(e.getKeyCode(), false);
				kh.handleKey(e.getKeyCode(), e.getKeyChar(), false);

			}
		});
	}

	private void initGameList() {
		gameList = new ArrayList<String>();

		File dir = new File("menuimages");
		// menuimages resides in the src folder in eclipse project folder
		// a copy is in the bin for running MultiGame21 outside of eclipse
		if (!dir.exists()) {
			dir = new File("src\\menuimages");
		} else if (!dir.exists()) {
			dir = new File(".");
		}

		String dirList[] = dir.list();
		for (String str : dirList) {
			// find the index of the first capitol letter
			// and create the "packageName.className" String
			// System.out.println(str);
			str = str.substring(0, str.length() - 4);// remove file extension
			int firstCapPos = 0;
			for (int i = 0; i < str.length(); i++) {
				if (Character.isUpperCase(str.charAt(i))) {
					firstCapPos = i;
					break;
				}
			}
			// only add name to gameList if a cap exists
			// this will exclude package multigame, menu and screensaver
			if (firstCapPos > 0) {
				String packageName = str;
				String className = str + "." + str.substring(firstCapPos);
				gameList.add(className);

			}
		}
		System.out.println("\n");
		for (String str : gameList) {
			System.out.println(str);
		}
		// System.out.println(imageList.size());
	}

	// accessors /////////////////////////////////////////////////
	/**
	 * Returns the current KeyHandler.
	 */
	public KeyHandler getKeyHandler() {
		return kh;
	}

	/**
	 * Returns the width of the panel (or window).
	 */
	public int pWidth() {
		return pWidth;
	}

	/**
	 * Returns the height of the panel (or window).
	 */
	public int pHeight() {
		return pHeight;
	}

	/**
	 * Returns the current Menu.
	 */
	public Menu getMenu() {
		return menu;
	}

	public int getGameIndex() {
		return gameIndex;
	}

	public void setGameIndex(int index) {
		gameIndex = index;
	}

	/**
	 * sets menu to null
	 */
	public void destroyMenu() {
		menu.destroyMenu();

		return;
	}

	/**
	 * sets menu to null
	 */
	public void initMenu() {
		setPeriod(DEFAULT_FPS);
		menu = new Menu(this);
	}

	/**
	 * Returns the current gameList.
	 */
	public ArrayList<String> getGameList() {
		return gameList;
	}

	/**
	 * Returns the current imageList... <br>
	 * (which should be the same size as gameList)
	 */
	public ArrayList<BufferedImage> getImageList() {
		return imageList;
	}

	/**
	 * Returns the current MultiGameState.<br>
	 * (needed to be called from Menu to resetMenuTicks)
	 */
	public MultiGameState getMultiGameState() {
		return gameState;
	}

	/**
	 * Returns the current period or number of milliseconds between frames.
	 */
	public int getPeriod() {
		return (int) (period / 1000000L);
	}

	/**
	 * Returns the status of MultiGame running in freePlay mode
	 */
	public boolean getFreePlay() {
		return freePlay;
	}

	/**
	 * Returns the Graphics2D for Multigame
	 */
	public Graphics2D getGraphics2D() {
		return gScr2d;
	}

	// /////////////////////////////////////////////////////////

	private void gameRender(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, pWidth, pHeight);

		if (gameState.inState(gameState.SCREENSAVER)) {
			scrSaver.render(g);
		}

		else if (gameState.inState(gameState.MENU)) {
			menu.render(g);
		}

		else if (gameState.inState(gameState.GAMEPLAYING)) {
			try {
				curGame.gameRender(g);
			} catch (Exception e) {
				e.printStackTrace();
				curGame = null;
				// force garbage collection
				gameState.toState(gameState.MENU);
			}
		}

	} // end of gameRender()

	private void gameUpdate() {

		gameState.tick();

		if (gameState.inState(gameState.SCREENSAVER)) {
			if (kh.wasLeftJustPressed(1) || kh.wasRightJustPressed(1)
					|| kh.wasBtn1JustPressed(1)) {
				// curGame = new game of selected game
				gameState.toState(gameState.MENU);
			}
		}

		else if (gameState.inState(gameState.MENU)) {
			menu.update();
			// launch single game
			if ((kh.wasStartBtn1JustPressed())
					&& (credits >= kh.getCreditsPerGame() || freePlay)
					&& gameState.getCurTick() > 150) {
				// don't accept clicks until after 1 seconds

				// curGame = new game of selected game
				// curGame = new ruthLightCycle.LightCycle(this);
				try {
					gameIndex = menu.getGameIndex();
					Class temp = getClass().getClassLoader().loadClass(
							gameList.get(gameIndex));
					Constructor con = temp.getConstructors()[0];

					curGame = (Game) con.newInstance(this);
					credits -= kh.getCreditsPerGame();
					outFileLog = new EasyWriter(logFileName, "app");
					outFileLog.println("Game Played: "
							+ gameList.get(gameIndex) + " - " + new Date());
					outFileLog.close();
					gameState.toState(gameState.INITGAME);
				} catch (Exception e) {
					System.err
							.println("YOUR GAME MUST HAVE ONLY ONE CONSTRUCTOR\n"
									+ "WITH EXACTLY ONE PARAMETER.. public GameName(MultiGame mg)");
					e.printStackTrace();
				}
			}
		}

		else if (gameState.inState(gameState.GAMEPLAYING)) {
			if (kh.wasExitBtnJustPressed()) {
				kh.init();
				curGame = null;
				gameState.toState(gameState.MENU);
				return;
			}
			try {
				curGame.gameUpdate();

				if (curGame.isGameOver()) {
					kh.init();
					curGame = null;
					gameState.toState(gameState.MENU);
				}
			} catch (Exception e) {
				e.printStackTrace();
				curGame = null;
				// force garbage collection

				gameState.toState(gameState.MENU);
			}
		}

	} // end of gameUpdate()

	private void screenUpdate() {
		// use active rendering
		try {
			gScr2d = (Graphics2D) bufferStrategy.getDrawGraphics();
			gameRender(gScr2d);
			gScr2d.dispose();

			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents Lost");
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}
	} // end of screenUpdate()

	private void animationStart() {
		// initialise and start the thread
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of gameStart()

	private void readyForTermination() {

		// for shutdown tasks
		// a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				running = false;
				finishOff();
			}
		});
	} // end of readyForTermination()

	private void initFullScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		setUndecorated(true); // no menu bar, borders, etc. or Swing components
		setIgnoreRepaint(true); // turn off all paint events since doing active
		// rendering
		setResizable(false);

		if (!gd.isFullScreenSupported()) {
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}
		gd.setFullScreenWindow(this); // switch on full-screen exclusive mode

		// we can now adjust the display modes, if we wish
		showCurrentMode();

		// setDisplayMode(800, 600, 8); // or try 8 bits
		// setDisplayMode(1280, 1024, 32);

		// reportCapabilities();

		pWidth = getBounds().width;
		pHeight = getBounds().height;

		setBufferStrategy();

		// hide mouse cursor
		Toolkit tk = Toolkit.getDefaultToolkit();
		setCursor(tk.createCustomCursor(new BufferedImage(32, 32,
				BufferedImage.TYPE_4BYTE_ABGR), new Point(0, 0), ""));
		tk.setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);

	} // end of initFullScreen()

	private void reportCapabilities() {
		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		// Image Capabilities
		ImageCapabilities imageCaps = gc.getImageCapabilities();
		System.out.println("Image Caps. isAccelerated: "
				+ imageCaps.isAccelerated());
		System.out.println("Image Caps. isTrueVolatile: "
				+ imageCaps.isTrueVolatile());

		// Buffer Capabilities
		BufferCapabilities bufferCaps = gc.getBufferCapabilities();
		System.out.println("Buffer Caps. isPageFlipping: "
				+ bufferCaps.isPageFlipping());
		System.out.println("Buffer Caps. Flip Contents: "
				+ getFlipText(bufferCaps.getFlipContents()));
		System.out.println("Buffer Caps. Full-screen Required: "
				+ bufferCaps.isFullScreenRequired());
		System.out.println("Buffer Caps. MultiBuffers: "
				+ bufferCaps.isMultiBufferAvailable());
	} // end of reportCapabilities()

	private String getFlipText(BufferCapabilities.FlipContents flip) {
		if (flip == null)
			return "false";
		else if (flip == BufferCapabilities.FlipContents.UNDEFINED)
			return "Undefined";
		else if (flip == BufferCapabilities.FlipContents.BACKGROUND)
			return "Background";
		else if (flip == BufferCapabilities.FlipContents.PRIOR)
			return "Prior";
		else
			// if (flip == BufferCapabilities.FlipContents.COPIED)
			return "Copied";
	} // end of getFlipTest()

	private void setBufferStrategy()

	/*
	 * Switch on page flipping: NUM_BUFFERS == 2 so there will be a 'primary
	 * surface' and one 'back buffer'.
	 * 
	 * The use of invokeAndWait() is to avoid a possible deadlock with the event
	 * dispatcher thread. Should be fixed in J2SE 1.5
	 * 
	 * createBufferStrategy) is an asynchronous operation, so sleep a bit so
	 * that the getBufferStrategy() call will get the correct details.
	 */
	{
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					createBufferStrategy(NUM_BUFFERS);
				}
			});
		} catch (Exception e) {
			System.out.println("Error while creating buffer strategy");
			System.exit(0);
		}

		try { // sleep to give time for the buffer strategy to be carried out
			Thread.sleep(500); // 0.5 sec
		} catch (InterruptedException ex) {
		}

		bufferStrategy = getBufferStrategy(); // store for later
	} // end of setBufferStrategy()

	public void run() {
		// The frames of the animation are drawn inside the while loop.
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		sleep(500);// sleep 1/2 second at the beginning
		gameStartTime = System.nanoTime();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;
		running = true;

		while (running) {
			gameUpdate();
			screenUpdate();
			afterTime = getNanoTime();// checks for invalid System.nanoTime()
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					// System.out.println("tempTime = " + tempTime);
				}
				overSleepTime = (getNanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NUM_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}
			beforeTime = getNanoTime();
			// If frame animation is taking too long,
			// update the game state without rendering it,
			// to get the updates/sec nearer to the required FPS.
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			framesSkipped += skips;

			storeStats();
		}// end while(running)
		finishOff();
	} // end of run()

	private long getNanoTime() {
		long tempTime;
		// added to handly nanoTime() irregularities
		// 1902707657806868
		tempTime = System.nanoTime();
		// System.out.println("tempTime = " + tempTime);
		while (tempTime > 4902707657806868L || tempTime < 0L) {
			tempTime = System.nanoTime();
			System.out.println("tempTime = " + tempTime);
		} // //////////////////////////////////////////
		return tempTime;
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The statistics: - the summed periods for all the iterations in this
	 * interval (period is the amount of time a single frame iteration should
	 * take), the actual elapsed time in this interval, the error between these
	 * two numbers; - the total frame count, which is the total number of calls
	 * to run(); - the frames skipped in this interval, the total number of
	 * frames skipped. A frame skip is a game update without a corresponding
	 * render; - the FPS (frames/sec) and UPS (updates/sec) for this interval,
	 * the average FPS & UPS over the last NUM_FPSs intervals.
	 * 
	 * The data is collected every MAX_STATS_INTERVAL (1 sec).
	 */
	private void storeStats() {

		frameCount++;
		statsInterval += period;

		if (statsInterval >= MAX_STATS_INTERVAL) {
			// record stats every MAX_STATS_INTERVAL
			long timeNow = System.nanoTime();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); // ns
			// -->
			// secs

			long realElapsedTime = timeNow - prevStatsTime; // time since last
			// stats
			// collection
			totalElapsedTime += realElapsedTime;

			double timingError = ((double) (realElapsedTime - statsInterval) / statsInterval) * 100.0;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			/*
			 * System.out.println(timedf.format( (double)
			 * statsInterval/1000000000L) + " " + timedf.format((double)
			 * realElapsedTime/1000000000L) + "s " + df.format(timingError) +
			 * "% " + frameCount + "c " + framesSkipped + "/" +
			 * totalFramesSkipped + " skip; " + df.format(actualFPS) + " " +
			 * df.format(averageFPS) + " afps; " + df.format(actualUPS) + " " +
			 * df.format(averageUPS) + " aups" );
			 */
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

	private void printStats() {
		System.out.println("Frame Count/Loss: " + frameCount + " / "
				+ totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
	} // end of printStats()

	private void finishOff() {

		// Tasks to do before terminating. Called at end of run() and via the
		// shutdown hook in readyForTermination().
		// The call at the end of run() is not really necessary, but included
		// for
		// safety. The flag stops the code being called twice.

		// System.out.println("finishOff");
		if (!finishedOff) {
			finishedOff = true;
			printStats();
			restoreScreen();

			// Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK,
			// false);
			System.exit(0);
		}
	} // end of finishedOff()

	private void restoreScreen() {
		// Switch off full screen mode.
		// This also resets the display mode if it's been changed.
		Window w = gd.getFullScreenWindow();
		if (w != null)
			w.dispose();
		gd.setFullScreenWindow(null);
	} // end of restoreScreen()

	// ------------------ display mode methods -------------------

	private void setDisplayMode(int width, int height, int bitDepth) {
		// attempt to set the display mode to the given width, height, and bit
		// depth
		if (!gd.isDisplayChangeSupported()) {
			System.out.println("Display mode changing not supported");
			return;
		}

		if (!isDisplayModeAvailable(width, height, bitDepth)) {
			System.out.println("Display mode (" + width + "," + height + ","
					+ bitDepth + ") not available");
			return;
		}

		DisplayMode dm = new DisplayMode(width, height, bitDepth,
				DisplayMode.REFRESH_RATE_UNKNOWN); // any refresh rate
		try {
			gd.setDisplayMode(dm);
			System.out.println("Display mode set to: (" + width + "," + height
					+ "," + bitDepth + ")");
		} catch (IllegalArgumentException e) {
			System.out.println("Error setting Display mode (" + width + ","
					+ height + "," + bitDepth + ")");
		}

		try { // sleep to give time for the display to be changed
			Thread.sleep(1000); // 1 sec
		} catch (InterruptedException ex) {
		}
	} // end of setDisplayMode()

	private boolean isDisplayModeAvailable(int width, int height, int bitDepth) {
		// Check that a displayMode with this width, height, bit depth is
		// available.
		// We don't care about the refresh rate, which is probably
		// REFRESH_RATE_UNKNOWN anyway.

		DisplayMode[] modes = gd.getDisplayModes();
		showModes(modes);

		for (int i = 0; i < modes.length; i++) {
			if (width == modes[i].getWidth() && height == modes[i].getHeight()
					&& bitDepth == modes[i].getBitDepth())
				return true;
		}
		return false;
	} // end of isDisplayModeAvailable()

	// pretty print the display mode information in modes
	private void showModes(DisplayMode[] modes) {
		System.out.println("Modes");
		for (int i = 0; i < modes.length; i++) {
			System.out.print("(" + modes[i].getWidth() + ","
					+ modes[i].getHeight() + "," + modes[i].getBitDepth() + ","
					+ modes[i].getRefreshRate() + ")  ");
			if ((i + 1) % 4 == 0)
				System.out.println();
		}
		System.out.println();
	} // end of showModes()

	// print the display mode details for the graphics device
	private void showCurrentMode() {
		DisplayMode dm = gd.getDisplayMode();
		System.out.println("Current Display Mode: (" + dm.getWidth() + ","
				+ dm.getHeight() + "," + dm.getBitDepth() + ","
				+ dm.getRefreshRate() + ")  ");
	}

} // end of MultiGame class

