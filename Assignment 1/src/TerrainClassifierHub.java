import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TerrainClassifierHub {
    public static void main(final String[] args) throws FileNotFoundException 
    {
        //getting the file name from the user and setting up the scanner to read from the file
        String fileName = args[0];
        File file = new File(fileName);
        Scanner darkly = new Scanner(file);
        
        //initializing the size of the array of the terrain data
        String line = darkly.nextLine();
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter(" ");
        int x = lineScanner.nextInt();
        int y = lineScanner.nextInt();
        float [][] terrain = new float[y][x];
        lineScanner.close();
        
        //populating the terrain data into the array
        line = darkly.nextLine();
        lineScanner = new Scanner(line);
        lineScanner.useDelimiter(" ");

        for (int i = 0; i < y; i++) 
        {
            for (int j = 0; j < x; j++) 
            {
                terrain[i][j] = lineScanner.nextFloat();
            }
        }

        darkly.close();
        lineScanner.close();

    }

}