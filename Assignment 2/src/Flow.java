import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

public class Flow extends frame implements MouseListener
{
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;

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
	
	public static void setupGUI(int frameX,int frameY,Terrain landdata) 
	{
		Dimension fsize = new Dimension(800, 800);
    	JFrame frame = new JFrame("Waterflow"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setLayout(new BorderLayout());
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
   
		fp = new FlowPanel(landdata);
		fp.setPreferredSize(new Dimension(frameX,frameY));
		g.add(fp);
	    
		// to do: add a MouseListener, buttons and ActionListeners on those buttons
	   	
		JPanel b = new JPanel();
	    b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
		JButton endB = new JButton("End");;
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// to do ask threads to stop
				frame.dispose();
			}
		});
		
		b.add(endB);
		g.add(b);
    	
		frame.setSize(frameX, frameY+50);	// a little extra space at the bottom for buttons
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
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}
				
		// landscape information from file supplied as argument
		Terrain landData = new Terrain(args[0]);
		waterTerrain waterData = new waterTerrain(landData);
		
		frameX = landData.getDimX();
		frameY = landData.getDimY();
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata));

		new mouseListener(); 
		
		// to do: initialise and start simulation
	}
}

public class mouseListener extends Frame implements MouseListener
{
	public void mouseClicked(MouseEvent e) 
	{  
        Graphics g=getGraphics();  
        g.setColor(Color.BLUE);  
		g.fillOval(e.getX(),e.getY(),30,30); 
		
		int x = Math.round(e.getX());
        int y = Math.round(e.getY());

        /*
         _ _ _ ____________ 
         y -1  |x-1| x |x+1|
         _ _ _ |___|___|___|
         y     |x-1| x |x+1|
         _ _ _ |___|_*_|___|
         y+1   |x-1| x |x+1|
         _ _ _ |___|___|___|

         A user click should add 3 units of water to the terrain grid in this manner, 
         then proceed to spread to lower elevations.
               
        */

        int add = 3;

        setHeight(x, y, add);
        setHeight(x-1, y, add);
        setHeight(x+1, y, add);

        setHeight(x, y-1, add);
        setHeight(x-1, y-1, add);
        setHeight(x+1, y-1, add);
        
        setHeight(x, y+1, add);
        setHeight(x-1, y+1, add);
        setHeight(x+1, y+1, add);  
    }  
	
	public void mouseEntered(MouseEvent e) {}  
    public void mouseExited(MouseEvent e) {}  
    public void mousePressed(MouseEvent e) {}  
    public void mouseReleased(MouseEvent e) {}  
}
