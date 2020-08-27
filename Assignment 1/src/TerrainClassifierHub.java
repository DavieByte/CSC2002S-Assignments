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

    private static float[][] terrainData(File terrainFile) throws IOException
    {
        File file = terrainFile;
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

        return terrain;
    }

    private static void runSequencialTest(SequencialTerrainClassifier stc, float[][] data, String fileName) throws IOException
    {
        float time = 0.0f;
        for (int i = 0; i < 10; i++) 
        {
            stc = new SequencialTerrainClassifier(data);
            time = 0.0f;
            tick();
            stc.scanTerrain();
            time = tock();
            stc.printFile(fileName);
            fjTimeWrite(fileName, time);
        }
    }

    private static void runFJTest(FJTerrainClassifier fjtc, float[][] data, String fileName) throws IOException
    {
        float time = 0.0f;
        for (int i = 0; i < 10; i++) 
        {
            fjtc = new FJTerrainClassifier(data, 1, 1, data.length);
            time = 0.0f;
            tick();
            fjtc.invoke();
            time = tock();
            fjtc.writeToFile(fileName);
            sequencialTimeWrite(fileName, time);
        }
    }

    static final ForkJoinPool fjPool = new ForkJoinPool();
    static float startTime = 0.0f;

    public static void main(String[] args) throws IOException 
    {
        File smallFile = new File("small_in.txt");
        File medFile = new File("med_in.txt");
        File largeFile = new File("large_in.txt");

        float[][] small = terrainData(smallFile);
        float[][] med = terrainData(medFile);
        float[][] large = terrainData(largeFile);

        SequencialTerrainClassifier stc = new SequencialTerrainClassifier(small);
        FJTerrainClassifier fjtc = new FJTerrainClassifier(small, 1, 1, small.length);

        runSequencialTest(stc, small, "small");
        runSequencialTest(stc, med, "med");
        runSequencialTest(stc, large, "large");

        runFJTest(fjtc, small, "small");
        runFJTest(fjtc, med, "med");
        runFJTest(fjtc, large, "large");

    }

}