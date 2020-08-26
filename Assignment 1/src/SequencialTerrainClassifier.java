import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SequencialTerrainClassifier 
{
    float[][] terrainData; //stores terrain data
    int basins;
    ArrayList<String> coordinates; //stores the coordinates of the basins
    String outputFileName = "";

    public SequencialTerrainClassifier(float[][] data, String outputFileName) 
    {
        terrainData = data;
        basins = 0;
        coordinates = new ArrayList<String>(4);
        this.outputFileName = outputFileName;
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

    public void printFile() throws IOException
    {
        String fileName = outputFileName + "_out(sequencial).txt";
        File file = new File(fileName);
        file.createNewFile();
        
        FileWriter writer = new FileWriter(fileName);
        String output = basins + "\n";
        for (String positions : coordinates) 
        {
            output += positions + "\n";    
        }
        writer.write(output);
        writer.close();
    }

}