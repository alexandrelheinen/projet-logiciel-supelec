package controleur;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import vue.GameFrame;
import modele.*;

public class Humain extends Controleur implements KeyListener {

	private int player;

	public Humain(int modele, int posIni, int player, GameFrame frame, Circuit circuit) {
		super(modele, posIni, frame, circuit);
		this.player = player;
		frame.addKeyListener(this);
	}
	
	public int getPlayer () {
		return player;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		int fleche = arg0.getKeyCode();
		if (player == 1) {
			switch(fleche) {
				case KeyEvent.VK_DOWN :
				case KeyEvent.VK_UP :
				case KeyEvent.VK_RIGHT :
				case KeyEvent.VK_LEFT : voiture.commander(fleche);
										break;
			}
		} else if (player == 2) {
			switch(fleche) {
			case KeyEvent.VK_S :
			case KeyEvent.VK_W :
			case KeyEvent.VK_D :
			case KeyEvent.VK_A : voiture.commander(fleche);
									break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int fleche = arg0.getKeyCode();
		if (player == 1) {
			switch(fleche) {
				case KeyEvent.VK_DOWN :
				case KeyEvent.VK_UP : voiture.accNulle();
										break;
			}
		} else if(player == 2) {
			switch(fleche) {
			case KeyEvent.VK_S :
			case KeyEvent.VK_W : voiture.accNulle();
									break;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
