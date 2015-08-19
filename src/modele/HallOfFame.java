package modele;

import java.io.*;
import java.util.Observable;

import javax.swing.JOptionPane;

import vue.*;

public class HallOfFame extends Observable {

	public static final int numResults = 10;

	private Result[][] results;
	private int lastChange;

	public HallOfFame(HallFrame frame) throws FileNotFoundException {
		results = new Result[Circuit.numCirc][numResults];
		// charge le Hall of Fame memorise
		try {
			FileInputStream file = new FileInputStream("halloffame.dat");
			ObjectInputStream input = new ObjectInputStream(file);
			int m,n;
			for (m = 0; m < Circuit.numCirc; m++) {
				for (n = 0; n < numResults; n++) {
					results[m][n] = (Result) input.readObject();
				}
			}
			file.close();
			input.close();
		} catch (Exception ex) {
			// si impossibilite d'ouvrir le fichier, on cree un nouveau Hall of Fame
			JOptionPane.showMessageDialog(null, ex.getMessage()+"\nUn nouveau Hall of Fame va etre cree.");
			setNewHall();
		}

		// sets
		this.addObserver(frame);

		// first update of the table
		lastChange = 0;
		this.setChanged();
		this.notifyObservers();
		frame.hideHall();

	}

	private void setNewHall() {
		String[] noms = {"Paul", "Alexandre", "Chloe", "Nathan", "Raphael", "Louise", "Arthur", "Emma", "Jules", "Amelie"};
		int i, j;
		for(i = 0; i < Circuit.numCirc; i++) {
			for(j = 0; j < numResults; j++) {
				results[i][j] = new Result(noms[j], 30000+1000*j);
			}
		}
		try{
            FileOutputStream file = new FileOutputStream("halloffame.dat");
            ObjectOutputStream output = new ObjectOutputStream(file);
			int m,n;
			for (m = 0; m < Circuit.numCirc; m++) {
				for (n = 0; n < numResults; n++) {
					output.writeObject(results[m][n]);
				}
			}
            output.close();
            file.close();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
	}

	public Result getResult(int circ, int pos) {
		return results[circ][pos];
	}

	// teste si le resultat peut entrer dans le hall of fame
	public void testResult(double temps, int circ) {
		int k;
		double test;
		int num = numResults;
		for (k = numResults - 1; k >= 0; k--) {
			try
			{
				test = results[circ][k].getTime();
			} catch (Exception e) {
				test = 10E10;
			}
			if (temps < test) {
				num = k;
			}
		}
		if (num < numResults) {
			addResult(num, temps, circ);
		} else {
			this.setChanged();
			this.notifyObservers();
		}
	}

	public int getLastChange() {
		return lastChange;
	}

	// add the result of time t at position num of the track circ's hall of fame
	private void addResult(int num, double t, int circ) {
		int k;
		String message = "Nouveau classement au Hall of Fame!\n#"+Integer.toString(num+1)+" - Circuit "+Integer.toString(circ+1)+"\nInserez le nom du joueur :";
		String name = JOptionPane.showInputDialog(message, "Alexandre LOEBLEIN HEINEN");
		for (k = numResults - 1; k > num; k--) {
			results[circ][k] = results[circ][k - 1];
		}
		lastChange = circ;
		results[circ][num] = new Result(name, t);

		// stores new results in file
		try{
            FileOutputStream file = new FileOutputStream("halloffame.dat");
            ObjectOutputStream output = new ObjectOutputStream(file);
			int m,n;
			for (m = 0; m < Circuit.numCirc; m++) {
				for (n = 0; n < numResults; n++) {
					output.writeObject(results[m][n]);
				}
			}
            output.close();
            file.close();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

		// shows the hall of fame
		this.setChanged();
		this.notifyObservers();
	}

}
