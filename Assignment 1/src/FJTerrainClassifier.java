import java.util.concurrent.RecursiveTask;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;

public class FJTerrainClassifier extends RecursiveTask<Integer>
{
    /*
      _ _ _ ____________ 
      y -1  |x-1| x |x+1| 
      _ _ _ |___|___|___| 
      y     |x-1| x |x+1| 
      _ _ _ |___|_*_|___| 
      y+1   |x-1| x |x+1| 
      _ _ _ |___|___|___|
      
      >The anchor point is where the '*' symbol is. 
      >Essentially I'm traversing
      through the entire 2D terrainData array and creating these blocks. 
      >Then I'm taking the anchor point and comparing it with the values surrounding it. 
      >The idea with the thread implementation is making many of these blocks by
      shifting the anchor points across the entire 2D terrainData array and then
      tallying up the amount of basins found.
      
     */

    float[][] data; // holds file data
    int anchorX = 1;
    int anchorY = 1;
    int SEQUENTIAL_CUTOFF; // the width of the square array of terrain data
    float[] values = new float[8]; // terrain values of the block mentioned in the diagram above
    float compare; // terrain value of the anchor point
    int basins = 0;
    String file = "";

    public FJTerrainClassifier(float[][] terrainData, int anchorX, int anchorY, int cutoff, String fileName) 
    {
        data = terrainData;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        SEQUENTIAL_CUTOFF = cutoff;
        file = fileName;
        
        if(SEQUENTIAL_CUTOFF - anchorX <= 2 && SEQUENTIAL_CUTOFF - anchorY <= 2)
        {
            fillBlock();
        }
    }

    // filling in the terrain values of the block in the diagram above
    public void fillBlock()
    {
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

    @Override
    protected Integer compute()
    {
        FJTerrainClassifier down = new FJTerrainClassifier(data, anchorX, anchorY + 1, SEQUENTIAL_CUTOFF, file);
        FJTerrainClassifier right = new FJTerrainClassifier(data, anchorX + 1, 1, SEQUENTIAL_CUTOFF, file);

        if (SEQUENTIAL_CUTOFF - anchorY != 2 && SEQUENTIAL_CUTOFF - anchorX != 2) 
        {
            // testing to see if the values surrounding the anchor point to varify that it
            // is a basin or not
            int counter = 0;
            for (int i = 0; i < values.length; i++) 
            {
                if (values[i] - compare >= 0.1) 
                {
                    counter++;
                }
            }

            if (counter == 8) 
            {
                try
                {
                    appendToFile(file, anchorX + " " + anchorY);
                }
                catch(IOException e)
                {
                    System.out.println("exception occurred" + e); 
                }
                return 1;
            }
        }


        if (SEQUENTIAL_CUTOFF - anchorY != 2) 
        {
        /*
         _ _ _ ____________ 
         y -1  |x-1| x |x+1| 
         _ _ _ |___|___|___| 
         y     |x-1| x |x+1| 
         _ _ _ |___|_*_|___| 
         y+1   |x-1| x |x+1| 
         _ _ _ |___|___|___|
               | . | . | . |
               | . | . | . |
               | . | . | . |
         
        >This if statement causes these blocks to shift downwards recursively until it hits
        the bottom of the 2D array
        */
            down.fork();
        } 

        else if(SEQUENTIAL_CUTOFF - anchorY == 2 && SEQUENTIAL_CUTOFF - anchorX > 2)
        {
        /*
         _ _ _ ____________ ___
         y -1  |x-1| x |x+1| ...
         _ _ _ |___|___|___|___ 
         y     |x-1| x |x+1| ...
         _ _ _ |___|_*_|___|___ 
         y+1   |x-1| x |x+1| ...
         _ _ _ |___|___|___|___
               
         
        >Once the down shifting blocks hit the bottom, the anchorY shift back up to 1
        and the anchorX shifts one unit to the right and starts the process all over again 
        */
            right.compute();
        }

        basins+= right.join();
        basins += down.join();
        return basins;
    }

    public void appendToFile(String fileName, String input) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(file, true)); 
        out.write(input + "\n"); 
        out.close(); 
    }

}