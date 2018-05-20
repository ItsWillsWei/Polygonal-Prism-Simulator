//Imports necessary packages
import java.awt.*;
import javax.swing.JFrame;

/**
 * Creates and opens the window that contains the simulation.
 * @author Will Wei & Liheng Chen
 * @version March 31 2016
 */
public class Main {

	public Main(){
		//Creates a new window to display the application
		JFrame frame = new JFrame("Simulation");
		
		//Sets the icon of the window
		frame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage("image.png"));		
		
		//Creates a new physics engine for rolling polygonal prisms
		PhysicsFrame contentPane = new PhysicsFrame();
		contentPane.setOpaque(true); // content panes must be opaque
		
		//Properly sets up the frame for viewing
		frame.setContentPane(contentPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setMinimumSize(new Dimension(contentPane.getDimension().width+20, contentPane.getDimension().height+40));
		frame.setResizable(false);//Prevent resizing
		// frame should adapt to the panel size
		frame.pack();
		frame.setVisible(true);
		
	}
	public static void main(String[] args) {
		//Runs program
		new Main();
	}
}