//Import necessary packages
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * Sets up the tabs and windows in the simulation program.
 * Starts the physics engine.
 * @author Will
 *
 */
@SuppressWarnings("serial")
public class PhysicsFrame extends JPanel {
	// Declare and initialize Global variables
	public final Dimension DIMENSION = new Dimension(1000, 1000);
	public final Dimension FIELD = new Dimension(100, 20);
	public final Dimension STRETCH = new Dimension(120,20);
	//Set viewing window coordinates
	public final String[][] PARAMETERS = {{"-0.1","1","-2","2"},{"-0.1","1.0","-10","100"},{"-0.05","0.25","-0.25","0.05"}};
	public PhysicsArea area;
	public boolean finishedProcessing = false, animating = false;
	public List<JPanel> panels = new ArrayList<JPanel>();
	public List<Graph> graphs = new ArrayList<Graph>();
	public JPanel main;
	public int modellingType = 0;

	/**
	 * Create a new physics frame
	 */
	public PhysicsFrame() {
		// Create a new JPanel that holds all tabs
		super(new GridLayout(1, 0));

		// Sets up the animation panel
		setUp();

		// Sets up the other graph panels
		createPanels();

		// Adds all panels as tabs
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Animation", main);
		for (int panel = 0; panel < panels.size(); panel++) {
			tabs.addTab(panels.get(panel).getName(), null, panels.get(panel));
		}

		//Finishes setting up the main window
		tabs.setSelectedIndex(0);
		add(tabs);
		this.setVisible(true);
	}

	/**
	 * Sets up each of the tabs
	 */
	public void createPanels() {
		//Creates each tab panel
		JPanel thetaTime = new JPanel();
		thetaTime.setName("Angle vs Time");

		JPanel omegaTime = new JPanel();
		omegaTime.setName("Angular Speed vs Time");

		JPanel xy = new JPanel();
		xy.setName("Position Graph");

		//Add panels to the frame
		panels.add(thetaTime);
		panels.add(omegaTime);
		panels.add(xy);

		// Add settings common to all graphs
		for (int panel = 0; panel < panels.size(); panel++) {
			//Sets up the GridBagLayout
			JPanel current = panels.get(panel);
			current.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();

			// Adds the graph
			Graph graph = new Graph(Double.parseDouble(PARAMETERS[panel][0]), Double.parseDouble(PARAMETERS[panel][1]), Double.parseDouble(PARAMETERS[panel][2]), Double.parseDouble(PARAMETERS[panel][3]));

			// Adds graph
			graph.setPreferredSize(DIMENSION);
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.PAGE_START;
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridheight = 7;
			current.add(graph, constraints);
			graphs.add(graph);

			// Adds labels
			JLabel label = new JLabel("Parameters: ");
			label.setPreferredSize(STRETCH);
			constraints.fill = GridBagConstraints.NONE;
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridheight = 1;
			current.add(label, constraints);

			JLabel label2 = new JLabel(" ");
			label2.setPreferredSize(STRETCH);
			constraints.fill = GridBagConstraints.NONE;
			constraints.gridx = 2;
			constraints.gridy = 0;
			constraints.gridheight = 1;
			current.add(label2, constraints);

			// Adds Labels and TextFields for parameters
			JLabel x1Label = new JLabel("x1: ");
			JTextField x1Text = new JTextField(PARAMETERS[panel][0]);
			JLabel x2Label = new JLabel("x2: ");
			JTextField x2Text = new JTextField(PARAMETERS[panel][1]);
			JLabel y1Label = new JLabel("y1: ");
			JTextField y1Text = new JTextField(PARAMETERS[panel][2]);
			JLabel y2Label = new JLabel("y2: ");
			JTextField y2Text = new JTextField(PARAMETERS[panel][3]);

			// Add graph size labels
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 1;
			constraints.gridy = 1;
			constraints.gridheight = 1;
			current.add(x1Label, constraints);
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 2;
			constraints.gridy = 1;
			constraints.gridheight = 1;
			current.add(x1Text, constraints);

			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 1;
			constraints.gridy = 2;
			constraints.gridheight = 1;
			current.add(x2Label, constraints);
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 2;
			constraints.gridy = 2;
			constraints.gridheight = 1;
			current.add(x2Text, constraints);

			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 1;
			constraints.gridy = 3;
			constraints.gridheight = 1;
			current.add(y1Label, constraints);
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 2;
			constraints.gridy = 3;
			constraints.gridheight = 1;
			current.add(y1Text, constraints);

			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 1;
			constraints.gridy = 4;
			constraints.gridheight = 1;
			current.add(y2Label, constraints);
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 2;
			constraints.gridy = 4;
			constraints.gridheight = 1;
			current.add(y2Text, constraints);

			// Adds the resize button
			JButton resize = new JButton("Resize!");
			resize.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					// Set the graph window size
					graph.setValues(Double.parseDouble(x1Text.getText()), //
							Double.parseDouble(x2Text.getText()), //
							Double.parseDouble(y1Text.getText()), //
							Double.parseDouble(y2Text.getText()));//
					graph.repaint();
				}
			});
			
			//Sizes the graph properly
			graph.repaint();

			// Adds the resize button
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 1;
			constraints.gridy = 5;
			constraints.gridwidth = 2;
			current.add(resize, constraints);
			
			//Adds space
			JLabel space = new JLabel(" ");
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 1;
			constraints.gridy = 6;
			constraints.gridwidth = 2;
			current.add(space, constraints);
		}
	}

	/**
	 * Sets up the frame with buttons on the right and the display on the left
	 */
	public void setUp() {
		//Creates the main panel
		main = new JPanel();
		main.setLayout(new GridBagLayout());
		
		// Creates a Graph area to display the animation
		area = new PhysicsArea(-0.1, 0.3, -0.3, 0.1);

		// Adds Labels and TextFields for parameters
		JLabel x1Label = new JLabel("x1: ");
		JTextField x1Text = new JTextField("-0.1");
		JLabel x2Label = new JLabel("x2: ");
		JTextField x2Text = new JTextField("0.3");
		JLabel y1Label = new JLabel("y1: ");
		JTextField y1Text = new JTextField("-0.3");
		JLabel y2Label = new JLabel("y2: ");
		JTextField y2Text = new JTextField("0.1");

		JLabel OinitLabel = new JLabel("Initial \u03D5 (rad): ");
		JTextField OinitText = new JTextField("0.085");
		JLabel massLabel = new JLabel("Mass (kg): ");
		JTextField massText = new JTextField("0.0252");
		JLabel radiusLabel = new JLabel("Radius (m): ");
		JTextField radiusText = new JTextField("0.01733");
		JLabel sidesLabel = new JLabel("Number of sides: ");
		JTextField sidesText = new JTextField("4");
		JLabel betaLabel = new JLabel("\u03B2: ");
		JTextField betaText = new JTextField("0.9");
		JLabel rampLabel = new JLabel("Ramp length (m): ");
		JTextField rampText = new JTextField("0.279");
		JLabel rampAngleLabel = new JLabel("Ramp Angle \u03B8 (deg): ");
		JTextField rampAngleText = new JTextField("33");
		JLabel dtLabel = new JLabel("dt: ");
		JTextField dtText = new JTextField("0.000001");

		//Sets up the radio buttons to select the model type
		JRadioButton walking = new JRadioButton("Walking");
		walking.setSelected(true);
		walking.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modellingType = 0;
			}
		});

		JRadioButton running = new JRadioButton("Running");
		running.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modellingType = 1;
			}
		});

		ButtonGroup modelType = new ButtonGroup();
		modelType.add(walking);
		modelType.add(running);

		//Sets up the animation speed input
		JLabel animSpeedLabel = new JLabel("Animation Speed:");
		JTextField speedText = new JTextField("8");
		speedText.setHorizontalAlignment(JTextField.RIGHT);
		JLabel speedLabel = new JLabel("x slower");
		
		//Sets up the animation buttons
		JButton process = new JButton("Process");
		JButton animate = new JButton("Animate!");
		animate.setEnabled(false);

		//If the process button is pressed
		process.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop the animation if it is already started
				if (!area.isFinished()) {
					area.stopAnimating();
					animate.setText("Animate");

					// Allow 10 milliseconds for the animation to stop
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				// Set the graph window size
				area.updateGraph(Double.parseDouble(x1Text.getText()), //
						Double.parseDouble(x2Text.getText()), //
						Double.parseDouble(y1Text.getText()), //
						Double.parseDouble(y2Text.getText()));//

				// Display processing info while the information is processing
				process.setText("Processing");
				process.setEnabled(false);
				area.setModel(modellingType);
				area.process(// Process information and parse Strings
						Double.parseDouble(OinitText.getText()), // Initial
																	// phi
						Double.parseDouble(massText.getText()), // mass
						Double.parseDouble(radiusText.getText()), // radius
						Double.parseDouble(sidesText.getText()), // number
																	// of
																	// sides
						Double.parseDouble(betaText.getText()), // coefficient
																// of
																// restitution
						Double.parseDouble(rampText.getText()), // ramp
																// length
						Double.parseDouble(rampAngleText.getText()), // ramp
																		// angle
																		// in
																		// degrees
						Double.parseDouble(dtText.getText()));// time
																// increment
				// Set up other graphs
				graphPanels();

				// Allow for re-processing
				process.setText("Re-process");
				process.setEnabled(true);
				// Enable animation
				animate.setEnabled(true);
			}
		});

		//If the animate button is pressed
		animate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop the animation
				if (!area.isFinished()) {
					area.stopAnimating();
					animate.setText("Animate");
				} else {
					// Animate
					animate.setText("Start/Stop");
					// animate.setEnabled(false);
					process.setText("Animating!!!");
					process.setEnabled(false);
					// area.requestFocusInWindow();
					area.startTime(Double.parseDouble(speedText.getText()));
					// area.requestFocusInWindow();
					// animating = false;

					/// animate.setText("Animate!");
					// animate.setEnabled(true);
					process.setText("Re-process");
					process.setEnabled(true);
					// finishedProcessing = false;
				}
			}
		});

		//Adds the spacers
		JLabel space1 = new JLabel("Parameters:");
		JLabel space2 = new JLabel(" ");
		JLabel space3 = new JLabel(" ");

		//Initializes the specifications for component placement
		GridBagConstraints constraints = new GridBagConstraints();

		// Adds the graph
		area.setPreferredSize(DIMENSION);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 19;
		main.add(area, constraints);

		//Adds the spacers
		space1.setPreferredSize(STRETCH);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		main.add(space1, constraints);
		space2.setPreferredSize(STRETCH);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		main.add(space2, constraints);
		
		// Add graph size labels		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		main.add(x1Label, constraints);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		main.add(x1Text, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		main.add(x2Label, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		main.add(x2Text, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridheight = 1;
		main.add(y1Label, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridheight = 1;
		main.add(y1Text, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridheight = 1;
		main.add(y2Label, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 4;
		constraints.gridheight = 1;
		main.add(y2Text, constraints);

		
		// Adds Oinit label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridheight = 1;
		main.add(OinitLabel, constraints);
		
		// Adds Oinit field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.gridheight = 1;
		main.add(OinitText, constraints);
		
		// Adds mass label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridheight = 1;
		main.add(massLabel, constraints);
		
		// Adds mass field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 6;
		constraints.gridheight = 1;
		main.add(massText, constraints);
		
		// Adds radius label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 7;
		constraints.gridheight = 1;
		main.add(radiusLabel, constraints);
		
		// Adds radius field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 7;
		constraints.gridheight = 1;
		main.add(radiusText, constraints);

		
		// Adds sides label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 8;
		constraints.gridheight = 1;
		main.add(sidesLabel, constraints);
		
		// Adds sides field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 8;
		constraints.gridheight = 1;
		main.add(sidesText, constraints);
		
		// Adds beta label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 9;
		constraints.gridheight = 1;
		main.add(betaLabel, constraints);
		
		// Adds beta field
		rampText.setPreferredSize(STRETCH);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 9;
		constraints.gridheight = 1;
		main.add(betaText, constraints);
		
		// Adds ramp label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 10;
		constraints.gridheight = 1;
		main.add(rampLabel, constraints);
		
		// Adds ramp field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 10;
		constraints.gridheight = 1;
		main.add(rampText, constraints);
		
		// Adds ramp angle label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 11;
		constraints.gridheight = 1;
		main.add(rampAngleLabel, constraints);
		
		// Adds ramp angle field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 11;
		constraints.gridheight = 1;
		main.add(rampAngleText, constraints);

		// Adds dt label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 12;
		constraints.gridheight = 1;
		main.add(dtLabel, constraints);
		
		// Adds dt field
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 12;
		constraints.gridheight = 1;
		main.add(dtText, constraints);

		// Adds the radio buttons for model type
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 13;
		constraints.gridwidth = 1;
		main.add(walking, constraints);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 13;
		constraints.gridwidth = 1;
		main.add(running, constraints);
		
		// Adds the start/stop button
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 14;
		constraints.gridwidth = 2;
		main.add(process, constraints);

		// Adds the animate button
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 15;
		constraints.gridwidth = 2;
		main.add(animate, constraints);

		// Adds the animation speed label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 16;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		main.add(animSpeedLabel, constraints);
		
		// Adds the speed text
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 17;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		main.add(speedText, constraints);
		
		// Adds the speed label
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = 17;
		constraints.gridheight = 1;
		main.add(speedLabel, constraints);

		// Adds the spacer
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 18;
		main.add(space3, constraints);
	}

	/**
	 * Graphs the other tabs after the model has finished processing
	 */
	public void graphPanels() {
		//Create new lists to store the data points
		for (int graph = 0; graph < graphs.size(); graph++) {
			graphs.get(graph).dataPoints = new ArrayList<Point>();
		}

		//Add points to the graphs
		for (int element = 0; element < area.times.size(); element++) {
			graphs.get(0).dataPoints.add(new Point(area.times.get(element), area.angles.get(element)));
			graphs.get(1).dataPoints.add(new Point(area.times.get(element), area.angularSpeeds.get(element)));
			graphs.get(2).dataPoints.add(new Point(area.xPos.get(element), area.yPos.get(element)));
		}
		
		//Update the graphs
		for (int graph = 0; graph < graphs.size(); graph++) {
			graphs.get(graph).repaint();
		}
	}

	/**
	 * Gets the dimensions of the PhysicsFrame
	 * @return the dimensions
	 */
	public Dimension getDimension() {
		return DIMENSION;
	}
}
