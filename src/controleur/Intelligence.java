package controleur;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import modele.*;
import vue.GameFrame;

// on n'a rien
public class Intelligence extends Controleur {
	
	private Circuit circuit;
	private int[] coordOld;
	private int[] coord;
	private double r, eOld;
	
	public Intelligence (int modele, int posIni, GameFrame frame, Circuit circuit) {
		super(modele, posIni, frame, circuit);
		this.circuit = circuit;
		coord = circuit.getCoordonates(voiture);
		coordOld = coord.clone();
		coordOld[0] += 1;
		r = 0;
		eOld = 0;
	}
	
	private double normalize(double e) {
		while (e <= - Math.PI) {
            e += 2*Math.PI;
        }
        while (e > Math.PI) {
            e -= 2*Math.PI;
        }
        return e;
	}
	
	private void command() {
		// les parametres
		double kP = 0.0007*voiture.getStat(2);
		double kD = -30/voiture.getStat(2);
 		double y = voiture.getAngle();
 		double e = normalize(r - y);
        double u = kP*e + kD*(e - eOld);
        // saturation
        if (u > .002*voiture.getStat(2)) {
        	u = .002*voiture.getStat(2);
        }
		// correcteur proportionnel/derivatif
		voiture.setAngle(voiture.getAngle() + (float) u);
		eOld = e;
	}

	public void mettreAJour() {
		setReference(); 
		this.command();
		voiture.commander(KeyEvent.VK_UP);
		super.mettreAJour();
	}
	
	private void setReference() {
		int[] pos = circuit.getCoordonates(voiture).clone();
		if (pos[0] != coord[0] || pos[1] != coord[1]) {
			coordOld = coord.clone();
			coord = pos;
		}
		r = 0;
 		switch(circuit.getPiece(voiture)) {
		case 1: if(coordOld[0] == coord[0] + 1) {
					r = -Math.PI/2;
				} else if(coordOld[0] == coord[0] - 1) {
					r = Math.PI/2;
				}
			break;
		case 2: if(coordOld[1] == coord[1] - 1) {
					r = 0;
				} else if(coordOld[1] == coord[1] + 1) {
					r = Math.PI;
				}
			break;
		case 3: if(coordOld[1] == coord[1] - 1) {
					r = Math.PI/2;
				} else if(coordOld[0] == coord[0] + 1) {
					r = Math.PI;
				}
			break;
		case 4: if(coordOld[0] == coord[0] + 1) {
					r = 0;
				} else if(coordOld[1] == coord[1] + 1) {
					r = Math.PI/2;
				}
			break;
		case 5: if(coordOld[0] == coord[0] - 1) {
					r = 0;
				} else if(coordOld[1] == coord[1] + 1) {
					r = -Math.PI/2 + 2*Math.PI;
				}
			break;
		case 6: if(coordOld[0] == coord[0] - 1) {
					r = Math.PI;
				} else if(coordOld[1] == coord[1] - 1) {
					r = -Math.PI/2;
				}
			break;
		case 7: 
			break;
		}
	}
}
	
