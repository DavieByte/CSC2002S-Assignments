import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
//import java.awt.*;  
import java.awt.event.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

public class Flow extends JFrame 
{
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;
	static Terrain landData;
	static waterTerrain waterData;
	//static ExecutorService pool;

	// start timer
	private static void tick() 
	{
		startTime = System.currentTimeMillis();
	}

	// stop timer, return time elapsed in seconds
	private static float tock() 
	{
		return (System.currentTimeMillis() - startTime) / 1000.0f;
	}

	public static void setupGUI(int frameX, int frameY, Terrain landdata, waterTerrain waterdata) 
	{
		// setting up frame
		Dimension fsize = new Dimension(800, 800);
		JFrame frame = new JFrame("Waterflow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		// setting up panel
		JPanel g = new JPanel();
		g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));

		// FlowPanel object
		fp = new FlowPanel(landData, waterData);
		fp.setPreferredSize(new Dimension(frameX, frameY));
		g.add(fp);

		// adds bottom panel for buttons
		JPanel b = new JPanel();
		b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

		// implementing action listeners for mouse and buttons
		g.addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 
			{
				int x = e.getX();
				int y = e.getY();
				// _ _ _ ____________
				// y -1 |x-1| x |x+1|
				// _ _ _|___|___|___|
				// y 	|x-1| x |x+1|
				// _ _ _|___|_*_|___|
				// y+1  |x-1| x |x+1|
				// _ _ _|___|___|___|

				// A user click should add 3 units of water to the terrain grid in this manner,
				// then proceed to spread to lower elevations.
				int add = 3;

				waterdata.setHeight(x, y, add);
				waterdata.setHeight(x - 1, y, add);
				waterdata.setHeight(x + 1, y, add);

				waterdata.setHeight(x, y - 1, add);
				waterdata.setHeight(x - 1, y - 1, add);
				waterdata.setHeight(x + 1, y - 1, add);

				waterdata.setHeight(x, y + 1, add);
				waterdata.setHeight(x - 1, y + 1, add);
				waterdata.setHeight(x + 1, y + 1, add);

				waterdata.deriveWaterImage();
				fp.repaint();
			}
		});

		JButton endB = new JButton("End");
		endB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				// to do ask threads to stop
				//pool.shutdown();
				frame.dispose();
				System.exit(0);
			}
		});

		JButton startB = new JButton("Start");
		startB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				waterdata.play();
			}
		});

		JButton pauseB = new JButton("Pause");
		pauseB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				waterdata.pause();
			}
		});

		JButton resetB = new JButton("Reset");
		resetB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				waterData.reset();
				waterData.deriveWaterImage();
				fp.repaint();
			}
		});

		b.add(startB);
		b.add(pauseB);
		b.add(pauseB);
		b.add(resetB);
		b.add(endB);
		g.add(b);
    	
		frame.setSize(frameX, frameY+50);
      	frame.setLocationRelativeTo(null);  // center window on screen
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
		
		Thread fpt = new Thread(fp);
        fpt.start();
	}
	
	
	public static void main(String[] args) 
	{		
		// check that number of command line arguments is correct
		//if(args.length != 1)
		//{
		//	System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
		//	System.exit(0);
		//}

		JOptionPane pane = new JOptionPane();
		String fileName = pane.showInputDialog("Input file name");
				
		// landscape information from file supplied as argument
		landData = new Terrain(fileName);
		waterData = new waterTerrain(landData);
		Thread flow = new Thread(waterData);

		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landData, waterData));


		frameX = landData.getDimX();
		frameY = landData.getDimY();

		flow.start();

	}
}