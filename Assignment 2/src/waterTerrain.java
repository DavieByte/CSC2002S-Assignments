import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
//import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class waterTerrain 
{
    float [][] height; // regular grid of height values
    int dimx, dimy; // data dimensionsnew String(x + "," + y)
    Terrain ground;
    ArrayList <String> waterLocations = new ArrayList<String>();
    ListIterator<String> iterator = waterLocations.listIterator();
    BufferedImage waterImage;
    private volatile boolean noPauseRequested;
    private Thread internalThread;

    
    public waterTerrain(Terrain ground)
    {
        this.ground = ground;
        dimx = ground.getDimX();
        dimy = ground.getDimY();
        height = new float[dimx][dimy];
        
        noPauseRequested = true;
        Runnable r = new Runnable()
        {
            public void run()
            {
                try
                {
                    flow();
                }
                catch(Exception x)
                {
                    x.printStackTrace();
                }
            }   
        };

        internalThread = new Thread(r, "Flow");

       
    }

    public synchronized void reset()
    {
        height = new float[dimx][dimy];
        waterLocations.clear();
    }

    public float getHeight(int x, int y)
	{
		return height[x][y];
	}

	public synchronized void setHeight(int x, int y, int add)
	{
        if(x != 0 || y != 0 || x != dimx || y != dimy)
        {
            height[x][y] += add*0.1;
            iterator.add(x + "," + y);
        }
        else
        {
            height[x][y] = 0;
        }
        if(height[x][y] < 0)
        {
            height[x][y] = 0;
            iterator.remove();
        }
    }

    public synchronized void deriveWaterImage()
	{
        Iterator <String> iterator = waterLocations.iterator();
		Color color = Color.blue;
        int x,y;
        String location = "";
        waterImage = new BufferedImage(ground.dimx, ground.dimy, BufferedImage.TYPE_INT_ARGB);
        Scanner lineScanner;
		
		while(iterator.hasNext())
		{
            location = iterator.next();
            lineScanner = new Scanner(location);
            lineScanner.useDelimiter(",");
			x = Integer.parseInt(lineScanner.next());
            y = Integer.parseInt(lineScanner.next());
			waterImage.setRGB(x, y, color.getRGB());
		}
    }
    
    public synchronized BufferedImage getImage() 
	{
		  return waterImage;
	}

    /*
    public CopyOnWriteArrayList<Integer> getWaterLocations()
    {
        return waterLocations;
    }
    */

    public void flow()
    {
        // _ _ _ ____________ 
        // y -1  |x-1| x |x+1|
        // _ _ _ |___|___|___|
        // y     |x-1| x |x+1|
        // _ _ _ |___|_*_|___|
        // y+1   |x-1| x |x+1|
        // _ _ _ |___|___|___|
        //The 'centre' varible is the corresponding height value of block with the * in it
        iterator = waterLocations.listIterator();
        int x,y;
        String location = "";
        Scanner lineScanner = new Scanner(location);

        Runnable updateImage = new Runnable() 
        {
            public void run() 
            {
                deriveWaterImage();
            }
        };

        while(iterator.hasNext() &&  noPauseRequested == true) 
        {
            location = iterator.next();
            lineScanner = new Scanner(location);
            lineScanner.useDelimiter(",");
            x = Integer.parseInt(lineScanner.next());
            y = Integer.parseInt(lineScanner.next());

            float centre = height[x][y] + ground.getHeight(x, y);
            int waterStacks = (int)(height[x][y] / 0.1);
            int isBasin = 0;

            //comparing centre with its neighboring height values
            if(centre > 0.0f)
            {
                for (int i = -1; i < 2; i++) 
                {
                    for (int j = -1; j < 2; j++) 
                    {
                        if(i != x && j != y)
                        {
                            if(centre - (height[x + i][y + j] + ground.getHeight(x + i, y + j)) > 0.0001)
                            {   
                                setHeight(x + i, y + j, waterStacks - 1);
                            }
                            else
                            {
                                isBasin++;
                            }
                        }
                    }
                }

                if(isBasin != 8)
                {
                    setHeight(x, y, -1);
                }
            }
            else if(centre == 0.0f)
            {
                iterator.remove();
            }

        }

        lineScanner.close();
        try 
        {
            SwingUtilities.invokeAndWait(updateImage);
        } 
        catch (InvocationTargetException e) 
        {
            e.printStackTrace();
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
    }

    public void pauseRequest() 
    {
        noPauseRequested = false;
        internalThread.interrupt();
    }
    

}