//import java.awt.event.*;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.*; 
import java.awt.image.*;
import javax.swing.*;

public class FlowPanel extends JPanel implements Runnable
{
	Terrain land;
	waterTerrain water;
	CopyOnWriteArrayList<Integer> waterLocations;
	Iterator<Integer> iterator;
	BufferedImage waterImage;

	
	FlowPanel(Terrain terrain, waterTerrain aqua) 
	{
		land = terrain;
		water = aqua;
		waterLocations = aqua.getWaterLocations();
		iterator = waterLocations.iterator();
		waterImage = new BufferedImage(land.dimx, land.dimy, BufferedImage.TYPE_INT_ARGB);
	}
		
	// responsible for painting the terrain and water
	// as images
	@Override
	protected void paintComponent(Graphics g) 
	{	  
		super.paintComponent(g);
		
		// draw the landscape in greyscale as an image
		if (land.getImage() != null)
		{
			g.drawImage(land.getImage(), 0, 0, null);
		}

		if(waterImage != null)
		{
			g.drawImage(waterImage, 0, 0, null);
		}
	}

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
}