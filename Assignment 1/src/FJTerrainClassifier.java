import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool; 
import java.util.Iterator; 
import java.util.concurrent.CopyOnWriteArrayList; 

public class FJTerrainClassifier extends RecursiveTask<Integer>
{
    /*
    _ _ _  ____________
    y -1  |x-1| x |x+1|
    _ _ _ |___|___|___|
    y     |x-1| x |x+1|
    _ _ _ |___|_*_|___|
    y+1   |x-1| x |x+1|
    _ _ _ |___|___|___|
    
    >The anchor point is where the '*' symbol is.
    >Essentially I'm traversing through the entire 2D terrainData array and creating these blocks.
    >Then I'm taking the anchor point and comparing it with the values surrounding it.
    >The idea with the thread implementation is making many of these blocks by 
    shifting the anchor points across the entire 2D terrainData array and then tallying up
    the amount of basins found.
    
    */

    float [][] data; //holds file data
    int anchorX;
    int anchorY;
    final int SEQUENTIAL_CUTOFF; //the width of the square array of terrain data
    float[] values = new float[8]; //terrain values of the block mentioned in the diagram above
    float compare; //terrain value of the anchor point
    CopyOnWriteArrayList<String> coordinates; //stores the anchor value provided it passes the test
    int basins = 0;

    public FJTerrainClassifier(float [][] terrainData, int anchorX, int anchorY, int cutoff)
    {
        data = terrainData;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        SEQUENTIAL_CUTOFF = cutoff;
        coordinates = new CopyOnWriteArrayList<String>(); 

        //filling in the terrain values of the block in the diagram above
        compare = data[anchorY][anchorX];
        values[0] = data[anchorY][anchorX - 1];
        values[1] = data[anchorY][anchorX + 1];
        values[2] = data[anchorY - 1][anchorX];
        values[3] = data[anchorY - 1][anchorX - 1];
        values[4] = data[anchorY - 1][anchorX + 1];
        values[5] = data[anchorY + 1][anchorX];
        values[6] = data[anchorY + 1][anchorX + 1];
        values[7] = data[anchorY + 1][anchorX - 1];
    }

    protected Integer compute()
    {
        if(SEQUENTIAL_CUTOFF - anchorY == 1 && SEQUENTIAL_CUTOFF - anchorX == 1)
        {
            //testing to see if the values surrounding the anchor point to varify that it is a basin or not
            int counter = 0;
            for (int i = 0; i < values.length; i++) 
            {
                if (values[i] - compare >= 0.1) 
                    {
                        counter++;
                    }
            }

            if (counter == 6) 
            {
                basins++;
                coordinates.add(anchorX + " " + anchorY);
            }

            return basins; //say return 1 in case this causes more basins in the output
        }
        else
        {
            if(SEQUENTIAL_CUTOFF - anchorX > 1)
            {
                FJTerrainClassifier left = new FJTerrainClassifier(data, anchorX + 1, anchorY, SEQUENTIAL_CUTOFF);
            }
        }

    }

}