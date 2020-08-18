import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SequencialTerrainClassifier 
{
    private float[][] terrainData;
    private int basins;
    private ArrayList<String> coordinates;

    public SequencialTerrainClassifier(float[][] data) 
    {
        terrainData = data;
        basins = 0;
        coordinates = new ArrayList<String>(4);
    }

    public void scanTerrain() throws IOException 
    {
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

                if (counter == 6) 
                {
                    basins++;
                    coordinates.add(i + " " + j);
                }
            }
        }

        printFile();
    }

    private void printFile() throws IOException
    {
        File file = new File("output.txt");
        file.createNewFile();
        
        FileWriter writer = new FileWriter("output.txt");
        String output = basins + "\n";
        for (String positions : coordinates) 
        {
            output += positions + "\n";    
        }
        writer.write(output);
        writer.close();
    }

}