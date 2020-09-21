//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
//import java.awt.image.*;
//import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class waterTerrain implements Runnable
{
    float [][] height; // regular grid of height values
    int dimx, dimy; // data dimensions
    Terrain ground;
    CopyOnWriteArrayList <Integer> waterLocations = new CopyOnWriteArrayList<Integer>();
    
    public waterTerrain(Terrain ground)
    {
        this.ground = ground;
        dimx = ground.getDimX();
        dimy = ground.getDimY();
        height = new float[dimx][dimy];
        
    }

    public void reset()
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
            waterLocations.add(x*y);
        }
        else
        {
            height[x][y] = 0;
        }
        if(height[x][y] < 0)
        {
            height[x][y] = 0;
        }
    }

    public CopyOnWriteArrayList<Integer> getWaterLocations()
    {
        return waterLocations;
    }

    public void run()
    {
        // _ _ _ ____________ 
        // y -1  |x-1| x |x+1|
        // _ _ _ |___|___|___|
        // y     |x-1| x |x+1|
        // _ _ _ |___|_*_|___|
        // y+1   |x-1| x |x+1|
        // _ _ _ |___|___|___|
        //The 'centre' varible is the corresponding height value of block with the * in it
        Iterator<Integer> iterator = waterLocations.iterator();
        int location, x, y;
        while(iterator.hasNext())
        {
            location = iterator.next();
            x = (int) location / ground.dimy;
			y = location % ground.dimy;

            float centre = height[x][y] + ground.getHeight(x, y);
            int waterStacks = (int)(height[x][y] / 0.1);
            int isBasin = 0;

            //comparing centre with its neighboring height values
            for (int i = -1; i < 2; i++) 
            {
                for (int j = -1; j < 2; j++) 
                {
                    if(i != x && j != y)
                    {
                        if(centre - (height[i][j] + ground.getHeight(i, j)) > 0.0001)
                        {   
                            setHeight(i, j, waterStacks - 1);
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

    }

}