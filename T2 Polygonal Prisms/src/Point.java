/**
 * Keeps track of x and y coordinates
 * @author Will
 *
 */
public class Point {
	public double x;
	public double y;
	
	/**
	 * Creates a new point
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 */
	Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new point from an existing point
	 * @param point the existing point
	 */
	Point(java.awt.Point point)
	{
		this.x = point.x;
		this.y = point.y;
	}
}