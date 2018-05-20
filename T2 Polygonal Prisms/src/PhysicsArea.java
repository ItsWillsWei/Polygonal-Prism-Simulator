
//Imports necessary classes
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

/**
 * Runs the physics engine and simulates the rolling of the polygonal prisms
 * 
 * @author Will
 *
 */
@SuppressWarnings("serial")
public class PhysicsArea extends Graph {
	// Define variables
	public final int POINT = 0, LINE = 1;
	public List<Point> plotPoints = new ArrayList<Point>();
	public List<Double> times = new ArrayList<Double>();
	public List<Double> angles = new ArrayList<Double>();
	public List<Double> angularSpeeds = new ArrayList<Double>();
	public List<Double> xPos = new ArrayList<Double>();
	public List<Double> yPos = new ArrayList<Double>();

	public List<String> text = new ArrayList<String>();

	public final double PI = Math.PI;
	public int modelType = 0;

	// Creates the ramp
	GeneralPath rampShape;

	// Timer variables
	Thread timer;
	Particle shape;

	// Logic variables
	public boolean draw = false;
	public boolean isFinished = true;;

	// Calculation variables
	public double length;
	public double angle;
	public double dt;

	// Drawing variables
	public int plotType = LINE;
	public boolean drawAxes = true;

	/**
	 * Makes a new graph with the window sized to the specified size
	 * 
	 * @param x1
	 *            the left boundary
	 * @param x2
	 *            the right boundary
	 * @param y1
	 *            the lower boundary
	 * @param y2
	 *            the upper boundary
	 */
	PhysicsArea(double x1, double x2, double y1, double y2) {
		super(x1, x2, y1, y2);
	}

	/**
	 * Updates the viewing window
	 * 
	 * @param x1
	 *            the left boundary
	 * @param x2
	 *            the right boundary
	 * @param y1
	 *            the lower boundary
	 * @param y2
	 *            the upper boundary
	 */
	public void updateGraph(double x1, double x2, double y1, double y2) {
		super.setValues(x1, x2, y1, y2);
	}

	/**
	 * Creates a new particle object to keep track of the polygonal prism's
	 * movements
	 * 
	 * @param diameter
	 *            the diameter of the object
	 * @param sides
	 *            the object's number of sides
	 * @param theta
	 *            the initial angle (tilt) of the object
	 */
	public void makeShape(double diameter, int sides, double theta) {
		shape = new Particle(diameter, 0, function(0) + diameter / 2, sides, theta / 180.0 * PI);
	}

	/**
	 * Sets the model type 0 = WALKING 1 = RUNNING
	 * 
	 * @param modelType
	 *            the model type (WALKING/RUNNING)
	 */
	public void setModel(int modelType) {
		this.modelType = modelType;
	}

	/**
	 * Uses Euler integration to model the movement of the polygon
	 * 
	 * @param Oinit
	 *            the initial angle phi of the polygon
	 * @param m
	 *            the mass of the polygon
	 * @param r
	 *            the distance from a corner to the center of the polygon
	 * @param n
	 *            the total number of sides of the polygon
	 * @param e
	 *            the coefficient of restitution for an edge collision
	 * @param length
	 *            the distance to be traveled along the hypotenuse of the ramp
	 * @param angle
	 *            the inclination angle of the ramp in degrees
	 * @param dt
	 *            the time increment for the Euler integration
	 * @return the success boolean
	 */
	public boolean process(double Oinit, double m, double r, double n, double e, double length, double angle,
			double dt) {
		System.out.println("Start");
		
		// Creates a new Particle
		makeShape(r * 2, (int) n, angle);

		// Resets all variables for each new ramp angle
		double t = 0.0;
		double O = Oinit;
		double g = 9.81;// Gravitational field at Earth's surface

		this.length = length;
		this.angle = angle;
		this.dt = dt;

		// Calculates the moment of inertia about a corner
		double I = m * r * r * (Math.pow(Math.cos(Math.PI / n), 2) / 3.0 + 7.0 / 6.0);

		// Sets a precise starting angle for the cylinder approximate with large
		// number of sides
		if (n >= 100) {
			System.out.println("Cylinder:");
			O = angle * PI / 180.0;
		}

		// Lists keep track of angle phi (O) at each time t
		times = new ArrayList<Double>();
		angles = new ArrayList<Double>();
		text = new ArrayList<String>();
		times.add(t);
		angles.add(O);
		text.add("0");

		// Lists keep track of x and y positions of the center of mass
		xPos = new ArrayList<Double>();
		yPos = new ArrayList<Double>();
		double x = r * Math.sin(O);
		double y = r * Math.cos(O);
		xPos.add(x);
		yPos.add(y);

		// Sets the ramp angle and the initial angular velocity
		double theta = angle;
		double dO = 0.00000001;
		angularSpeeds = new ArrayList<Double>();
		angularSpeeds.add(dO);

		boolean walking = true;

		// Set the horizontal distance travelled
		double totalHorizontalDist = length * Math.cos(theta / 180.0 * PI);
		if (n < 100)
			totalHorizontalDist -= r * Math.sin(O);

		// Roll until the necessary distance travelled is reached
		while (x <= totalHorizontalDist && walking) {
			// Use Euler's method for each roll
			while (O < theta / 180.0 * PI + PI / n && x <= totalHorizontalDist && walking) {

				// Find the angular velocity
				// Update the angle using the angular velocity
				O = O + dO * dt;
				t += dt;

				// Update time and phi (O)
				times.add(t);
				text.add("0");
				angles.add(O);

				// Update x and y positions of the center of mass
				x += r * (Math.sin(O) - Math.sin(O - dO * dt));// Minus because
																// already
																// updated
				y += r * (Math.cos(O) - Math.cos(O - dO * dt));
				xPos.add(x);
				yPos.add(y);
				plotPoints.add(new Point(x, y));
				dO = Math.sqrt(
						2.0 / I * (m * g * r * Math.cos(O) - m * g * r * Math.cos(O + dO * dt) + 0.5 * I * dO * dO));
				angularSpeeds.add(dO);

				// Losing contact condition
				if (modelType == 1) {
					walking = dO < Math.sqrt(g * Math.cos(O) / r);
				}
			} // for each time the face hits the board or walking done

			// Use Coefficient of restitution for collision
			if (walking) {
				dO *= e;
				// Reset initial angle
				O = theta / 180.0 * PI - PI / n;
			}

			// Use the running model
			if (modelType == 1 && x < totalHorizontalDist && !walking) {

				// Sets the initial x and y velocity of the center of mass
				double Vx0 = dO * r * Math.cos(O);
				double Vy0 = -dO * r * Math.sin(O);
				double tf = 0; // set the initial flying time to 0

				double[] mus = new double[(int) n];
				double[] musI = new double[(int) n];
				double[] Hs = new double[(int) n];

				double mu0 = O + Math.PI / 2 - 2 * Math.PI / n;
				// Sets initial mu values and initial H values for each corner
				for (int corner = 0; corner < n; corner++) {
					musI[corner] = mu0 - corner * 2 * Math.PI / n;
					mus[corner] = mu0 - corner * 2 * Math.PI / n;
				}

				boolean stop = false;
				int lastCorner = -1;

				double VY = Vy0; // Set initial y-velocity for tracing
									// y-coordinate

				boolean[] corners = new boolean[(int) n];
				double highestLastCorner = 0;
				// Loop until the polygon falls down to the ramp
				while (!stop && x <= totalHorizontalDist) {
					// For each corner

					for (int corner = 0; corner < n && !stop; corner++) {

						// Define h = height of center above ramp
						double h = r * Math.cos(O) + r * Math.sin(O) * Math.tan(theta / 180.0 * Math.PI) + Vy0 * tf
								- 1.0 / 2.0 * g * tf * tf + Vx0 * tf * Math.tan(theta / 180.0 * Math.PI);

						mus[corner] += dO * dt;
						// Define L = Distance from horizontal through center to
						// corner
						double L = r * Math.sin(mus[corner]);
						// Define K = Distance from horizontal through bottom of
						// h to ramp, below corner
						double K = r * Math.tan(theta / 180.0 * Math.PI) * Math.cos(mus[corner]);

						Hs[corner] = h - L + K;
						if (corner == n - 1) {
							text.add(String.format("%.4f", (Math.round(Hs[corner] * 10000) / 10000.0)));
							if (Hs[corner] > highestLastCorner)
								highestLastCorner = Hs[corner];
							// System.out.println(highestLastCorner + " !!");
						}

						// Collision detection
						if (Hs[corner] <= 0) {
							if (corner != n - 1 || highestLastCorner > r*0.5) {
								//if (corner == n - 1)
									//System.out.println("in " + highestLastCorner);
								corners[corner] = true;
								lastCorner = corner;
								stop = true;
								//y -= Hs[corner];
							}
						}
					}

					// Update times
					tf = tf + dt;
					t += dt;
					times.add(t);

					angles.add(O + dO * tf);// Continuous
					angularSpeeds.add(dO);// Constant Angular Speed
					// trace the x,y coordinates in the air
					x = x + Vx0 * dt;
					y = y + VY * dt;
					VY = VY - g * dt;

					xPos.add(x);
					yPos.add(y);
					plotPoints.add(new Point(x, y));
				} // polygon has just touched the ramp

				// Finds the initial O and dO for the walking model
				double beta = e * e;// edge-on restitution for energy:
									// coefficient's square

				// update energy profile after collision
				double Er = (0.5 * m * Vx0 * Vx0 + 0.5 * m * Math.pow((Vy0 - g * tf), 2)
						+ 0.5 * (I - m * r * r) * dO * dO) * beta;

				// Converts the mu of the colliding corner to phi
				if (stop == false)
					lastCorner = 0;
				
				//Fix multiple rotations
				while (mus[lastCorner] > 2*Math.PI)
					mus[lastCorner] -= 2*Math.PI;
				
				O = mus[lastCorner] - Math.PI / 2;// starting phi for
													// gaining contact
				// Prevent super negatives
				while (O < -Math.PI / 2)
					O += 2 * Math.PI;
				// set initial phi again for the following loop
				dO = Math.sqrt(2 / I * Er); // set initial angular speed

				// Keep going using the running model
				walking = true;
			}
		} // While the prism hasn't reached the total distance yet

		System.out.println("time: " + t);
		return true;
	}

	/**
	 * Starts the animation
	 * 
	 * @param slowFactor
	 *            the factor to slow the animation down
	 */
	public void startTime(double slowFactor) {
		timer = new Thread(new TimerThread(dt, slowFactor));
		draw = true;
		timer.start();
		makeRamp();
	}

	/**
	 * Creates the ramp shape
	 */
	public void makeRamp() {
		rampShape = new GeneralPath();
		rampShape.moveTo((int) ((0.0 - x1) / dx), (int) ((y2 - 0.0) / dy));
		rampShape.lineTo((int) ((length * Math.cos(angle * PI / 180.0) - x1) / dx),
				(int) ((y2 - (-length * Math.sin(angle * PI / 180.0))) / dy));
		rampShape.lineTo((int) ((0.0 - x1) / dx), (int) ((y2 - (-length * Math.sin(angle * PI / 180.0))) / dy));
		rampShape.lineTo((int) ((0.0 - x1) / dx), (int) ((y2 - 0.0) / dy));
		rampShape.closePath();
	}

	/**
	 * Checks to see if the animation is finished
	 * 
	 * @return whether the animation is finished
	 */
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * Stops the animation
	 */
	public void stopAnimating() {
		isFinished = true;
	}

	/**
	 * Connects plotted points
	 */
	public void plotLine() {
		plotType = LINE;
	}

	/**
	 * Plots individual points
	 */
	public void plotPoint() {
		plotType = POINT;
	}

	/**
	 * Gets the dimensions of the animation graph
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * Updates the graphics
	 */
	public void graphics() {
		repaint(0);
	}

	/**
	 * Draws the animation
	 */
	public void paintComponent(Graphics g) {
		//Create a new Graphics object
		Graphics2D g2 = (Graphics2D) g.create();
		Font font = new Font("TimesRoman", Font.BOLD, 32);
		g2.setFont(font);
		// Clears the screen
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.BLACK);

		// Draw y axis
		g2.drawLine((int) (-x1 / dx), 0, (int) (-x1 / dx), dimension.height);
		// Draw x axis
		g2.drawLine(0, (int) (y2 / dy), dimension.width, (int) (y2 / dy));

		if (draw) {
			g2.setColor(new Color(110, 60, 0));
			g2.fill(rampShape);
			g2.setColor(Color.BLACK);

			// Draws the particle
			Point[] points = shape.getPoints();

			// Draw the inside of the circle
			GeneralPath filler = new GeneralPath();
			filler.moveTo((int) ((points[2].x - x1) / dx), (int) ((y2 - points[2].y) / dy));

			for (int point = 2; point < points.length; point++) {
				filler.lineTo((int) ((points[(point + 1) % points.length].x - x1) / dx),
						(int) ((y2 - points[(point + 1) % points.length].y) / dy));
			}
			filler.closePath();
			g2.setColor(new Color(225, 190, 115));
			g2.fill(filler);
			g2.setColor(Color.BLACK);

			try{
			g2.drawString(shape.getText(), 50, 925);
			}catch(Exception e){}

			boolean drawLines = true;
			if (drawLines) {
				for (int point = 2; point < points.length; point++) {
					g2.drawLine((int) ((points[point].x - x1) / dx), (int) ((y2 - points[point].y) / dy),
							(int) ((points[(point + 1) % points.length].x - x1) / dx),
							(int) ((y2 - points[(point + 1) % points.length].y) / dy));
				}

				// Connects the first point to the origin
				g2.drawLine((int) ((points[2].x - x1) / dx), (int) ((y2 - points[2].y) / dy),
						(int) ((points[0].x - x1) / dx), (int) ((y2 - points[0].y) / dy));
			}
			// Draws the radius
			g2.setColor(Color.RED);
			g2.drawLine((int) ((points[1].x - x1) / dx), (int) ((y2 - points[1].y) / dy),
					(int) ((points[0].x - x1) / dx), (int) ((y2 - points[0].y) / dy));
			g2.setColor(Color.BLACK);

		} // if the ramp and shape is to be drawn
	}

	/**
	 * Keeps track of the animation in real time
	 * 
	 * @author Will
	 */
	public class TimerThread implements Runnable {
		// Define time class variables
		public long start;
		public long last;
		public double dt;
		public double slow;

		/**
		 * Creates a new TimerThread to keep track of a single animation
		 * 
		 * @param dt
		 *            the Euler integration time increment for the model
		 * @param slowFactor
		 *            the factor to slow down the animation
		 */
		public TimerThread(double dt, double slowFactor) {
			this.dt = dt;
			this.slow = slowFactor;
		}

		/**
		 * Animate!
		 */
		public void run() {
			// Gets the current system time
			start = System.currentTimeMillis();
			last = start;
			isFinished = false;
			int element = 0;

			// Animate until there is no more data to animate
			while (element < times.size() && !isFinished) {
				try {
					// Pause for 20 milliseconds
					Thread.sleep(20);

					// Find the amount of time that passed
					long time = System.currentTimeMillis();
					double dt = (time - last) / 1000.0;
					last = time;

					// Update position and angle (angle is counterclockwise)
					shape.update(xPos.get(element), yPos.get(element), -angles.get(element));
					shape.text(String.format("t= %.2fs", times.get(element)));
					// Draw
					graphics();

					// Advance by the number of elements/frames required to slow
					// down by the specified factor
					element += (int) (dt / (this.dt) / slow);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			// Finish the animation
			stopAnimating();
		}// end of run() method
	}// end of TimerThread class
}// end of PhysicsArea class
