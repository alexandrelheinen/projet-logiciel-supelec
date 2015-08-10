package controleur;

import vue.GameFrame;
import modele.*;

public abstract class Controleur {

	private static int comptPlayer = 0;
	protected Voiture voiture;	// quelle est la voiture que le controleur contrôle
	protected GameFrame frame;
	
	public Controleur(int modele, int posIni, GameFrame frame, Circuit circuit) {
		comptPlayer++;
		String nom;
		if (this.toString().contains("Humain")) {
			nom = Integer.toString(comptPlayer);
		} else {
			nom = "C";
		}
		voiture = new Voiture(modele, posIni, nom, frame, comptPlayer, circuit);
		System.out.println("Voiture créée : Modèle "+Integer.toString(modele)+"; Position initiale "+Integer.toString(posIni));
		System.out.println(this.getClass());
		System.out.println(" ------------- ");
		this.frame = frame;
	}

	public Voiture getVoiture() {
		return voiture;
	}
	
	public static int getNumPlayers() {
		return comptPlayer;
	}
	
	public static void resetPlayers() {
		comptPlayer = 0;
	}
	
	// mettre à jour la voiture, en appliquant la physique
	public void mettreAJour() {
		voiture.physique(Game.TEMPS_ACT/1000.0);
	}
}
