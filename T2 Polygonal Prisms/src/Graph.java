//Imports necessary packages
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Sets up a basic Graphics class that other classes can extend
 * @author Will
 *
 */
@SuppressWarnings("serial")
public class Graph extends JPanel {
	//Declares class variables
	public Point center;
	public Dimension dimension = new Dimension(1000, 1000);
	public Point[] points;
	protected double x1, x2, y1, y2, dx, dy;
	public final int POINT = 0, LINE = 1;
	public int plotType = LINE;
	public List<Point> dataPoints = new ArrayList<Point>();

	/**
	 * Creates a new Graph with a specific viewing window size
	 * @param x1 the left boundary
	 * @param x2 the right boundary
	 * @param y1 the lower boundary
	 * @param y2 the upper boundary
	 */
	Graph(double x1, double x2, double y1, double y2) {
		super();

		points = new Point[dimension.width];
		setValues(x1, x2, y1, y2);
	}

	/**
	 * Sets the size of the graph
	 * 
	 * @param x1 the left boundary
	 * @param x2 the right boundary
	 * @param y1 the lower boundary
	 * @param y2 the upper boundary
	 */
	public void setValues(double x1, double x2, double y1, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		dy = (y2 - y1) / dimension.height;
		dx = (x2 - x1) / dimension.width;
	}

	/**
	 * Generates a function with a given x independent variable
	 * @param x the input
	 * @return the output of the function
	 */
	public double function(double x) {
		return -1;
	}

	/**
	 * Gets the dimensions of the graph
	 * @return the size of the graph
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * Draws the graph and the points that are to be plotted
	 */
	public void paintComponent(Graphics g) {
		// Clears the screen with a grey rectangle
		g.setColor(Color.WHITE);//Color.GRAY.brighter());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		
		//Plots points
		if (plotType == POINT) {
			// Plot individual points
			for (int point = 0; point < dataPoints.size(); point++) {
				g.drawRect((int) ((dataPoints.get(point).x - x1) / dx), (int) ((y2 - dataPoints.get(point).y) / dy), 1,
						1);
			}
		}
		//Plots lines by connecting the points
		else if (plotType == LINE) {
			// Plot individual points and connects them
			for (int point = 0; point < dataPoints.size() - 1; point++) {
				g.drawLine((int) ((dataPoints.get(point).x - x1) / dx), (int) ((y2 - dataPoints.get(point).y) / dy),
						(int) ((dataPoints.get(point + 1).x - x1) / dx),
						(int) ((y2 - dataPoints.get(point + 1).y) / dy));
			}
		}

		// Draw y axis vertical line
		g.drawLine((int) (-x1 / dx), 0, (int) (-x1 / dx), dimension.height);
		// Draw x axis horizontal line
		g.drawLine(0, (int) (y2 / dy), dimension.width, (int) (y2 / dy));
	}
}