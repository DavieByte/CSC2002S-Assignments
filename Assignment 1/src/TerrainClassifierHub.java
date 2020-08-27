import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
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
        String temp = "";

        for (int i = 0; i < y; i++) 
        {
            for (int j = 0; j < x; j++) 
            {
                temp = lineScanner.next();
                terrain[i][j] = Float.parseFloat(temp);
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
            sequencialTimeWrite(fileName, time);
        }
    }

    private static void runFJTest(ForkJoinPool pool,FJTerrainClassifier fjtc, float[][] data, String fileName) throws IOException
    {
        float time = 0.0f;
        int basins = 0;
        RandomAccessFile firstLine;
        FileWriter temp;
        PrintWriter wipe;

        for (int i = 0; i < 10; i++) 
        {
            temp = new FileWriter(fileName, false); 
            wipe = new PrintWriter(temp, false);
            temp.flush();
            wipe.close();
            temp.close();
            
            basins = 0;
            fjtc = new FJTerrainClassifier(data, 1, 1, data.length,fileName);
            time = 0.0f;
            tick();
            basins = pool.invoke(fjtc);
            time = tock();
            fjTimeWrite(fileName, time);

            firstLine = new RandomAccessFile(new File(fileName), "rw");
            firstLine.seek(0); // to the beginning
            firstLine.write((basins + "\n").getBytes());
            firstLine.close();
        }
    }

    static float startTime = 0.0f;

    public static void main(String[] args) throws IOException 
    {
        File smallFile = new File("small_in.txt");
        File medFile = new File("med_in.txt");
        File largeFile = new File("large_in.txt");

        float[][] small = terrainData(smallFile);
        float[][] med = terrainData(medFile);
        float[][] large = terrainData(largeFile);

        ForkJoinPool pool = new ForkJoinPool();

        SequencialTerrainClassifier stc = new SequencialTerrainClassifier(small);
        FJTerrainClassifier fjtc = new FJTerrainClassifier(small, 1, 1, small.length,"small");

        runSequencialTest(stc, small, "small");
        runSequencialTest(stc, med, "med");
        runSequencialTest(stc, large, "large");

        runFJTest(pool, fjtc, small, "small_out(fj).txt");
        runFJTest(pool, fjtc, med, "med_out(fj).txt");
        runFJTest(pool, fjtc, large, "large_out(fj).txt");

        pool.shutdown();
    }

}