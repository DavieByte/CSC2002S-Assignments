import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class TerrainClassifierHub 
{
    
    private static void tick() 
    {
        startTime = System.currentTimeMillis();
    }

    private static float tock() 
    {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

    //writes computational times for the various input files for sequencial algorithm
    private static void sequencialTimeWrite(String inputFileName, float time) throws IOException 
    {
        BufferedWriter out;
        
        if(inputFileName.contains("small"))
        {
            out = new BufferedWriter(new FileWriter("sequencial_small_times.csv", true));
            out.write("," + time);
            out.close();
        }
        else if(inputFileName.contains("med"))
        {
            out = new BufferedWriter(new FileWriter("sequencial_med_times.csv", true));
            out.write("," + time);
            out.close();
        }
        else if(inputFileName.contains("large"))
        {
            out = new BufferedWriter(new FileWriter("sequencial_large_times.csv", true));
            out.write("," + time);
            out.close();
        }
    }

    //writes computational times for the various input files for fork join threads
    private static void fjTimeWrite(String inputFileName, float time) throws IOException 
    {
        BufferedWriter out;
        
        if(inputFileName.contains("small"))
        {
            out = new BufferedWriter(new FileWriter("fj_small_times.csv", true));
            out.write("," + time);
            out.close();
        }
        else if(inputFileName.contains("med"))
        {
            out = new BufferedWriter(new FileWriter("fj_med_times.csv", true));
            out.write("," + time);
            out.close();
        }
        else if(inputFileName.contains("large"))
        {
            out = new BufferedWriter(new FileWriter("fj_large_times.csv", true));
            out.write("," + time);
            out.close();
        }
    }

    static final ForkJoinPool fjPool = new ForkJoinPool();
    static float startTime = 0.0f;

    public static void main(final String[] args) throws IOException 
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

        String outputFileName = fileName.substring(0, fileName.length() - 7);

        SequencialTerrainClassifier stc = new SequencialTerrainClassifier(terrain, outputFileName);
        FJTerrainClassifier fjtc = new FJTerrainClassifier(terrain, 1, 1, terrain.length, outputFileName);

        //running sequencial algorithm
        float time = 0.0f;
        tick();
        stc.scanTerrain();
        time = tock();
        stc.printFile();
        sequencialTimeWrite(fileName, time);

        //running fork join threading algorithm
        time = 0.0f;
        tick();
        fjtc.invoke();
        time = tock();
        fjtc.writeToFile();
        fjTimeWrite(fileName, time);
        
    }

}