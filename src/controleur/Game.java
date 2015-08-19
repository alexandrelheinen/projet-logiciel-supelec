package controleur;

import java.util.Timer;

import javax.swing.JOptionPane;

import modele.*;
import vue.*;

public class Game {

	public static final int TEMPS_ACT = 10; // temps d'actualisation

	private HallOfFame hall;
	private GameFrame game;
	private Controleur[] controleur;
	private Timer chrono;
	private Circuit circuit;
	private int[][] map;
	private int numPlayers;
	private int numTours;
	private int numCircuit;
	private boolean running;

	public static int[][][] MAPS = {{{4,2,2,3},{1,7,7,1},{5,2,2,6}},
																	{{4,3,7,7},{1,5,2,3},{5,2,2,6}},
																	{{4,2,2,3},{1,7,4,6},{5,2,6,7}},
																	{{4,3,4,3},{1,5,6,1},{5,2,2,6}}}; // on change apres, il faut definir e partir de "partie" quelle est la valeur de "map"

	public Game (int[] modeles, int piste, int num, int numTours, HallOfFame hall) {
		Controleur.resetPlayers();
		this.numCircuit = piste;
		map = Game.MAPS[piste - 1];
		this.numTours = numTours;
		this.hall = hall;
		numPlayers = num;

		game = new GameFrame("Super Sprint Supelec", modeles, map, piste);
		controleur = new Controleur[modeles.length];
		circuit = new Circuit(game, map);
		circuit.startLine(piste);

		// le on cree "num" voitures controlees par le clavier ; les autres sont controlees par l'intelligence artificielle
		int k = 0;
		while(k < modeles.length) {
			if (k < num) {
				controleur[k] = new Humain(modeles[k], k+1, k+1, game, circuit); }
			else {
				controleur[k] = new Intelligence(modeles[k], k+1, game, circuit);
			}
			k++;
		}

		// c'est l'objet qui va controler le temps du jeu
		chrono = new Timer();
		chrono.scheduleAtFixedRate(new Temps(controleur, circuit, this), (long) 5*TEMPS_ACT, TEMPS_ACT);
		running = true;
	}

	public void isFinished() {
		int c = 0;
		while(c < controleur.length) {
			if (controleur[c].getVoiture().getTours() <= numTours) {
				c++;
			} else {
				if (running) {
					finishGame(c);
				}
			}
		}
	}

	private void finishGame(int c) {
		running = false;
		chrono.cancel();
		chrono.purge();
		double temps = circuit.getTemps();
		if (c < numPlayers) {
			hall.testResult(temps, numCircuit-1);
		} else {
			// si l'ordinateur gagne, ea ne sera jamais considere
			hall.testResult(temps*1000.0, numCircuit-1);
			JOptionPane.showMessageDialog(null, "L'ordinateur a gagne.");
		}
		game.setVisible(false);
		game.dispose();
	}
}
