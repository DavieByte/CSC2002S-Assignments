import java.util.concurrent.RecursiveTask;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

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
    CopyOnWriteArrayList<String> coordinates; // stores the anchor value provided it passes the test
    int basins = 0;

    public FJTerrainClassifier(float[][] terrainData, int anchorX, int anchorY, int cutoff) 
    {
        data = terrainData;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        SEQUENTIAL_CUTOFF = cutoff;
        coordinates = new CopyOnWriteArrayList<String>();

        // filling in the terrain values of the block in the diagram above
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
        if (SEQUENTIAL_CUTOFF - anchorY == 1 && SEQUENTIAL_CUTOFF - anchorX == 1) 
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

            if (counter == 6) 
            {
                basins++;
                coordinates.add(anchorX + " " + anchorY);
            }

            return basins; // say return 1 in case this causes more basins in the output
        }

        FJTerrainClassifier down = new FJTerrainClassifier(data, anchorX, anchorY + 1, SEQUENTIAL_CUTOFF);
        FJTerrainClassifier right;

        if (SEQUENTIAL_CUTOFF - anchorY != 1) 
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
            down = new FJTerrainClassifier(data, anchorX, anchorY + 1, SEQUENTIAL_CUTOFF);
            down.fork();
        } 
        else 
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
            right = new FJTerrainClassifier(data, anchorX + 1, 1, SEQUENTIAL_CUTOFF);
            basins += right.compute();
        }

        basins += down.join();

        return basins;
    }

    public void writeToFile(String fileName) throws IOException
    {
        String file = "";
        if(fileName.contains("small"))
        {
            file = "small_out(fj).txt";
        }
        else if(fileName.contains("med"))
        {
            file = "med_out(fj).txt";
        }
        else if(fileName.contains("large"))
        {
            file = "large_out(fj).txt";
        }

        PrintWriter writer = new PrintWriter(file);
        String output = basins + "\n";
        for (String positions : coordinates) 
        {
            output += positions + "\n";    
        }
        writer.print(output);
        writer.close();
    }

}