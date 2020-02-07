import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;


class FishPondX
{
	//private Timer timer;
	
	public static void main( String[] args )
	{
		SwingUtilities.invokeLater( new Runnable() {
			public void run()
			{
				JFrame titleFrame = new JFrame();
				titleFrame.setTitle("FISH POND X");
				titleFrame.setSize(400,300);
				titleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				BufferedImage titleImage = null;
				try
				{
					titleImage = ImageIO.read( new File( "title.png" ) );
				}
				catch (IOException e ) {}
				
				BufferedImage confirmed = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
				Graphics g = confirmed.getGraphics();
				g.drawImage(titleImage, 0, 0, null);
				
				titleFrame.setContentPane( new JLabel( new ImageIcon( confirmed ) ) ) ;
				
				titleFrame.pack();
				titleFrame.setLocationRelativeTo(null);
				titleFrame.setVisible(true);
				titleFrame.addMouseListener( new MouseAdapter() {
					public void mousePressed( MouseEvent event ) {
						if (event.getPoint().x > 150 && event.getPoint().x < 250
								&& event.getPoint().y > 280 && event.getPoint().y < 320) {
							titleFrame.setVisible(false);
							createAndShowGUI();
						}
					}
				});
			}
		} );
	}
	public static void createAndShowGUI()
	{
		JFrame frame = new JFrame();
		frame.setTitle( "FISH POND X" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		BufferedImage a = null;
		try
		{
			a = ImageIO.read( new File( "main.png" ) );
		}
		catch (IOException e ) {}		
		
		BufferedImage c = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
		Graphics g = c.getGraphics();
		g.drawImage(a,0,0,null);
		//g.drawImage(b,0,0,null);
		final FishPondGame panel = new FishPondGame( c );
		frame.getContentPane().add( panel, BorderLayout.CENTER );

		
		frame.pack();
		//g.drawImage(b, 50, 50, null);
		frame.setLocationRelativeTo(null);
		frame.setVisible( true );
	}
}