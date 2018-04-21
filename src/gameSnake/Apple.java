package gameSnake;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Apple {
	private Rectangle2D.Double body;

	public Apple(int windowWidth, int windowHeight, int size) {
		double x = Math.random() * (windowWidth - size * 4) + 2 * size;
		double y = Math.random() * (windowHeight - size * 4) + 2 * size;
		
		body = new Rectangle2D.Double(x, y, size, size);
	}

	void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect((int) body.getX(), (int) body.getY(), (int) body.getWidth(), (int) body.getHeight());
	}

	public Rectangle2D.Double getBody() {
		return body;
	}

}
