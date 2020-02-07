import javax.swing.*;
import java.util.Random;

public class Fish extends JPanel{
	private double posX = 0.0;
	private double posY = 0.0;
	
	private double addX;
	private double addY;
	
	private int destX = 0;
	private int destY = 0;
						
//	private int speed;
//	private int maxSpeed;
//	private int accel;
	
	private int type;
	private int size;
	private int steps;
	private int stepCount = 0;
	
	private boolean caught = false;
	private boolean isFeeding = false;
	
	public Fish (int startX, int startY, int type, int size, int steps) {
		this.posX = startX;
		this.posY = startY;
		this.type = type;
		this.size = size;
		this.steps = steps;
		
		Random rando = new Random();
		
		if (destX == 0 && destY == 0) {
			destX = rando.nextInt(800) - 100;
			destY = rando.nextInt(600) - 100;
		}
		else if (posX == destX || posY == destY) {
			destX = rando.nextInt(800) - 100;
			destY = rando.nextInt(600) - 100;
		}
		else if (stepCount == steps) {
			destX = rando.nextInt(800) - 100;
			destY = rando.nextInt(600) - 100;
		}

		addX = (destX - posX) / this.steps;
		addY = (destY - posY) / this.steps;
	}
	
	public void setDest(int destX, int destY) {
		this.destX = destX;
		this.destY = destY;
	}
	
	public void setPos(double posX, double posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}
	
	public void setCaught(boolean caught) {
		this.caught = caught;
	}
	
	public void setFeeding(boolean isFeeding) {
		this.isFeeding = isFeeding;
	}
	
	public double getPosX() {
		return this.posX;
	}
	
	public double getPosY() {
		return this.posY;
	}
	
	public double getDestX() {
		return this.destX;
	}
	
	public double getDestY() {
		return this.destY;
	}
	
	public double getAddX() {
		return this.addX;
	}
	
	public double getAddY() {
		return this.addY;
	}
	
	public int getFSize() {
		return this.size;
	}
	
	public void stepInc() {
		this.stepCount++;
	}
	
	public void resetSteps() {
		this.stepCount = 0;
		addX = (destX - posX) / this.steps;
		addY = (destY - posY) / this.steps;
	}
	
	public int getStepCount() {
		return this.stepCount;
	}

	public boolean getCaught() {
		return this.caught;
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getSteps() {
		return this.steps;
	}
	
	public boolean getFeeding() {
		return this.isFeeding;
	}
}
