package multigame;

import static java.awt.event.KeyEvent.*;

/**
 * 
 * <xmp> <code> <pre>
 * 
 * for MultiGameUprite2013
 * added support for key just pressed events as well as isPressed status
 * 
 * two players:
 * each player has 4 keys (four directions and one button)
 * 
 * joystick :            up
 *            left       +       right
 *                      down
 *                      
 * buttons  : btn1 
 *       
 * 
 *  individual controls for game cabinet
 * 
 * coins    : coin1 , coin2
 * 
 *      
 *    joystik     buttons            joystik     buttons
 *  |---------------------------------------------| 
 *  |                     6                       |
 *  |     r                      ^                |
 *  |   d + g       q          < + >       ]      |
 *  |     f                      v                |
 *  |                     [                       |
 *  |---------------------------------------------|
 *          PLAYER_2             PLAYER_1
 *          
 *                                      (arrow keys..)
 *    coin1 = 'e'
 *    
 *    
 * </pre> </code> </xmp>
 */

public class KeyHandler {

	private long code = 123456L;
	private int numPlayers = 2;

	private Control up[] = new Control[2]; // joystick up
	private Control down[] = new Control[2]; // joystick down
	private Control right[] = new Control[2]; // joystick right
	private Control left[] = new Control[2]; // joystick left
	private Control btn1[] = new Control[2]; // lower left button
	private Control startBtn1; // upper start button
	private Control startBtn2; // lower start button
	private Control exitBtn;
	private Control coin1;
	private Control coin2;

	private int creditsPerGame;
	private boolean coin[] = new boolean[2]; // coin slots
	private MultiGame mg;

	/**
	 * Default constructor.
	 */
	public KeyHandler() {
		new KeyHandler(null);
	}

	/**
	 * Constructor has a parameter so KeyHandler has a reference to the
	 * MultiGame Object.
	 */
	public KeyHandler(MultiGame mg) {
		this.mg = mg;
		init();
		creditsPerGame = 1;
	}

	/**
	 * Calls addCredit in MultiGame. Only adds one credit when the coin switch
	 * is pressed.
	 */
	void addCredits() {
		if (!coin[0] && !coin[1]) {
			mg.addCredit(code);
			// Sound.playSound(0);
		}
	}

	/**
	 * Calls addCredit in MultiGame. Only adds one credit when the coin switch
	 * is pressed.
	 */
	void addCreditsCoin() {
		if (!coin[0] && !coin[1]) {
			mg.addCredit(666L);
			// Sound.playSound(0);
		}
	}

	/**
	 * Returns the number of creditsPerGame. Right now this is always one.
	 */
	public int getCreditsPerGame() {
		return creditsPerGame;
	}

	private void setVal(Control ctl[], int index, boolean press) {
		if (press) { // press
			ctl[index].pressed = true;
			if (ctl[index].clickReady) {
				ctl[index].clickReady = false;
				ctl[index].clicked = true;
			}
		} else { // release
			ctl[index].clickReady = true;
			ctl[index].pressed = false;
		}
	}

	private void setVal(Control ctl, boolean press) {
		if (press) { // press
			ctl.pressed = true;
			if (ctl.clickReady) {
				ctl.clickReady = false;
				ctl.clicked = true;
			}
		} else { // release
			ctl.clickReady = true;
			ctl.pressed = false;
		}
	}

	void init() {
		exitBtn = new Control();
		startBtn1 = new Control();
		startBtn2 = new Control();
		// System.out.println(idNum);
		for (int i = 0; i < 2; i++) {
			up[i] = new Control();
			down[i] = new Control();
			right[i] = new Control();
			left[i] = new Control();
			btn1[i] = new Control();
		}
	}

	/**
	 * <pre>
	 * Sample usage:
	 * if keyChar is a digit.. it might be from the bar code reader..
	 * shift the digits in String idNum to the left and place keyChar as the last digit
	 * 
	 * 
	 * </pre>
	 */
	void handleKey(int keyCode, char keyChar, boolean press) {

		switch (keyCode) {

		// PLAYER_1
		case VK_UP:
			setVal(up, 0, press);
			break;
		case VK_LEFT:
			setVal(left, 0, press);
			break;
		case VK_DOWN:
			setVal(down, 0, press);
			break;
		case VK_RIGHT:
			setVal(right, 0, press);
			break;
		case VK_CLOSE_BRACKET:
			setVal(btn1, 0, press);
			break;

		// PLAYER_2
		case VK_R:
			setVal(up, 1, press);
			break;
		case VK_D:
			setVal(left, 1, press);
			break;
		case VK_F:
			setVal(down, 1, press);
			break;
		case VK_G:
			setVal(right, 1, press);
			break;
		case VK_Q:
			setVal(btn1, 1, press);
			break;

		// UPPER START BUTTON
		case VK_6:
			setVal(startBtn1, press);
			break;

		// LOWER START BUTTON
		case VK_OPEN_BRACKET:
			setVal(startBtn2, press);
			break;

		// BILL ... not working at this time
		// case VK_PLUS:
		// addCredits();
		// coin[0] = press;
		// break;

		// COIN
		case VK_S:
		case VK_E:
			addCreditsCoin();
			coin[1] = press;
			break;

		// gameQuit .. key 'Z'
		case VK_Z:
			if (press) { // press
				exitBtn.pressed = true;
				if (exitBtn.clickReady) {
					exitBtn.clickReady = false;
					exitBtn.clicked = true;
				}
			} else { // release
				exitBtn.clickReady = true;
				exitBtn.pressed = false;
			}
			break;
		default:
		}
	}

	/**
	 * returns true if 'Z' key has been clicked
	 */
	public boolean wasExitBtnJustPressed() {
		if (exitBtn.clicked) {
			exitBtn.clicked = false;
			return true;
		} else {
			return false;
		}
	}

	// void clearAllClicks() {
	// enter = new Control();
	// // System.out.println(idNum);
	// for (int i = 0; i < 4; i++) {
	// up[i] = new Control();
	// down[i] = new Control();
	// right[i] = new Control();
	// left[i] = new Control();
	// btn1[i] = new Control();
	// btn2[i] = new Control();
	// btn3[i] = new Control();
	// btn4[i] = new Control();
	// }
	// }

	/**
	 * returns the status of joystick up of player playerNum
	 */
	public boolean isUpPressed(int playerNum) {
		return up[playerNum - 1].pressed;
	}

	/**
	 * returns true if joystick up of player playerNum has been clicked
	 */
	public boolean wasUpJustPressed(int playerNum) {
		if (up[playerNum - 1].clicked) {
			up[playerNum - 1].clicked = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the status of joystick left of player playerNum
	 */
	public boolean isLeftPressed(int playerNum) {
		return left[playerNum - 1].pressed;
	}

	/**
	 * returns true if joystick left of player playerNum has been clicked
	 */
	public boolean wasLeftJustPressed(int playerNum) {
		if (left[playerNum - 1].clicked) {
			left[playerNum - 1].clicked = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the status of joystick down of player playerNum
	 */
	public boolean isDownPressed(int playerNum) {
		return down[playerNum - 1].pressed;
	}

	/**
	 * returns true if joystick down of player playerNum has been clicked
	 */
	public boolean wasDownJustPressed(int playerNum) {
		if (down[playerNum - 1].clicked) {
			down[playerNum - 1].clicked = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the status of joystick right of player playerNum
	 */
	public boolean isRightPressed(int playerNum) {
		return right[playerNum - 1].pressed;
	}

	/**
	 * returns true if joystick right of player playerNum has been clicked
	 */
	public boolean wasRightJustPressed(int playerNum) {
		if (right[playerNum - 1].clicked) {
			right[playerNum - 1].clicked = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the status of lower left button (btn1) of player playerNum
	 */
	public boolean isBtn1Pressed(int playerNum) {
		return btn1[playerNum - 1].pressed;
	}

	/**
	 * returns true if btn1 of player playerNum has been clicked
	 */
	public boolean wasBtn1JustPressed(int playerNum) {
		if (btn1[playerNum - 1].clicked) {
			btn1[playerNum - 1].clicked = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the status of upper start button
	 */
	public boolean isStartBtn1Pressed() {
		return startBtn1.pressed;
	}

	/**
	 * returns true if upper start button has been clicked
	 */
	public boolean wasStartBtn1JustPressed() {
		if (startBtn1.clicked) {
			startBtn1.clicked = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the status of lower start button
	 */
	public boolean isStartBtn2Pressed() {
		return startBtn2.pressed;
	}

	/**
	 * returns true if lower start button has been clicked
	 */
	public boolean wasStartBtn2JustPressed() {
		if (startBtn2.clicked) {
			startBtn2.clicked = false;
			return true;
		} else {
			return false;
		}
	}

	// inner class
	class Control {
		boolean pressed;
		boolean clicked;
		boolean clickReady;

		public Control() {
			pressed = false;
			clicked = false;
			clickReady = true;
		}
	}

}