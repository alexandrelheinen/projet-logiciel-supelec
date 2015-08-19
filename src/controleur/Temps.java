package controleur;

import java.util.TimerTask;

import modele.Circuit;

public class Temps extends TimerTask {

	private Controleur[] controle;
	private Circuit circuit;
	private Game game;

	public Temps(Controleur[] c, Circuit cc, Game g)
	{
		controle = c;
		circuit = cc;
		game = g;
		System.out.println("La course a commance avec " + controle.length + " voitures.");
		System.out.println(" ------------- ");
	}

	// a chaque instant, on doit actualiser tous les controleurs
	@Override
	public void run() {
		circuit.mettreAJour();
		game.isFinished();
		int k, l;
		for (k = 0; k < controle.length; k++) {
			circuit.testerLimites(controle[k].getVoiture());
			controle[k].mettreAJour();
			for (l = 0; l < k; l ++) {
				controle[k].getVoiture().collide(controle[l].getVoiture());
			}
		}
	}
}
