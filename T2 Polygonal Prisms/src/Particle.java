/**
 * Keeps track of the polygonal prism's coordinates and movements
 * @author Will
 *
 */
public class Particle {
	//Initialize class variables
	public double diameter;
	public double radius;
	public int sides;
	public double intAngle;
	public Point center;
	public Point vel;
	public Point[] points;
	double theta = 0;
	public String text;

	/**
	 * Creates a new object
	 * @param diameter
	 */
	Particle(double diameter) {
		this.diameter = diameter;
		radius = diameter / 2.0;
		vel = new Point(0, 0);
	}

	/**
	 * Creates a new object with specifications
	 * @param diameter the object's diameter
	 * @param x the x coordinate of the object's center
	 * @param y the y coordinate of the object's center
	 * @param sides the object's number of sides
	 * @param theta the initial angle (physical tilt) of the object
	 */
	Particle(double diameter, double x, double y, int sides, double theta) {
		this(diameter);
		center = new Point(x, y);
		this.sides = sides;
		intAngle = 2 * Math.PI / sides;
		this.theta = theta;
		create();
	}

	/**
	 * Creates the array of points that defines the polygon
	 */
	public void create() {
		//Creates the bottom point and the center point
		points = new Point[sides + 1];
		points[0] = new Point(center.x, center.y - radius);
		points[1] = new Point(center.x, center.y);
		
		// Add the remaining faces
		for (int point = 2; point < points.length; point++) {
			// Rotate the remaining points about the center to create the shape
			double rot = (point - 1) * intAngle;
			double x = (points[0].x - points[1].x) * Math.cos(rot) - (points[0].y - points[1].y) * Math.sin(rot)
					+ points[1].x;
			double y = (points[0].x - points[1].x) * Math.sin(rot) + (points[0].y - points[1].y) * Math.cos(rot)
					+ points[1].y;
			points[point] = new Point(x, y);
		}

		// Rotate the particle about the edge touching the plane
		// so that its initial tilt is theta
		for (int point = 1; point < points.length; point++) {
			double x = points[point].x;
			double y = points[point].y;
			points[point].x = (x - points[0].x) * Math.cos(theta) - (y - points[0].y) * Math.sin(theta) + points[0].x;
			points[point].y = (x - points[0].x) * Math.sin(theta) + (y - points[0].y) * Math.cos(theta) + points[0].y;
		}

	}

	/**
	 * Updates the specified point
	 * @param point the index of the point
	 * @param newX the updated x coordinate
	 * @param newY the updated y coordinate
	 */
	public void changePoint(int point, double newX, double newY) {
		points[point] = new Point(newX, newY);
	}

	/**
	 * Gets the set of points that defines the particle
	 * @return the array of points
	 */
	public Point[] getPoints() {
		return points;
	}
	
	public void text(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}

	/**
	 * Updates the center of the shape as well as the surrounding points
	 * @param newX the new x coordinate
	 * @param newY the new y coordinate
	 * @param theta the angle (tilt)
	 */
	public void update(double newX, double newY, double theta) {
		//Updates the coordinates of the particle's center
		center.x = newX;
		center.y = newY;
		this.theta = theta;

		// Same as create() but without creating new Objects
		points[0].x = center.x;
		points[0].y = center.y - radius;
		points[1].x = center.x;
		points[1].y = center.y;

		// Add the remaining faces
		for (int point = 2; point < points.length; point++) {
			// Rotate the remaining points about the center to create the shape
			double rot = (point - 1) * intAngle;
			double x = (points[0].x - points[1].x) * Math.cos(rot) - (points[0].y - points[1].y) * Math.sin(rot)
					+ points[1].x;
			double y = (points[0].x - points[1].x) * Math.sin(rot) + (points[0].y - points[1].y) * Math.cos(rot)
					+ points[1].y;
			points[point].x = x;
			points[point].y = y;
		}

		// Rotate the particle about the center of the polygon
		for (int point = 0; point < points.length; point++) {
			if (point != 1) {
				double x = points[point].x;
				double y = points[point].y;
				points[point].x = (x - points[1].x) * Math.cos(theta) - (y - points[1].y) * Math.sin(theta)
						+ points[1].x;
				points[point].y = (x - points[1].x) * Math.sin(theta) + (y - points[1].y) * Math.cos(theta)
						+ points[1].y;
			}
		}
	}

	/**
	 * Rotates the particle dTheta clockwise about the center
	 * @param dTheta the angle to be rotated
	 */
	public void updateThetaOld(double dTheta) {
		//Update the angle
		theta += dTheta;

		// Rotates the center about the edge touching the line
		points[1].x = (points[1].x - points[0].x) * Math.cos(dTheta) - (points[1].y - points[0].y) * Math.sin(dTheta)
				+ points[0].x;
		points[1].y = (points[1].x - points[0].x) * Math.sin(dTheta) + (points[1].y - points[0].y) * Math.cos(dTheta)
				+ points[0].y;

		// Finds dx and dy to determine the angle
		// Used to adjust the shrinking radius problem
		double dx = 0, dy = 0;
		dx = points[1].x - points[0].x;
		dy = points[1].y - points[0].y;

		double angle = 0;
		// Determines the angle from the center to the edge that touches the
		// ground
		if (dx == 0) {
			if (dy > 0) {
				angle = Math.PI / 2.0;
			} else {
				angle = 3.0 * Math.PI / 2.0;
			}
		} else if (dx > 0) {
			if (dy > 0) {
				angle = Math.atan(dy / dx);
			} else {
				angle = 2 * Math.PI + Math.atan(dy / dx);
			}
		} else {
			if (dy > 0) {
				angle = Math.PI + Math.atan(dy / dx);
			} else {
				angle = Math.PI + Math.atan(dy / dx);
			}
		}

		// Makes each point a fixed distance (radius) from the center
		// of the particle
		points[1].x = points[0].x + radius * Math.cos(angle);
		points[1].y = points[0].y + radius * Math.sin(angle);

		// Rotates each of the remaining edges about the center of the
		// particle
		for (int point = 2; point < points.length; point++) {

			double rot = (point - 1) * intAngle;
			points[point].x = (points[0].x - points[1].x) * Math.cos(rot) - (points[0].y - points[1].y) * Math.sin(rot)
					+ points[1].x;
			points[point].y = (points[0].x - points[1].x) * Math.sin(rot) + (points[0].y - points[1].y) * Math.cos(rot)
					+ points[1].y;
		}
	}
}
