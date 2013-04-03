package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import game.Game;
import game.Player;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ActionWindow extends JFrame implements ActionListener, MouseListener, MessageUI {

	//---- Game related variables ----------------------
	
		private Game game;
		private String name;
		private Player player;
		private Client client;
		
	// Windows and Panels
		
		private Container c;
		private JPanel menu = new JPanel();
		private JPanel turndisp = new JPanel();
		private JPanel chatbox = new JPanel();

		private GamePanel gamePanel;
	
	//---- Constructor ---------------------------------
		
	public ActionWindow(final Game g, final Player p, final Client client) {
		super("Lord of the RINGGZ");
		
		this.client = client;
		this.game = g;
		//this.name = n;
		this.player = p;
		client.setMUI(this);
		c = getContentPane();
		buildGUI();

		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        }
    );
				
		pack();
		validate();
		setVisible(true);
	}

	/**
	 * Creates the main GUI components.
	 */
	private void buildGUI() {
		
		//---- Main window ----
				setPreferredSize(new Dimension(500, 650));
				setMinimumSize(new Dimension(500, 650));
		
		buildGame();
		// TODO Auto-generated constructor stub
		
		//---- Defines Border styles
				Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);
				Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}
	/**
	 * Constructs the Game panel where the player
	 * can play on.
	 */
	protected void buildGame() {
		//---- Loading the Game ----
		//setGame(g);
		//---- Game Panel -----
				gamePanel = new GamePanel(game, player);
				
				gamePanel.setPreferredSize(new Dimension(500, 600));
				gamePanel.addMouseListener(this);
				
				c.add(gamePanel, BorderLayout.LINE_START);
				
				gamePanel.setVisible(true);
	}
	
	/**
	 * Updates the entire ActionWindow to reflect
	 * any changes that may have occured.
	 */
	void updateAW() {
		repaint();
	}
	
	@Override
	public void addMessage(final String name, final String msg) {
		//taMessages.append("<" + name + "> " + msg +"\n");

	}
	
	//---- Action Events ------------------------
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		gamePanel.repaint();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getX());
		int x = e.getX();
		int y = e.getY();
		//System.out.println(gamePanel.findComponentAt(x, y));
		System.out.println("Component: " + e.getComponent());
		//System.out.println("Source: " + e.getSource());
		repaint();
		gamePanel.repaint();
		
		//((CellPanel.)
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
