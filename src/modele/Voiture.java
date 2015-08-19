package modele;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.util.Observable;

import vue.*;

public class Voiture extends Observable
{
	// les caracteristiques des modeles sont des donnees de la classe voiture
	public static final int[][] donnees = {{120, 280, 50},{200, 320, 38},{146, 374, 40},{170, 250, 55}};
	public static final int numCars = 4;
	private static final int[] DIM_IMAGE = {38, 22}; // {40, 24};
	private static final double EPS = 0.1;

	private int[] stats; // les 3 caracteristiques d'une voiture
						// acc, vmax, tenue
						// + peut etre le poid de la voiture

	// physique
	private float[] position; // x, y
	private float angle;
	private float vitesse;
	private float acceleration;
	private Circuit circuit;

	private int status; // 0 - arretee, 1 - en acceleration, 2 - deceleration
	private int index; // l'index c'est la position de la voiture dans la liste, c'est-e-dire quel est son sprite
	private String nom;
	private int comptTour; // compteur de tours

	private boolean flagLin;


	public Voiture(int modele, int classement, String nom, GameFrame frame, int player, Circuit circuit)
	{
		// idee: definir pour la classe voiture deux tableaux
		// un pour les caracteristiques qu'ondefinit selon le modele de la voiture
		// un pour les positions initiales dans le "grid" initial, qui vont varier avec le circuit
		super(); // classe Observable
		this.circuit = circuit;

		this.nom = nom;
		stats = donnees[modele-1];
		try {
			position = Circuit.posIni[frame.getPiste() - 1][classement - 1].clone();
		} catch(Exception ex) {
			System.out.println(ex.toString());
			System.out.println("Erreur : le numero maximum de voiture est 4!");
		}
		angle = (float) (-Math.PI/2);
		vitesse = 0;
		acceleration = 0;

		// definit comme Observer le GameFrame
		addObserver(frame);

		// le status commence avec 0, arretee
		status = 0;

		// l'index c'est la position de la voiture dans la liste, c'est-e-dire quel est son sprite
		index = classement - 1;

		comptTour = 0;
		flagLin = false;
	}

	// *** FONCTIONS DE GET **************
	public int getX() {
		return Math.round(position[0]); }

	public int getY() {
		return Math.round(position[1]); }

	public float getAngle() {
		return angle; }

	public int getIndex() {
		return index; }

	public float getVitesse() {
		return vitesse; }

	public String getNom() {
		return nom;	}

	public boolean getFlagLin() {
		return flagLin;
	}
	public int getTours() {
		return comptTour;
	}
	public int getStat(int i) {
		return stats[i];
	}
	// ************************************

	// *** FONCTIONS DE SET ***************
	public void setVitesse(float d) {
		this.vitesse = d; }

	public void setAngle(float a) {
		this.angle = a;	}

	public void deplacer(float x, float y) {
		position[0] += x;
		position[1] += y;
	}

	public void setFlagLin () {
		flagLin = !flagLin;
	}
	// ************************************

	public void commander(int fleche)
	{
		double v = .002;

		switch(fleche) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				if(vitesse != 0.0)
					angle -= stats[2]*v;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				if(vitesse != 0.0)
					angle += stats[2]*v;
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				acceleration = stats[0];
				status = 1;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				acceleration = -stats[0];
				status = 1;
				break;
		}
		setChanged();
	}

	// c'est le cas oe aucune fleche n'est appuyee
	public void accNulle() {
		acceleration = 0;
		status = 2;
	}

	// calculer la physique, d est l'intervalle d'actualisation, d'echantillonage
	public void physique(double d)
	{
		float thres = (float) 11.0;									// threshold

		// non linearites
		if(status == 2) {
			if(vitesse < thres && vitesse > -thres) {					// pour eviter certaines erreurs d'arrondi
				acceleration = 0;
				vitesse = 0;
				status = 0; 											// la voiture s'arrete completement
			} else {
				acceleration = (float) (-0.5*stats[0]*Math.signum(vitesse)); // modelisation du frottement
			}
		}

		if(Math.abs(vitesse) >= stats[1]) {								// v < vmax, toujours
			vitesse = Math.signum(vitesse)*stats[1]; }

		// equations basiques de physique
		position[0] += vitesse*Math.cos(angle)*d;
		position[1] += vitesse*Math.sin(angle)*d;
		vitesse += acceleration*d;

		// etat de la voiture
		comptTour += circuit.crossLine(this);

		// mises e jour, soit pour les observers, soit pour les onglets
		setChanged();
		notifyObservers();
	}

	//fonction de collision

	public void collide(Voiture voiture) {

		Shape shape1 = new Rectangle(this.getX(), this.getY(), DIM_IMAGE[0], DIM_IMAGE[1]);
		Shape shape2 = new Rectangle(voiture.getX(), voiture.getY(), DIM_IMAGE[0], DIM_IMAGE[1]);

		AffineTransform at1 = new AffineTransform();
		AffineTransform at2 = new AffineTransform();

		at1.rotate(this.getAngle()   , this.getX()   , this.getY()   );
		at2.rotate(voiture.getAngle(), voiture.getX(), voiture.getY());

		shape1 = at1.createTransformedShape(shape1);
		shape2 = at1.createTransformedShape(shape2);

		if(shape1.intersects(shape2.getBounds2D()) || shape2.intersects(shape1.getBounds2D())){
			float deltaAng = this.angle-voiture.getAngle();
			float deltaA = (float) (0.01*deltaAng);
			this.angle = this.angle - deltaA;
			voiture.setAngle(voiture.getAngle() + deltaA);
			float aux = this.vitesse;
			this.vitesse = (float) ((1-EPS)*this.vitesse + Math.cos(deltaAng)*EPS*voiture.getVitesse());
			voiture.setVitesse((float) ((1-EPS)*voiture.getVitesse() + Math.cos(deltaAng)*EPS*aux));
			int deltaX = this.getX() - voiture.getX();
			deltaX = (int) Math.signum(deltaX);
			int deltaY = this.getY() - voiture.getY();
			deltaY = (int) Math.signum(deltaY);
			this.deplacer(deltaX, deltaY);
			voiture.deplacer(-deltaX, -deltaY);
		}

	}

}
