package gameSnake;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Player {
	ArrayList<SnakeBody> body;
	SnakeBody head;
	int x;
	int y;
	int size;
	int speed;

	public Player(int x, int y, int size, int speed, int length) {
		body = new ArrayList<SnakeBody>();
		head = new SnakeBody(x, y, speed, size);
		this.size = size;
		this.speed = speed;
		body.add(head);
		growBy(length - 1);
	}
	
	void growBy(int length) {
		for (int i = 0; i < length; i++) {
			body.add(new SnakeBody(body.get(body.size() - 1).getX(), body.get(body.size() - 1).getY(), speed, size));
		}
	}
	
	void draw(Graphics g, Color color) {
		for (int i = 0; i < body.size(); i++) {
			body.get(i).draw(g, color);
		}
		
	}
	
	void draw(Graphics g, int tick){
		head.draw(g, Color.WHITE);
		
		
		for (int i = 0; i < body.size(); i++) {
			Color color;
			int j = (tick + i) % 14;

			switch (j) {
			case 0:
			case 1:
				color = Color.RED;
				break;
			case 2:
			case 3:
				color = Color.ORANGE;
				break;
			case 4:
			case 5:
				color = Color.YELLOW;
				break;
			case 6:
			case 7:
				color = Color.GREEN;
				break;
			case 8:
			case 9:
				color = Color.CYAN;
				break;
			case 10:
			case 11:
				color = Color.BLUE;
				break;
			case 12:
			case 13:
				color = Color.MAGENTA;
				break;
			default:
				color = Color.WHITE;

			}

			body.get(i).draw(g, color);
		}
	}

	boolean collidesWithApple(Apple a) {
		if (head.getBody().intersects(a.getBody())) {
			return true;
		}
		return false;
	}

	ArrayList<SnakeBody> getBody(){
		return body;
	}
	
	boolean collidesWithPlayer(Player p) {
		ArrayList<SnakeBody> pBody = p.getBody();
		
		double tempX = head.getBody().getCenterX();
		double tempY = head.getBody().getCenterY();

		for (int i = 0; i < pBody.size(); i++) {
			double tempX2 = pBody.get(i).getBody().getCenterX();
			double tempY2 = pBody.get(i).getBody().getCenterY();

			double dis = Math.sqrt((tempY2 - tempY) * (tempY2 - tempY) + (tempX2 - tempX) * (tempX2 - tempX));
			
			

			if (dis <= head.getSize()) {
				return true;
			}
		}
		return false;
	}
	
	
	boolean collidesWithSelf() {
		double tempX = head.getBody().getCenterX();
		double tempY = head.getBody().getCenterY();

		double tempX3 = body.get(1).getBody().getCenterX();
		double tempY3 = body.get(1).getBody().getCenterY();

		for (int i = 5; i < body.size(); i++) {
			double tempX2 = body.get(i).getBody().getCenterX();
			double tempY2 = body.get(i).getBody().getCenterY();

			double dis = Math.sqrt((tempY2 - tempY) * (tempY2 - tempY) + (tempX2 - tempX) * (tempX2 - tempX));
			double dis2 = Math.sqrt((tempY2 - tempY3) * (tempY2 - tempY3) + (tempX2 - tempX3) * (tempX2 - tempX3));
			
			System.out.println("dis: " + dis);
			System.out.println("head size: " + head.getSize());
			if (dis <= head.getSize() && dis2 > dis) {
				return true;
			}
		}
		return false;
	}

	

	double getMaxHeadX() {

		return head.getBody().getMaxX();
	}

	double getMinHeadX() {

		return head.getBody().getMinX();
	}

	double getMaxHeadY() {

		return head.getBody().getMaxY();
	}

	double getMinHeadY() {

		return head.getBody().getMinY();
	}
	
	void setSpeed(int s) {
		speed = s;
		head.setSpeed(speed);
	}

	public void move(double dir) {
		head.move(dir);
		for (int i = 1; i < body.size(); i++) {
			body.get(i).move(body.get(i - 1).getX(), body.get(i - 1).getY(), size);
		}
	}
}
