import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SequencialTerrainClassifier 
{
    float[][] terrainData; //stores terrain data
    int basins;
    ArrayList<String> coordinates; //stores the coordinates of the basins


    public SequencialTerrainClassifier(float[][] data) 
    {
        terrainData = data;
        basins = 0;
        coordinates = new ArrayList<String>(4);
    }

    public void scanTerrain() throws IOException 
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
      >Essentially I'm traversing through the entire 2D terrainData array and creating these blocks. 
      >Then I'm taking the anchor point and comparing it with the values surrounding it. 
      
    */
        
        float compare = 0;
        float[] values = new float[8];
        int counter = 0;

        for (int i = 1; i + 1 < terrainData.length; i++) // shifting y value of terrainData
        {
            counter = 0;

            for (int j = 1; j + 1 < terrainData[0].length; j++) // shifting x value of terrainData
            {
                compare = terrainData[i][j];

                values[0] = terrainData[i][j - 1];
                values[1] = terrainData[i][j + 1];
                values[2] = terrainData[i - 1][j];
                values[3] = terrainData[i - 1][j - 1];
                values[4] = terrainData[i - 1][j + 1];
                values[5] = terrainData[i + 1][j];
                values[6] = terrainData[i + 1][j + 1];
                values[7] = terrainData[i + 1][j - 1];

                for (int k = 0; k < values.length; k++) 
                {
                    if (values[k] - compare >= 0.1) 
                    {
                        counter++;
                    }
                }

                if (counter == 8) 
                {
                    basins++;
                    coordinates.add(i + " " + j);
                }
            }
        }
    }

    public void printFile(String fileName) throws IOException
    {
        String file = "";
        if(fileName.contains("small"))
        {
            file = "small_out(sequencial).txt";
        }
        else if(fileName.contains("med"))
        {
            file = "med_out(sequencial).txt";
        }
        else if(fileName.contains("large"))
        {
            file = "large_out(sequencial).txt";
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