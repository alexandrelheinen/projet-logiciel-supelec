package vue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import modele.Circuit;
import modele.Voiture;

public class GameFrame extends JFrame implements Observer {
	
	public static final int dimImage = 219;
	public static final int[] DIMCAR = {40, 29};
	private static final long serialVersionUID = 1L;
	private BufferedImage[] images;
	private BufferedImage[][] background;
	private BufferedImage texture;
	private int[][] map; // quel est le "level" auquel on joue
	private boolean[] flags;
	private int piste;
	private int[] dim;
	private Color colorFont;
	private Font fontText;
	
	public GameFrame(String s, int[] modeles, int[][] circuit, int num) {
		super(s);
		
		int k = 0;
		// identification du niveau actuel
		this.map = circuit;
		this.piste = num;
		dim = new int[2];
		dim[0] = map.length;
		dim[1] = map[0].length;
		
		flags = new boolean[modeles.length + 1];
		for (k = 0; k < flags.length; k++) {
			flags[k] = false;
		}
		
		// icone de la fenêtre *****************************************************
		ImageIcon ic = new ImageIcon("images/icon.png");
		setIconImage(ic.getImage());
		
		// chargement des images des voitures ************************************
		images = new BufferedImage[modeles.length];
		
		for (k = 0; k < modeles.length; k++) {
			try {
				images[k] = ImageIO.read(new File("images/voiture"+Integer.toString(modeles[k])+".png"));
				System.out.println("Sprite #"+Integer.toString(k+1)+" chargé");
			} catch (Exception e) {
				System.out.println("Erreur en chargeant les images .. "+e.getMessage());
			}
		}
		System.out.println(" **************** \n");
		// *************************************************************
		
		// Chargement de l'image du circuit, 
		background = gererCircuit(map);
		
		// image de fond (arriere-plan)
		try {
			texture = ImageIO.read(new File("images/texture.png"));
		} catch (Exception e) {
			System.out.println("Erreur en chargeant la texture .. "+e.getMessage());
		}
		
		colorFont = new Color(0,90,180);
		fontText = new Font ("Segoe UI", Font.BOLD, 20);
		
		// informations de la fenetre
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setSize(20 + dim[1]*dimImage, 50 + 20 + dim[0]*dimImage);
		setVisible(true);
		setResizable(false);
		this.setBackground(new Color(222,222,222));
		
		// création du buffer
		createBufferStrategy(2);
	}

	public int getPiste() {
		return piste; }
	
	public int[][] getMap() {
		return map; }
	
	@Override
	public void update(Observable obs, Object obj) {
		String nom = obs.toString();
		
		// variables d'image
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;
		Graphics2D g2 = null;
		
		// si l'objet qui a été actualisé est une voiture...
		if (nom.contains("Voiture")) {			
			try {
				Voiture voiture = (Voiture) obs;					// on recupere l'objet qui a géré l'interruption
				BufferedImage image = images[voiture.getIndex()];
				g = bf.getDrawGraphics(); 
				g2 = (Graphics2D) g;								// les graphiques et l'image
				
				AffineTransform transform = new AffineTransform();
				transform.rotate(voiture.getAngle(), image.getWidth()/2, image.getHeight()/2);
				AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);		// la rotation de la voiture
				g2.drawImage(image, op, voiture.getX() - DIMCAR[0]/2 , voiture.getY() - DIMCAR[1]/2);			// l'impression avec translation
				g2.drawString(voiture.getNom(), voiture.getX() - DIMCAR[0]/2, voiture.getY() - DIMCAR[1]/2);
				
				g2.setFont(fontText);
				g2.setColor(colorFont);
				g2.drawString("[P"+voiture.getNom()+": "+Integer.toString(voiture.getTours())+"]",2*dimImage + 100*voiture.getIndex(),dim[0]*dimImage + 50);
				
				flags[voiture.getIndex()] = true;					// cette voiture a été actualisée
			} finally {
				g.dispose();
			}
		} else { // c'est le cas où l'objet actualisé est le circuit
			try {
				Circuit circuit = (Circuit) obs;
				g = bf.getDrawGraphics();
				g2 = (Graphics2D) g;
				AffineTransformOp op = new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_BILINEAR);
				g.drawImage(texture, 0, 0, null); // dessiner l'image de fond
				
				// dessiner chaque composant du circuit
				int a, b;
				for (b = 0; b < dim[1]; b++) {
					for(a = 0; a < dim[0]; a++) {
						g2.drawImage(background[a][b], op, 0 + b*dimImage, 10 + a*dimImage);
					}
				}

				g2.setColor(new Color(255,255,255));
				for (a = 0; a < 4; a++) {
					g2.drawLine((int) Circuit.posIni[piste - 1][a][0] - 20, (int) Circuit.posIni[piste - 1][a][1] - 20, (int) Circuit.posIni[piste - 1][a][0] + 17, (int) Circuit.posIni[piste - 1][a][1] - 20);
				} // dessine les lignes des positions initiales
				g2.setColor(new Color(255,255,0));
				g2.drawLine((int) circuit.getLine().getX1(), (int) circuit.getLine().getY1(), (int) circuit.getLine().getX2(), (int) circuit.getLine().getY2());
				// la ligne d'arrivée
				g2.setFont(new Font ("Segoe UI", Font.BOLD, 20));
				g2.setColor(new Color(0,90,180));
				g2.drawString("Temps de Course : " + Integer.toString((int) circuit.getTemps()/1000) + " s", 20, dim[0]*dimImage + 50);
				// le temps de jeu
				flags[flags.length - 1] = true;							// ce circuit a été actualisé (meme chose que pour les voitures)
			} finally {
				g.dispose();
			}
		}
		
		if (testArray(flags)) {						// si tous les elements graphiques importants on été imprimés
			bf.show();
			Toolkit.getDefaultToolkit().sync();
			resetArray(flags); }
	}
	
	// *** fonctions auxiliaires ***********************
	public static boolean testArray(boolean[] array) {
	// teste si tous les elements du array sont true
		int k = 0;
		for (k = 0; k < array.length; k++) {
			if (!array[k]) {
				return false; }
		}
		return true;
	}
	
	private void resetArray(boolean[] array) {
	// met tous les elements comme false
		int k = 0;
		for (k = 0; k < array.length; k++) {
			array[k] = false;
		}
	}
	// **************************************************
	
	private BufferedImage[][] gererCircuit (int[][] map) {
		BufferedImage[][] buf = new BufferedImage[dim[0]][dim[1]];
		int a; int b;
		for (b = 0; b < dim[1]; b++) {
			for(a = 0; a < dim[0]; a++) {
				try {
					buf[a][b] = ImageIO.read(new File("images/track"+Integer.toString(map[a][b])+".png"));
				} catch (Exception e) {
					System.out.println("Erreur en chargeant les images du circuit .. "+e.getMessage());
				}
			}
		}
		return buf;
	}
}
