import javax.swing.event.MouseInputListener;
import org.w3c.dom.events.MouseEvent;
import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*;

public class waterTerrain
{
    float [][] height; // regular grid of height values
    int dimx, dimy; // data dimensions
    Terrain ground;
    
    public waterTerrain(Terrain ground)
    {
        this.ground = ground;
        dimx = ground.getDimX();
        dimy = ground.getDimY();
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

}