import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Stack;
import java.awt.geom.Ellipse2D;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.geom.Point2D;


class FishPondGame extends JPanel implements Runnable
{
	static private final Color OUTLINE_COLOR = Color.BLACK;
	
	//thread things
	private Thread thread;
	private boolean isMovin;
	
	// panel size
	private final int WIDTH, MAX_X;
	private final int HEIGHT, MAX_Y;
	
	// image displayed on panel
	private BufferedImage image;
	private BufferedImage fishLog;
	private BufferedImage main;
	private Graphics2D g2d;
	private Graphics2D fishG;
	private Graphics2D log2D;
	float netStroke = 2.5f;
	
	//menu icons
	private BufferedImage menu;
	private BufferedImage hint;
	private BufferedImage food;
	private BufferedImage net;
	private BufferedImage fish;
	
	private Fish[] fishes = new Fish[7];
	private int fishCount = 0;
	//private int fishTime = 0;
	
	//fish log numbers
	private int logY = 0;
	private int textY = 30;
	private int logNum = 0;		//number placed after "fish_log_" when saving your log image
	private DecimalFormat f = new DecimalFormat("##.00");
	
	// current selection
	private int x = -1;
	private int y = -1;
	private int w = 0;
	private int h = 0;
	
	private int leftX = -1;
	private int leftY = -1;
	
	private int rightX = -1;
	private int rightY = -1;
	
	//Animation Stuff
	Random rando = new Random();
	
	//Food stuff
	final int foodColor = 0xFFd7a76c;
	private Stack<Point> foodCoords = new Stack<Point>();
	
	//Button Settings
	private boolean isNetting;
	private boolean isFeeding;
	//------------------------------------------------------------------------
	// constructor
	public FishPondGame( BufferedImage image )
	{
		//makeTitleScreen();
		importMenuIcons();
		start();
		this.image = image;
		BasicStroke stroke = new BasicStroke(netStroke);
		g2d = image.createGraphics();
		fishG = image.createGraphics();
		g2d.setXORMode( Color.BLACK );
		g2d.setStroke(stroke);
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON );
		fishG.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON );
		
		// define panel characteristics
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		Dimension size = new Dimension( WIDTH, HEIGHT );
		setMinimumSize( size );
		setMaximumSize( size );
		setPreferredSize( size );
		MAX_X = WIDTH - 1;
		MAX_Y = HEIGHT - 1;
		isNetting = false;
		
		//Fish log JFrame
		JFrame fishies = new JFrame();
		fishies.setSize(235, 200);
		this.fishLog = new BufferedImage(200, 1000, BufferedImage.TYPE_INT_ARGB);
		this.log2D = (Graphics2D) fishLog.createGraphics();
		
		try
		{
			main = ImageIO.read( new File( "main.png" ) );
		}
		catch (IOException e ) {}
		
		addMouseListener( new MouseAdapter()
		{
			public void mousePressed( MouseEvent event )
			{
				//NET
				if (event.getPoint().x > 242 && event.getPoint().x < 321
						&& event.getPoint().y > 321 && event.getPoint().y < 380) {
					if (isNetting)
						isNetting = false;
					else {
						isNetting = true;
						isFeeding = false;
						g2d.setColor(Color.CYAN);
						g2d.setXORMode(Color.BLACK);
					}
				}
				//FOOD
				else if (event.getPoint().x > 24 && event.getPoint().x < 137
						&& event.getPoint().y > 321 && event.getPoint().y < 380) {
					if (isNetting)
						isNetting = false;
					isFeeding = true;
					g2d.setPaintMode();
				}
				//MENU
				else if (event.getPoint().x > 14 && event.getPoint().x < 84
						&& event.getPoint().y > 9 && event.getPoint().y < 46) {
					if (isNetting)
						isNetting = false;
					JFrame menuFrame = new JFrame();
					BufferedImage menu = null;
					try
					{
						menu = ImageIO.read( new File( "menu_screen.png" ) );
					}
					catch (IOException e ) {}
					
					menuFrame.setContentPane( new JLabel( new ImageIcon( menu ) ) ) ;
					menuFrame.validate();
					menuFrame.pack();
					menuFrame.setLocationRelativeTo(null);
					menuFrame.setVisible( true ); 
					
					menuFrame.addMouseListener( new MouseAdapter() {
						public void mousePressed( MouseEvent event) {
							//System.out.println(event.getPoint());
							//SAVE
							if (event.getPoint().x > 24 && event.getPoint().x < 115
									&& event.getPoint().y > 44 && event.getPoint().y < 82) {
								
								try {
									javax.imageio.ImageIO.write(fishLog, "png", new File("fish_log_" + logNum + ".png"));
									logNum++;
								}
								catch(IOException e) {
									JOptionPane.showMessageDialog( menuFrame,
								  		          "Error saving file", "oops!", JOptionPane.ERROR_MESSAGE );
								}
								menuFrame.setVisible(false);
							}
							else if (event.getPoint().x > 24 && event.getPoint().x < 115
									&& event.getPoint().y > 97 && event.getPoint().y < 137) {
								System.exit( 0 );
							}
							else if (event.getPoint().x > 24 && event.getPoint().x < 109
									&& event.getPoint().y > 153 && event.getPoint().y < 189) {
								menuFrame.setVisible(false);
							}
						}
					});
				}
				//HINT
				else if (event.getPoint().x > 100 && event.getPoint().x < 170
						&& event.getPoint().y > 9 && event.getPoint().y < 46) {
					if (isNetting)
						isNetting = false;
					JFrame hintFrame = new JFrame();
					BufferedImage hintPage = null;
					try
					{
						hintPage = ImageIO.read( new File( "hint_page.png" ) );
					}
					catch (IOException e ) {}
					
					hintFrame.setContentPane( new JLabel( new ImageIcon( hintPage ) ) ) ;
					hintFrame.validate();
					hintFrame.pack();
					hintFrame.setLocationRelativeTo(null);
					hintFrame.setVisible( true ); 
					
				}
				//FISH
				else if (event.getPoint().x > 462 && event.getPoint().x < 575
						&& event.getPoint().y > 321 && event.getPoint().y < 380) {
					if (isNetting)
						isNetting = false;				
					
					fishies.setContentPane( new JScrollPane( new JLabel( new ImageIcon( fishLog ) ) ) );
					fishies.validate();
					fishies.setLocationRelativeTo(null);
					fishies.setVisible( true ); 
				}
				
				if(isFeeding && event.getPoint().x > 0 && event.getPoint().x < 600
						&& event.getPoint().y > 60 && event.getPoint().y < 310) {
					//g2d.setPaintMode();
					foodCoords.push(event.getPoint());
					g2d.setColor(new Color(foodColor));
					//opacity = 0.5;
					g2d.fill(new Ellipse2D.Double(event.getX() - 5, event.getY() - 5, 10, 10));
				}
				
				clearSelection( event.getPoint() );
			}
		} );
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent event)
			{
				if (isNetting) {
					g2d.setXORMode(Color.BLACK);
					updateSelection( event.getPoint() );
				}
			}
		} );
		addMouseListener( new MouseAdapter()
		{
			public void mouseReleased( MouseEvent event )
			{
				if (isNetting) {
					
					for (int i = 0; i < fishCount; i++) {
						//System.out.println("catching");
						updateNet(event.getPoint());
						if (fishes[i].getPosX() < rightX && fishes[i].getPosX() > leftX) {
							if (fishes[i].getPosY() < rightY && fishes[i].getPosY() > leftY) {
								//System.out.println("caught");
								fishes[i].setCaught(true);
							}
						}
					}
					clearSelection(event.getPoint());
				}
			}
		} );

	}
	
	private void start() {
		isMovin = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		int wait = 30;
		while(isMovin) {
			refreshIcons();
			
			tick();
			
			repaint();
			
			try {
				Thread.sleep(wait);
			}
			catch (Exception e){}
		}
	}
	
	public void tick() {
		double chance = rando.nextDouble();							//TODO Make this so that we aren't making new random every tick;
		//System.out.println("Test?");								//			- put generatior inside if statement, else chance = 1.0;
		if (chance < 0.005 && fishCount < 7) {
			int type = rando.nextInt(3);
			//System.out.println(type);
			if (type == 0) {
				int size = rando.nextInt(10) + 10;
				int steps = rando.nextInt(10) + 30;
				int startX = rando.nextInt(50) - 50;
				int startY = rando.nextInt(700) - 50;
				fishes[fishCount] = new Fish(startX, startY, type, size, steps);
				fishCount++;
			}
			else if (type == 1) {
				int size = rando.nextInt(10) + 20;
				int steps = rando.nextInt(10) + 60;
				int startX = rando.nextInt(50) - 50;
				int startY = rando.nextInt(700) - 50;
				fishes[fishCount] = new Fish(startX, startY, type, size, steps);
				fishCount++;
			}
			else if (type == 2) {
				int size = rando.nextInt(10) + 30;
				int steps = rando.nextInt(10) + 100;
				int startX = rando.nextInt(50) - 50;
				int startY = rando.nextInt(700) - 50;
				fishes[fishCount] = new Fish(startX, startY, type, size, steps);
				fishCount++;
			}
		}

		for (int i = 0; i < fishCount; ++i) {
			if (fishes[i].getFeeding()) {
				fishG.setColor(new Color(foodColor));
				fishG.fill(new Ellipse2D.Double(fishes[i].getDestX() - 5, fishes[i].getDestY() - 5, 10, 10));
			}

			fishG.setColor(new Color(0x6189c1));
			fishG.fill(new Ellipse2D.Double(fishes[i].getPosX() - 1 - (fishes[i].getFSize() / 2), fishes[i].getPosY() - 1 - (fishes[i].getFSize() / 2), fishes[i].getFSize() + 2, fishes[i].getFSize() + 2));
			fishes[i].setPos(fishes[i].getPosX() + fishes[i].getAddX(), fishes[i].getPosY() + fishes[i].getAddY());
			fishG.setColor(new Color(0x4770a8));
			fishG.fill(new Ellipse2D.Double(fishes[i].getPosX() - (fishes[i].getFSize() / 2), fishes[i].getPosY() - (fishes[i].getFSize()/2), fishes[i].getFSize(), fishes[i].getFSize()));
			refreshIcons();
			
			//check if fish moved to destination
			fishes[i].stepInc();
			if (fishes[i].getStepCount() == fishes[i].getSteps()) {
				checkFood(i);
			}
			if (fishes[i].getCaught()) {
				BufferedImage a = null;
				//FISH!!! picture
				try
				{
					a = ImageIO.read( new File( "catch.png" ) );
				}
				catch (IOException e ) {}
				fishG.drawImage(a, 200, 100, null);
				
				//fish picture
				if (fishes[i].getType() == 0) {
					try
					{
						a = ImageIO.read( new File( "fish_1.png" ) );
					}
					catch (IOException e ) {}
				}
				else if (fishes[i].getType() == 1) {
					try
					{
						a = ImageIO.read( new File( "fish_2.png" ) );
					}
					catch (IOException e ) {}
				}
				else if (fishes[i].getType() == 2) {
					try
					{
						a = ImageIO.read( new File( "fish_3.png" ) );
					}
					catch (IOException e ) {}
				}
				
				fishG.drawImage(a, 275, 200, null);
				
				//Fish log stuff
				log2D.setColor(Color.WHITE);
				log2D.fillRect(0, logY, 250, 50);
				log2D.setColor(Color.BLACK);
				log2D.drawImage(a, 0, logY, null);
				log2D.drawString("     Length: " + (f.format(fishes[i].getFSize() * 0.27)) + " inches     ", 60, textY - 10);
				log2D.drawString("     Weight: " + (f.format(fishes[i].getFSize() * 0.18 + rando.nextInt(3))) +  " ounces     ", 60, textY + 10);
				logY += 50;
				textY += 50;
				
				//Reset fish
				repaint();
				replenishFish(i);
				
				try {
					Thread.sleep(1200);
				}
				catch (Exception e){}
				
				try
				{
					a = ImageIO.read( new File( "main.png" ) );
				}
				catch (IOException e ) {}
				
				fishG.drawImage(a, 0, 0, null);

			}

			//System.out.println("painted");
		}

		
		fishG.setPaintMode();
		fishG.setColor(new Color(0x4770a8));
		//fishG.fill(new Ellipse2D.Double(xCoord, yCoord, 15, 15));
	}
	
	//------------------------------------------------------------------------
	// accessors - get points defining the area selected
	Point2D.Double getUpperLeft()
	{
		return getUpperLeft( new Point2D.Double() );
	}
	Point2D.Double getUpperLeft( Point2D.Double p )
	{
		if ( w < 0 )
			if ( h < 0 )
				p.setLocation( (x+w)/((double) MAX_X), (y+h)/((double) MAX_Y) );
			else
				p.setLocation( (x+w)/((double) MAX_X), y/((double) MAX_Y) );
		else if ( h < 0 )
			p.setLocation( x/((double) MAX_X), (y+h)/((double) MAX_Y) );
		else
			p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
		return p;
	}

	Point2D.Double getLowerRight()
	{
		return getLowerRight( new Point2D.Double() );
	}
	
	Point2D.Double getLowerRight( Point2D.Double p )
	{
		if ( w < 0 )
			if ( h < 0 )
				p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
			else
				p.setLocation( x/((double) MAX_X), (y+h)/((double) MAX_Y) );
		else if ( h < 0 )
			p.setLocation( (x+w)/((double) MAX_X), y/((double) MAX_Y) );
		else
			p.setLocation( (x+w)/((double) MAX_X), (y+h)/((double) MAX_Y) );
		return p;
	}
	//------------------------------------------------------------------------
	// change background image
	public void setImage( BufferedImage src )
	{
		g2d.setPaintMode();
		g2d.drawImage( src,
				0, 0, MAX_X, MAX_Y,
				0, 0, (src.getWidth() - 1), (src.getHeight() - 1),
				OUTLINE_COLOR, null );
		g2d.setXORMode( OUTLINE_COLOR );
		x = -1;
		y = -1;
		w = 0;
		h = 0;
		repaint();
	}
	//------------------------------------------------------------------------
	// behaviors
	
	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		g.drawImage( image, 0, 0, null );
	}
	private void clearSelection( Point p )
	{
		g2d.setXORMode(Color.BLACK);
		// erase old selection
		drawSelection();
		// begin new selection
		x = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		y = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = 0;
		h = 0;
		drawSelection();
		if (isFeeding)
			g2d.setPaintMode();
	}
	
	private void updateSelection( Point p )
	{
		// erase old selection
		drawSelection();

		// modify current selection
		int px = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		int py = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = px - x;
		h = py - y;
		drawSelection();
	}
	
	private void drawSelection()
	{
		if ( w < 0 )
			if ( h < 0 ) {
				g2d.drawRect( (x+w), (y+h), -w, -h );
			}
			else {
				g2d.drawRect( (x+w), y, -w, h );
			}
		else if ( h < 0 ) {
			g2d.drawRect( x, (y+h), w, -h );
		}
		else {
			g2d.drawRect( x, y, w, h );
		}
		repaint();
	}
	
	private void updateNet( Point p )
	{
		// erase old selection
		// modify current selection
		int px = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		int py = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = px - x;
		h = py - y;
		checkFish();
	}
	
	private void checkFish() {
		if ( w < 0 )
			if ( h < 0 ) {
				leftX = x + w;
				leftY = y + h;
				rightX = x;
				rightY = y;
			}
			else {
				leftX = x + w;
				leftY = y;
				rightX = x;
				rightY = y + h;
			}
		else if ( h < 0 ) {
			leftX = x;
			leftY = y + h;
			rightX = x + w;
			rightY = y;
		}
		else {
			leftX = x;
			leftY = y;
			rightX = x + w;
			rightY = y + h;
		}
	}
	
	private void replenishFish(int i) {
		fishes[i] = null;
		int type = rando.nextInt(3);
		//System.out.println(type);
		if (type == 0) {
			int size = rando.nextInt(10) + 10;
			int steps = rando.nextInt(10) + 30;
			int startX = rando.nextInt(50) - 50;
			int startY = rando.nextInt(700) - 50;
			fishes[i] = new Fish(startX, startY, type, size, steps);
		}
		else if (type == 1) {
			int size = rando.nextInt(10) + 20;
			int steps = rando.nextInt(10) + 60;
			int startX = rando.nextInt(50) - 50;
			int startY = rando.nextInt(700) - 50;
			fishes[i] = new Fish(startX, startY, type, size, steps);
		}
		else if (type == 2) {
			int size = rando.nextInt(10) + 30;
			int steps = rando.nextInt(10) + 100;
			int startX = rando.nextInt(50) - 50;
			int startY = rando.nextInt(700) - 50;
			fishes[i] = new Fish(startX, startY, type, size, steps);
		}
	}
	
	private void checkFood(int i) {
		if (fishes[i].getFeeding()) {
			//System.out.println("Nom");
			//fishG.setColor(new Color(0x6189c1));
			//fishG.fillRect((int)fishes[i].getDestX() - 5, (int)fishes[i].getDestY() - 5, 11, 11);
			fishes[i].setFeeding(false);
		}
		else if (foodCoords.size() > 0) {
			Point point = foodCoords.pop();
			fishes[i].setDest(point.x, point.y);
			//System.out.println(i + " getting food at " + point.x + " and " + point.y);
			fishes[i].setFeeding(true);
		}
		else {
			fishes[i].setDest(rando.nextInt(700) - 100, rando.nextInt(500) - 100);
		}
		
		fishes[i].resetSteps();
	}
	
	private void importMenuIcons() {
		try
		{
			this.menu = ImageIO.read( new File( "menu.png" ) );
		}
		catch (IOException e ) {}
		try
		{
			this.hint = ImageIO.read( new File( "hint.png" ) );
		}
		catch (IOException e ) {}
		try
		{
			this.food = ImageIO.read( new File( "food.png" ) );
		}
		catch (IOException e ) {}
		try
		{
			this.net = ImageIO.read( new File( "net.png" ) );
		}
		catch (IOException e ) {}
		try
		{
			this.fish = ImageIO.read( new File( "fish.png" ) );
		}
		catch (IOException e ) {}
	}
	
	private void refreshIcons() {
		fishG.drawImage(this.menu, 14, 9, null);
		fishG.drawImage(this.hint, 100, 9, null);
		fishG.drawImage(this.food, 24, 321, null);
		fishG.drawImage(this.net, 242, 321, null);
		fishG.drawImage(this.fish, 462, 321, null);
	}
}

