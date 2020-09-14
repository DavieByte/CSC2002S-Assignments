import javax.swing.event.MouseInputListener;
import org.w3c.dom.events.MouseEvent;
import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*;

public class waterTerrain implements MouseListener
{
    float [][] height; // regular grid of height values
    int dimx, dimy; // data dimensions
    Terrain ground;
    
    public waterTerrain(Terrain ground)
    {
        this.ground = ground;
        dimx = ground.getDimX;
        dimy = ground.getDimY;
        height = new float[dimx][dimy];
    }

    float getHeight(int x, int y)
	{
		return height[x][y];
	}

	void setHeight(int x, int y, int add)
	{
        if(x != 0 || y != 0 || x != dimx || y != dimy)
        {
            height[x][y] += add*0.1;
        }
	}

    public void mousePressed(MouseEvent m)
    {
        int x = Math.round(m.getX());
        int y = Math.round(m.getY());

        /*
         _ _ _ ____________ 
         y -1  |x-1| x |x+1|
         _ _ _ |___|___|___|
         y     |x-1| x |x+1|
         _ _ _ |___|_*_|___|
         y+1   |x-1| x |x+1|
         _ _ _ |___|___|___|

         A user click should add 3 units of water to the terrain grid in this manner, 
         then proceed to spread to lower elevations.
               
        */

        int add = 3;

        setHeight(x, y, add);
        setHeight(x-1, y, add);
        setHeight(x+1, y, add);

        setHeight(x, y-1, add);
        setHeight(x-1, y-1, add);
        setHeight(x+1, y-1, add);
        
        setHeight(x, y+1, add);
        setHeight(x-1, y+1, add);
        setHeight(x+1, y+1, add);
        
    }
}