//import java.awt.event.*;
//import java.util.Iterator;
//import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.*; 
import java.awt.image.*;
import javax.swing.*;

public class FlowPanel extends JPanel implements Runnable
{
	private static final long serialVersionUID = 1L;
	Terrain land;
	waterTerrain water;
	//CopyOnWriteArrayList<Integer> waterLocations;
	//Iterator<Integer> iterator;
	BufferedImage waterImage;

	
	FlowPanel(Terrain terrain, waterTerrain aqua) 
	{
		land = terrain;
		water = aqua;
		//waterLocations = aqua.getWaterLocations();
		//iterator = waterLocations.iterator();
		//waterImage = new BufferedImage(land.dimx, land.dimy, BufferedImage.TYPE_INT_ARGB);
	}
		
	// responsible for painting the terrain and water
	// as images
	@Override
	public void paintComponent(Graphics g) 
	{	  
		super.paintComponent(g);
		
		// draw the landscape in greyscale as an image
		if (land.getImage() != null)
		{
			g.drawImage(land.getImage(), 0, 0, null);
		}

		if(water.getImage() != null)
		{
			g.drawImage(water.getImage(), 0, 0, null);
		}
	}
	
	public void run()
	{
		while(true)
		{
			try 
			{
				Thread.sleep(1000);
				repaint();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}

	/*
	public void deriveWaterImage()
	{
		waterLocations = water.getWaterLocations();
		iterator = waterLocations.iterator();
		Color color = Color.blue;
		int x,y, location;
		
		while(iterator.hasNext())
		{
			location = iterator.next();
			x = (int) location / land.dimy;
			y = location % land.dimy;
			waterImage.setRGB(x, y, color.getRGB());
		}

		
	}
	
	public void run()
	{
		deriveWaterImage();
		repaint();
	}
	*/
}