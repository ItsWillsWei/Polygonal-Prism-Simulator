
//Import necessary packages
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;

/**
 * Reads in inpur from a CSV file and outputs results into another CSV file
 * 
 * @author Will
 *
 */
@SuppressWarnings("serial")
public class RecordData5 extends Graph {
	// Declare global static variables
	public static List<Point> plotPoints;
	public static boolean draw;

	/**
	 * Creates a new graph with a specified viewing window
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	RecordData5(double x1, double x2, double y1, double y2) {
		// Calls graph
		super(x1, x2, y1, y2);
		draw = false;
		plotPoints = new ArrayList<Point>();
		start();
	}

	/**
	 * Sets up the frame for viewing
	 */
	public void start() {
		System.out.println("Starting");
		JFrame frame = new JFrame("Graphs");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.setMinimumSize(new Dimension(getDimension().width + 20, getDimension().height + 40));
		frame.setResizable(false);// Prevent resizing
		// frame should adapt to the panel size
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Displays the results
	 */
	public void paintComponent(Graphics g) {
		if (draw) {
			// Draw the points
			if (plotType == POINT) {
				// Plot individual points
				for (int point = 0; point < plotPoints.size(); point++) {
					g.drawRect((int) ((plotPoints.get(point).x - x1) / dx), (int) ((y2 - plotPoints.get(point).y) / dy),
							1, 1);
				}
			} else if (plotType == LINE) {
				// Plot points and connect them
				for (int point = 0; point < plotPoints.size() - 1; point++) {
					g.drawLine((int) ((plotPoints.get(point).x - x1) / dx), (int) ((y2 - plotPoints.get(point).y) / dy),
							(int) ((plotPoints.get(point + 1).x - x1) / dx),
							(int) ((y2 - plotPoints.get(point + 1).y) / dy));
				}
			}
		} // if draw

		// Draw y axis
		g.drawLine((int) (-x1 / dx), 0, (int) (-x1 / dx), dimension.height);
		// Draw x axis
		g.drawLine(0, (int) (y2 / dy), dimension.width, (int) (y2 / dy));
	}

	/**
	 * Run the model
	 * 
	 * @param Oinit
	 *            the initial angle phi
	 * @param m
	 *            the mass of the prism
	 * @param r
	 *            the radius of the prism
	 * @param n
	 *            the prism's number of sides
	 * @param e
	 *            the coefficient of restitution for speed
	 * @param length
	 *            the length of the ramp
	 * @param angle
	 *            the angle of the ramp
	 * @param dt
	 *            the Euler integration time increment
	 * @param modelType
	 *            the type of model (WALKING = 0/RUNNING = 1)
	 * @return the time taken for the prism to roll down the ramp
	 */
	public static double process(double Oinit, double m, double r, double n, double e, double length, double angle,
			double dt, int modelType) {
		// Resets all variables for each new ramp angle
		double t = 0.0;
		double O = Oinit;
		double g = 9.81;// Gravitational field at Earth's surface

		// Calculates the moment of inertia about a corner
		double I = m * r * r * (Math.pow(Math.cos(Math.PI / n), 2) / 3.0 + 7.0 / 6.0);

		// Sets a precise starting angle for the cylinder
		if (n >= 100) {
			System.out.println("Cylinder:");
			O = angle * Math.PI / 180.0;
		}

		double x = r * Math.sin(O);
		double y = r * Math.cos(O);

		// Sets the ramp angle and the initial angular velocity
		double theta = angle;
		double dO = 0.00000001;

		boolean walking = true;

		// Set the horizontal distance travelled
		double totalHorizontalDist = length * Math.cos(theta / 180.0 * Math.PI) - r * Math.sin(O);

		// Roll until the necessary distance travelled is reached
		while (x <= totalHorizontalDist && walking) {
			// Use Euler's method for each roll
			while (O < theta / 180.0 * Math.PI + Math.PI / n && x <= totalHorizontalDist && walking) {

				// Find the angular velocity
				// Update the angle using the angular velocity
				O = O + dO * dt;
				t += dt;

				// Update x and y positions of the center of mass
				x += r * (Math.sin(O) - Math.sin(O - dO * dt));// Minus
																// because
																// already
																// did
				y += r * (Math.cos(O) - Math.cos(O - dO * dt));
				dO = Math.sqrt(
						2.0 / I * (m * g * r * Math.cos(O) - m * g * r * Math.cos(O + dO * dt) + 0.5 * I * dO * dO));

				// Losing contact condition
				if (modelType == 1) {
					walking = dO < Math.sqrt(g * Math.cos(O) / r);
				}
			} // for each time the face hits the board or walking done

			// Use Coefficient of restitution
			if (walking) {
				dO *= e;
				// Reset initial angle
				O = theta / 180.0 * Math.PI - Math.PI / n;
			}

			// Use the running model
			if (modelType == 1 && x < totalHorizontalDist && !walking) {

				// initial x and y velocity of the center of mass
				double Vx0 = dO * r * Math.cos(O);
				double Vy0 = -dO * r * Math.sin(O);
				double tf = 0; // set the initial flying time to 0
				double mu0 = O + Math.PI / 2 - 2 * Math.PI / n;

				double[] mus = new double[(int) n];
				double[] musI = new double[(int) n];
				double[] Hs = new double[(int) n];

				// Sets initial mu values and initial H values for each corner
				for (int corner = 0; corner < n; corner++) {
					musI[corner] = mu0 - corner * 2 * Math.PI / n;
					mus[corner] = mu0 - corner * 2 * Math.PI / n;
				}

				double VY = Vy0; // Set initial y-velocity for tracing
									// y-coordinate

				boolean stop = false;
				int lastCorner = -1;
				// Loop until the polygon falls down to the ramp
				while (!stop && x <= totalHorizontalDist) {

					// Do this for each corner
					for (int corner = 0; corner < n && !stop; corner++) {
						// Define h, L, K, H
						double h = r * Math.cos(O) + r * Math.sin(O) * Math.tan(theta / 180.0 * Math.PI) + Vy0 * tf
								- 1.0 / 2.0 * g * tf * tf + Vx0 * tf * Math.tan(theta / 180.0 * Math.PI);

						mus[corner] += dO * dt;

						double L = r * Math.sin(mus[corner]);
						double K = r * Math.tan(theta / 180.0 * Math.PI) * Math.cos(mus[corner]);
						Hs[corner] = h - L + K;

						if (Hs[corner] <= 0) {
							lastCorner = corner;
							stop = true;
						}

					}
					tf += dt; // update time
					t += dt;

					// trace the x,y coordinates in the air
					x = x + Vx0 * dt;
					y = y + VY * dt;
					VY = VY - g * dt;
				} // polygon has just touched the ramp

				// Finds the initial O and dO for the walking model
				double beta = e * e;// edge-on restitution coefficient's
											// square
				// update energy profile after collision
				double Er = (0.5 * m * Vx0 * Vx0 + 0.5 * m * Math.pow((Vy0 - g * tf), 2)
						+ 0.5 * (I - m * r * r) * dO * dO) * beta;
				O = mus[stop ? lastCorner : 0] - Math.PI / 2;// starting phi for
																// gaining
																// contact
				dO = Math.sqrt(2 / I * Er); // set initial angular speed

				// Keep going using the running model
				walking = true;
			}
		} // While the prism has not traveled the total length of the ramp
		return t;
	}

	public static void main(String[] args) {
		// Initialize and define the viewing window
		double x1 = 0;
		double x2 = 0.25;
		double y1 = -0.25;
		double y2 = 0;

		RecordData5 graph = new RecordData5(x1, x2, y1, y2);

		// Define and initialize variables and constants
		double Oinit = 0.08085;// 0.1135177;//0.08085;// //0.1135177; // degrees
		double t;// Time

		double m = 0.0233;// 0.0280;//0.0252;//
							// ////0.0233;//0.0252;////square0.0252;//0.0233;//hex:0.0280;//square:0.0252;//
							// mass
		// double g = 9.81;// gravitational field
		double r = 0.011865;// 0.01413252572;///0.017334722741;//
							// ////0.011865;//0.01413252572;//0.017334722741;//cylinder//0.011865;//hexagon//0.01413252572;//square//0.017334722741;//
							// Radius of object
		double theta = 25;// Ramp angle
		int n = 1000;
		// double I = m*r*r*(Math.pow(Math.cos(Math.PI/n),
		// 2)/3.0+7.0/6.0);//m/6.0 * Math.pow(Math.cos((n-2)*Math.PI/(2.0*n)),
		// 2)*r*r*(1+3*Math.pow(1.0/Math.tan(Math.PI/n), 2)) + m*r*r;// Moment
		// of inertia about edge
		double e = 1;// 0.856370966258631;//0.656282789918262;//
						// 0.830672294438214;//0.882069405158529;////0.626049;//0.6865166;//////1;//0.8;////1;//0.856370966258631;//
						// Coefficient of restitution (wf/wi)

		double length = 0.279;// Length travelled down ramp

		// Define and initialize time variables
		double dt = 0.000001;
		//final double PI = 3.1415926535897932384626433832795029;
		int modelType = 1;

		// Sets up writer to CSV spreadsheet
		try {
			// Creates a new PrintWriter object to write to a CSV file
			BufferedReader br = new BufferedReader(new FileReader(new File("In.csv")));
			PrintWriter output = new PrintWriter(new File("Data.csv"));

			// Prints the date
			output.print("Date,");
			output.println(new Date().toString());
			output.println();
			output.println("Angle,Time,Theta,dTheta");

			// Writes data to the spreadsheet for each ramp angle
			String line = br.readLine();
			while (line != null) {

				// Resets all variables for each new ramp angle
				t = 0;
				// double O = Oinit;
				double angle = Double.parseDouble(line);
				theta = angle;

				t = process(Oinit, m, r, n, e, length, theta, dt, modelType);
				// Print out info
				System.out.println(angle);
				System.out.println("t: " + t);
				output.println(theta + "," + t);

				line = br.readLine();
			} // For each ramp angle theta

			draw = true;
			graph.repaint();
			System.out.println("Done");
			output.close();
			br.close();
		} // PrintWriter try section
		//Catch file errors
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
