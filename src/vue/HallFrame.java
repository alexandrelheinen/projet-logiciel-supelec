package vue;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import modele.*;

public class HallFrame extends JFrame implements Observer, ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private JComboBox menuHall;
	private JTable results; // la table des résultats
	private MenuFrame menu;
	HallOfFame hall;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HallFrame(MenuFrame menu) {
		super("Hall Of Fame");
		this.menu = menu;
		hall = null;

		// les composants
		JPanel panel = new JPanel();
		String[] stringTrack = new String[Circuit.numCirc];
		int m;
		for(m = 0; m < Circuit.numCirc; m++) {
			stringTrack[m] = "Circuit "+Integer.toString(m+1);
		}
		
		// les entrées du tableau
		String[][] data = new String[HallOfFame.numResults][4];
		String[][] tt = {{"Position", "Nom", "Temps", "Date"}};
		String[] t = {"", "", "", ""};
		
		JTable title = new JTable(tt, t);
		results = new JTable(data, t);
		title.setPreferredSize(new Dimension(400,17));
		title.setEnabled(false);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 12));
		results.setPreferredSize(new Dimension(400,160));
		results.setEnabled(false);
		
		menuHall = new JComboBox(stringTrack);
		menuHall.addItemListener(this);
		menuHall.setSelectedIndex(1);
		menuHall.setSelectedIndex(0);
		
		JButton closeButton = new JButton("Fermer");
		
		closeButton.addActionListener(this);
		
		SpringLayout layout = new SpringLayout();
		Container contentPane = this.getContentPane();
		panel.setLayout(layout);
		layout.putConstraint(SpringLayout.WEST , menuHall, 10, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, menuHall, 10, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST , title, 10, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, title, 8, SpringLayout.SOUTH, menuHall);
		layout.putConstraint(SpringLayout.WEST , results, 10, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, results, 20, SpringLayout.NORTH, title);
		layout.putConstraint(SpringLayout.EAST , closeButton, (int) title.getPreferredSize().getWidth(), SpringLayout.WEST, title);
		layout.putConstraint(SpringLayout.NORTH, closeButton, 10, SpringLayout.SOUTH, contentPane);
		
		panel.add(menuHall);
		panel.add(title);
		panel.add(results);
		panel.add(closeButton);
		this.add(panel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setSize((int) results.getPreferredSize().getWidth() + 25, 265);
		setVisible(false);
		setResizable(false);
	}
	
	// cette fonction met à jour les données du Hall of Fame
	public void update(Observable obs, Object obj) {
		int k;
		Result r = null;
		hall = (HallOfFame) obs;
		int j = hall.getLastChange();
		menuHall.setSelectedIndex(j);
		for (k = 0; k < HallOfFame.numResults; k++) {
			r = hall.getResult(j, k);
			results.setValueAt(Integer.toString(k+1), k, 0);
			try {
				results.setValueAt(r.getName(), k, 1);
				results.setValueAt(Double.toString(r.getTime()/1000.0)+" s", k, 2);
				results.setValueAt(r.getDate(), k, 3);
			} catch (Exception ex) {
				results.setValueAt("-", k, 1);
				results.setValueAt("-", k, 2);
				results.setValueAt("-", k, 3);
			}
		}
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent arg0) {
		this.setVisible(false);
		menu.showMenu();
	}
	
	public void showHall() {
		this.setVisible(true);
	}
	
	public void hideHall() {
		this.setVisible(false);
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		@SuppressWarnings("rawtypes")
		JComboBox box = (JComboBox) event.getSource();
		int index = box.getSelectedIndex();
		int k;
		Result r;
		for (k = 0; k < HallOfFame.numResults; k++) {
			results.setValueAt(Integer.toString(k+1), k, 0);
			try {
				r = hall.getResult(index, k);
				results.setValueAt(r.getName(), k, 1);
				results.setValueAt(Double.toString(r.getTime()/1000.0)+" s", k, 2);
				results.setValueAt(r.getDate(), k, 3);
			} catch (Exception ex) {
				results.setValueAt("-", k, 1);
				results.setValueAt("-", k, 2);
				results.setValueAt("-", k, 3);
			}
		}
	}
}
