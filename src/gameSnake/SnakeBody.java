package gameSnake;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class SnakeBody {
	private Ellipse2D.Double body;
	private double x;
	private double y;
	private int speed;
	private int size;
	static final int UP = 0;
	static final int RIGHT = 1;
	static final int DOWN = 2;
	static final int LEFT = 3;

	SnakeBody(double x, double y, int speed, int size) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.size = size;
		body = new Ellipse2D.Double(x, y, size, size);
	}

	public void draw(Graphics g, Color color) {
		g.setColor(color);
		g.drawOval((int) x, (int) y, size, size);
	}

	public Ellipse2D.Double getBody() {
		return body;
	}

	public void move(double x2, double y2, double dis) {
		double yDiff = y2 - y;
		double xDiff = x2 - x;
		x += xDiff / 2;
		y += yDiff / 2;
		body.x = x;
		body.y = y;

	}

	public void move(double dir) {

		x += Math.cos(dir) * speed;
		y -= Math.sin(dir) * speed;

		body.x = x;
		body.y = y;
	}

	double getX() {
		return x;
	}

	double getY() {
		return y;
	}

	void setSpeed(int speed) {
		this.speed = speed;
	}

	public double getSize() {
		return size;
	}

}
