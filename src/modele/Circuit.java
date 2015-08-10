package modele;

import java.awt.geom.Line2D;
import java.util.Observable;

import controleur.Game;
import vue.GameFrame;

public class Circuit extends Observable {
	
	public static int numCirc = 4;
	public static final float posIni[][][] = {	{{147, 290},{75, 340},{147, 390},{75, 440}},
												{{147, 290},{75, 340},{147, 390},{75, 440}},
												{{147, 290},{75, 340},{147, 390},{75, 440}},
												{{147, 290},{75, 340},{147, 390},{75, 440}}}; // p. ex.: posIni[n][m][0] donne la coordonnée x de la m-ème voiture dans le circuit n
	private static final int RI = 26;
	private static final int RE = 191;
	
	private double tempsDeCourse;
	private int[][] map;
	private int[] dimMap;
	private int[] dimFrame;
	private Line2D finishLine;
	
	public Circuit (GameFrame g, int[][] map) {
		this.map = map;
		this.addObserver(g);
		tempsDeCourse = 0.00;
		dimMap = new int[2];
		dimFrame = new int[2];
		dimMap[0] = map.length;
		dimMap[1] = map[0].length;
		System.out.println("linhas: "+dimMap[0]+", colunas:"+dimMap[1]);
		dimFrame[0] = GameFrame.dimImage*dimMap[1];
		dimFrame[1] = GameFrame.dimImage*dimMap[0];
		System.out.println("largura: "+dimFrame[0]+", altura:"+dimFrame[1]);
	}
	
	public void startLine(int piste) {
		finishLine = new Line2D.Float(Circuit.posIni[piste - 1][0][0] - 122, Circuit.posIni[piste - 1][0][1] - 50, Circuit.posIni[piste - 1][0][0] + 43, Circuit.posIni[piste - 1][0][1] - 50);
	}
	
	// retourne 0 si la voiture ne franchit pas la ligne, 1 si son sens de franchissement est le bon, -1 sinon
	public int crossLine(Voiture v) {
		int r = 0;
		if(v.getFlagLin()) {
			if (finishLine.ptLineDist(v.getX(), v.getY()) > 3) {
				v.setFlagLin();
			}
		} else {
			if (finishLine.ptLineDist(v.getX(), v.getY()) < 3 && finishLine.getP1().distance(v.getX(), v.getY()) < GameFrame.dimImage) {
				r = (int) -Math.signum(v.getVitesse()*Math.sin(v.getAngle()));
				v.setFlagLin();
			}
		}
		
		return r;
	}
	
	public void mettreAJour() {
		tempsDeCourse += Game.TEMPS_ACT;
		this.setChanged();
		this.notifyObservers();
	}
	
	public double getTemps() {
		return tempsDeCourse;
	}
	
	// sortie = 1, si la voiture est à l'interieur du circuit
	public void testerLimites (Voiture voiture) {
		
		boolean sortie = true;
		
		int[] posV = getCoordonates(voiture);
		
		try {
			int t = map[posV[0]][posV[1]];

			float[] pos = {voiture.getX() - (float) posV[1]*GameFrame.dimImage, voiture.getY() - (float) posV[0]*GameFrame.dimImage};
			
			switch(t) {
			case 1: if(pos[0] <= Circuit.RI || pos[0] >= Circuit.RE) {
					sortie = false;
				}
				break;
			case 2: if(pos[1] <= Circuit.RI || pos[1] >= Circuit.RE) {
					sortie = false;
				}
				break;
			case 3: 
				sortie = testerCercle(pos[0], pos[1], 0, GameFrame.dimImage);
				break;
			case 4: 
				sortie = testerCercle(pos[0], pos[1], GameFrame.dimImage, GameFrame.dimImage);
				break;
			case 5: 
				sortie = testerCercle(pos[0], pos[1], GameFrame.dimImage, 0);
				break;
			case 6: 
				sortie = testerCercle(pos[0], pos[1], 0, 0);
				break;
			case 7: 
				break;
			}

			if (!sortie) {
				voiture.setVitesse((float) -0.2*(voiture.getVitesse()));
				voiture.deplacer((float) -1.0*Math.signum(pos[0] - GameFrame.dimImage/2), (float) -1.0*Math.signum(pos[1] - GameFrame.dimImage/2));
			}
		} catch (Exception ex) {
			System.out.println("La voiture est sortie de la fenêtre!");
			System.out.println(ex.getMessage());
			System.out.println("==============");
		}
	}
	
	public Line2D getLine() {
		return finishLine;
	}
	
	private boolean testerCercle(float x, float y, int x0, int y0) {
		boolean sortie = true;
		double r = Math.sqrt(Math.pow(x - x0, 2) + Math.pow(y - y0, 2));
		if(r <= Circuit.RI || r >= Circuit.RE) {
			sortie = false;
		}
		return sortie;
	}
	
	public int getPiece(Voiture voiture) {
		// ce sont les coordonnées de la voiture pour les composants du circuit
		int[] posV = getCoordonates(voiture);
		return map[posV[0]][posV[1]];
	}
	
	public int[] getCoordonates(Voiture voiture) {
		// ce sont les coordonnées de la voiture pour les composants du circuit
		int[] posV = new int[2];
		posV[0] = (int) (1.0*dimMap[0]*voiture.getY()/dimFrame[1]);
		posV[1] = (int) (1.0*dimMap[1]*voiture.getX()/dimFrame[0]);
		return posV.clone();
	}
}
