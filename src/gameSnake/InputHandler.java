package gameSnake;
import java.awt.Component;
import java.awt.event.*;

/**
 * Makes handling input a lot simpler
 */
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
	private boolean keys[] = new boolean[256];
	private boolean mousePressed = false;
	private boolean mouseInScreen = false;
	private int clicks = 0;
	private int mouseX = 0;
	private int mouseY = 0;

	public InputHandler(Component c) {
		c.addKeyListener(this);
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}

	public boolean isKeyDown(int keyCode) {
		if (keyCode > 0 && keyCode < 256) {
			return keys[keyCode];
		}

		return false;
	}

	public boolean isMouseDown() {
		return mousePressed;
	}

	public boolean isMouseInScreen() {
		return mouseInScreen;
	}

	public boolean wasClicked() {
		if (clicks > 0) {
			clicks--;
			return true;
		}
		return false;
	}

	public void resetClicks() {
		clicks = 0;
	}

	public int MouseX() {
		return mouseX;
	}

	public int MouseY() {
		return mouseY;
	}

	// event handlers for keys
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
			keys[e.getKeyCode()] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
			keys[e.getKeyCode()] = false;
		}
	}

	/**
	 * Not used
	 */
	public void keyTyped(KeyEvent e) {
	}

	// event handlers for mouse
	public void mouseEntered(MouseEvent e) {
		mouseInScreen = true;

	}

	public void mouseExited(MouseEvent e) {
		mouseInScreen = false;

	}

	public void mousePressed(MouseEvent e) {
		mousePressed = true;

	}

	public void mouseReleased(MouseEvent e) {
		mousePressed = false;

	}

	public void mouseClicked(MouseEvent e) {
		clicks++;
	}

	// mouse motion
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

	}

}