package vue;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.Random;

import javax.swing.*;

import modele.*;
import controleur.*;

public class MenuFrame extends JFrame implements ActionListener, ItemListener {
	
	private static final long serialVersionUID = 1L;
	private static final int nLaps = 3; // always 3
	
	private HallFrame hallFrame;
	private HallOfFame hall;
	
	// main menu variables
	private JPanel mainPanel; // panel of the main menu
	private JButton[] buttons; 
	
	// race menu variables
	private JPanel racePanel; // panel of the menu where we choose the race parameters
		
	// car panel variables (one to each player)
	private JPanel[] carPanel;
	@SuppressWarnings("rawtypes")
	private JComboBox[] menuCar;
	private JLabel[] carIcon;
	private JProgressBar[][] carStats;

	// track panel variables
	private JPanel trackPanel;
	@SuppressWarnings("rawtypes")
	private JComboBox menuTrack;
	private JLabel trackIcon;

	private JButton buttonOk; // button to start the race !
	private JButton buttonMenu; // button to go back to the main menu.
	
	// status variables
	private int selTrack;
	private int[] selCar;
	private int numPlayers;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MenuFrame() throws FileNotFoundException {
		super("Super Sprint Supélec");
		
		// initialize the hall of fame
		hallFrame = new HallFrame(this);
		hall = new HallOfFame(hallFrame);
		selCar = new int[2];
		
		// main menu
		mainPanel = new JPanel();
		JPanel imagePanel = new JPanel();
		JLabel image = new JLabel(new ImageIcon("images/menu.png"));
		imagePanel.add(image);
		
		// main menu's buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,2));
		buttons = new JButton[4];
		buttons[0] = new JButton("1 joueur");
		buttons[1] = new JButton("2 joueurs");
		buttons[2] = new JButton("Hall of Fame");
		buttons[3] = new JButton("Aide & Info");
		int k, m;								// index variables
		for (k = 0; k < buttons.length; k++) {
			buttonPanel.add(buttons[k]);
			buttons[k].addActionListener(this);	// se ActionListener function
			buttons[k].setMnemonic(k);			// each button has a code
		}
		// main menu
		mainPanel.add(imagePanel);
		mainPanel.add(buttonPanel);
		
		// race menu
		racePanel = new JPanel();
		carPanel = new JPanel[2];
		menuCar = new JComboBox[2];
		carStats = new JProgressBar[2][3];
		carIcon = new JLabel[2];
		trackPanel = new JPanel();
		

		// creates every variable twice
		for (k = 0; k < 2; k++) { // k is the player's number
			// the car's menu, numCars options
			String[] stringCar = new String[Voiture.numCars];
			for (m = 0; m < Voiture.numCars; m++) {
				stringCar[m] = "Modèle "+Integer.toString(m+1);
			}
			menuCar[k] = new JComboBox(stringCar);
			menuCar[k].setBackground(Color.WHITE);
			menuCar[k].addItemListener(this);
			menuCar[k].setName("car"+Integer.toString(k+1));
			carIcon[k] = new JLabel();
			// end of the intern components (labels and menus)
			
			// the car panel itself
			carPanel[k] = new JPanel();
			carPanel[k].setLayout(new BoxLayout(carPanel[k],BoxLayout.Y_AXIS));	// 
			carPanel[k].add(new JLabel("Player " + Integer.toString(k+1)));		// player identifier
			carPanel[k].add(menuCar[k]);										// the menu
			carPanel[k].add(carIcon[k]);										// the icon
			JPanel panelStats = new JPanel();									// a specific panel to the stats that will be shown
			panelStats.setLayout(new GridLayout(3,2));
			
			String[] stats = {"Acceleration  ", "Vitesse Max.  ", "Tenue de Route  "}; // attributes' names
			// each attribute will have a unique Label
			int[][] limits = {{100,250},{200,400},{30,60}}; // the visual limits to each status progress bar, defined as convenient
			for(m = 0; m < 3; m++) {
				panelStats.add(new JLabel(stats[m]));
				carStats[k][m] = new JProgressBar();
				carStats[k][m].setMinimum(limits[m][0]);
				carStats[k][m].setMaximum(limits[m][1]);
				carStats[k][m].setStringPainted(true);
				carStats[k][m].setPreferredSize(new Dimension(10,10));
				panelStats.add(carStats[k][m]);
			}
			carPanel[k].add(panelStats);
			// ends by add the panel at the race panel
			racePanel.add(carPanel[k]);
		}
		
		// track menu and its other components
		String[] stringTrack = new String[Circuit.numCirc];
		for(m = 0; m < Circuit.numCirc; m++) {
			stringTrack[m] = "Circuit "+Integer.toString(m+1);
		}
		menuTrack = new JComboBox (stringTrack);
		menuTrack.setBackground(Color.WHITE);
		menuTrack.addItemListener(this);
		menuTrack.setName("track");
		trackPanel.setLayout(new BoxLayout(trackPanel,BoxLayout.Y_AXIS));
		trackPanel.add(new JLabel("Choisissez le circuit !        "));
		trackPanel.add(menuTrack);
		trackIcon = new JLabel();
		trackPanel.add(new JLabel("     "));
		trackPanel.add(trackIcon);
		menuTrack.setSelectedIndex(1);
		menuTrack.setSelectedIndex(0);
		
		// start button
		buttonOk = new JButton("C'est parti !");
		buttonOk.setMnemonic(10);
		buttonOk.addActionListener(this);
		buttonOk.setPreferredSize(new Dimension(100,60));
		
		// menu button
		buttonMenu = new JButton("Menu principal");
		buttonMenu.setMnemonic(11);
		buttonMenu.addActionListener(this);
		buttonMenu.setPreferredSize(new Dimension(120,30));
		
		
		// add the other components
		racePanel.add(trackPanel);
		racePanel.add(buttonOk);
		racePanel.add(buttonMenu);
		
		// organize the panel's layout
		SpringLayout layout = new SpringLayout();
		Container contentPane = this.getContentPane();
		racePanel.setLayout(layout);
		layout.putConstraint(SpringLayout.WEST , carPanel[0], 10, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, carPanel[0], 10, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST , carPanel[1], 0, SpringLayout.WEST, carPanel[0]);
		layout.putConstraint(SpringLayout.NORTH, carPanel[1], 140, SpringLayout.NORTH, carPanel[0]);
		layout.putConstraint(SpringLayout.WEST , trackPanel, 20, SpringLayout.EAST, carPanel[1]);
		layout.putConstraint(SpringLayout.NORTH, trackPanel, 10, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST, buttonOk, 262, SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, buttonOk, 90, SpringLayout.SOUTH, carPanel[0]);
		layout.putConstraint(SpringLayout.WEST, buttonMenu, 252, SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, buttonMenu, 140, SpringLayout.SOUTH, carPanel[0]);
		
		
		// general configurations
		this.add(racePanel);
		this.setMainMenu();
		ImageIcon ic = new ImageIcon("images/icon.png");
		setIconImage(ic.getImage());						// the windows' icon
		mainPanel.setBackground(Color.BLACK);
		imagePanel.setBackground(Color.BLACK);
		
		// standard definitions
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);

		menuCar[0].setSelectedIndex(1);
		menuCar[0].setSelectedIndex(0);
		menuCar[1].setSelectedIndex(1);
		menuCar[1].setSelectedIndex(0);	
	}
	
	// this function sets the main menu at the frame
	private void setMainMenu() {
		this.remove(racePanel);
		this.add(mainPanel);
		this.setSize(430,380);
		this.repaint();
	}
	
	// sets the race menu depending on the number of players that will play
	private void setRaceMenu(int numPlayers) {
		// disables the player two's menu if the game is for only one player
		if (numPlayers == 1) {
			menuCar[1].setEnabled(false);
		} else {
			menuCar[1].setEnabled(true);
		}
		this.numPlayers = numPlayers;
		this.remove(mainPanel);
		this.add(racePanel);
		this.setSize(420,320);
		this.repaint();
	}

	// shows this frame
	public void showMenu() {
		this.setVisible(true);
	}
	
	// treat the buttons of the menus (main and race)
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		int b = button.getMnemonic();
		
		switch (b) {
			case 0: this.setRaceMenu(1); // one player
				break;
			case 1: this.setRaceMenu(2); // two players
				break;
			case 2: hallFrame.showHall(); // shows hall of fame
				break;
			case 3:	String message = 	"SUPER SPRINT SUPÉLEC\n" +
										"_______________________________________\n" +
										"INFORMATIONS GÉNÉRALES : \n\n" +
										"Projet Logiciel 2014/2015 - Séq. 6\n" +
										"Alexandre LOEBLEIN HEINEN & Gautier SHARPIN\n" +
										"Version de 14.01.14\n" +
										"_______________________________________\n" +
										"COMMANDES : \n\n" +
										"Pour commander les voitures, merci d'utiliser \n" +
										"les flèches directionnelles pour le joueur 1  \n" +
										"et les touches ASDW pour le joueur 2.\n" +
										"_______________________________________\n" +
										"Le nombre de tours est toujours fixé à 3.";
					JOptionPane.showMessageDialog(null, message); // shows some informations about the software
				break;
			case 10: this.setVisible(false); // LET'S THE RACE START! 
					this.setMainMenu();
					int[] models = new int[4];
					int k;
					// sets the selected cars
					for (k = 0; k < numPlayers; k++) {
						models[k] = selCar[k];
					}
					// generate the computer's cars randomly 
					Random random = new Random();
					for (k = numPlayers; k < 4; k++) {
						models[k] = random.nextInt(4) + 1;
					}
			new Game(models, selTrack, numPlayers, nLaps, hall);
				break;
			case 11: this.setMainMenu();
				break;
		}
	}

	public void itemStateChanged(ItemEvent event) {
		@SuppressWarnings("rawtypes")
		JComboBox box = (JComboBox) event.getSource();
		String name = box.getName();
		// identify if the event was generated by the car or the track menu
		if (name.contains("car")) {
			int n;
			// identify which player has chosen their car
			if(name.contains("1")) {
				n = 0; }
			else {
				n = 1; }
			// update all the data: icon, stats and selected car
			int k = box.getSelectedIndex();
			carIcon[n].setIcon(new ImageIcon("images/voiture"+Integer.toString(k+1)+".png"));
			int[] stats = Voiture.donnees[k];
			
			int i;
			for(i = 0; i < 3; i++) {
				carStats[n][i].setValue(stats[i]);
				carStats[n][i].setString(Integer.toString(stats[i]));
			selCar[n] = k + 1;
			}
		} else {
			// update the selected track and its icon
			int k = box.getSelectedIndex();
			selTrack = k + 1;
			trackIcon.setIcon(new ImageIcon("images/mini_circuit"+Integer.toString(k+1)+".png"));
		}
	}

}
