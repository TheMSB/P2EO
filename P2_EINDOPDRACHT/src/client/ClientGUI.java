package client;

import game.Board;
import game.Cell;
import game.Game;
import game.Piece;
import game.Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import server.Server;


/**
 * The Client Graphical User Interface for use with
 * our RINGZ game.
 * @author martijnbruning
 *
 */
public class ClientGUI {

	//CHECKSTYLE:OFF
	//---- Constants -----------------------------------
	Dimension settingA = new Dimension (800, 600);
	//---- Instance Variables --------------------------

	//---- Game related variables ----------------------
	private Game game;
	private static String name;
	private static int pnumber;
	// Windows and Panels
	private JFrame window = new JFrame("RINGZ");
	private JPanel menu = new JPanel();
	private JPanel turndisp = new JPanel();
	private JPanel chatbox = new JPanel();

	private JPanel board = new JPanel();
	private JPanel inventory = new JPanel();
	
	private JTextField	myMessage;
    private JTextArea   taMessages;
    private Server      server;
	//private JPanel cells[] = new JPanel[25];
	private Cell[][] cells;
	//CHECKSTYLE:ON

	//---- Constructor ---------------------------------

	public ClientGUI(Game game) {
		this.game = game;
		cells = game.getBoard().getCells();

		//---- Create a new application window
//TODO resolution support implementen
		window.setPreferredSize(new Dimension(600, 600));
		window.setMinimumSize(new Dimension(600, 600));
		board.setPreferredSize(new Dimension(400, 500));
		board.setMinimumSize(new Dimension(400, 500));
		inventory.setPreferredSize(new Dimension(200, 200));
		inventory.setMinimumSize(new Dimension(200, 200));

		//---- Defines Border styles
		Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		//---- Separate definition for the menu background

		menu.setOpaque(true);
		menu.setBackground(new Color(154, 165, 127));
		menu.setPreferredSize(new Dimension(200, 320));
		menu.setBorder(paneEdge);

		//---- sets up the turn display

		turndisp.setOpaque(true);
		turndisp.setBackground(new Color(248, 213, 131));
		turndisp.setPreferredSize(new Dimension(120, 50));
		turndisp.setBorder(loweredetched);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        

		//---- Create layouts for frame and panels

		window.setLayout(new BorderLayout());
		board.setLayout(new GridLayout(5, 5));
		inventory.setLayout(new GridLayout(2, 9));

		//---- Adds the panels to the frame
		Container contentPane = window.getContentPane();
		contentPane.add(board, BorderLayout.LINE_START);
		contentPane.add(inventory, BorderLayout.SOUTH);
		contentPane.add(menu, BorderLayout.LINE_END);     	

		if (turndisp != null) {
			menu.add(turndisp);
		}
		
		//---- Draws cells to the board
		drawCells();
		
		//---- Draws pieces to inventory
		drawPieces(pnumber);


		//---- Makes the window visible
		window.pack();
		window.validate();
		window.setVisible(true);

	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// Alleen voor testen, verwijder na implementatie
		ArrayList<String> names = new ArrayList<String>();
		names.add(args[0]);
		names.add(args[1]);
		names.add(args[2]);
		names.add(args[3]);
		// eigen naam zoeken, hoe te implementeren?
		name = args[0];

		ClientGUI gui = new ClientGUI(new Game(2, 2, names));
		Client client = new Client(name + Math.random());
		client.start();

	}

	/**
	 * Draws the pieces for a certain player in 
	 * inventory.
	 * @param p
	 */
	//TODO start command geeft lijst van spelers mee dit aanpassen!
	public void drawPieces(final int p) {
		for (Piece piece : game.getPlayer(p).getPieces()) {
			JPanel piecePanel = new PiecePainter(piece);
			piecePanel.setOpaque(true);
			
			inventory.add(piecePanel);
		}
			
		
	}
	/**
	 * Draws all of the cells onto the board.
	 */
	public void drawCells() {
		for (int y = 0; y < Board.Y; y++) {
			for (int x = 0; x < Board.X; x++) {
				JPanel cellPanel = new CellPainter(game.getBoard().getCell(x, y));
				cellPanel.setOpaque(true);
				cellPanel.setBackground(new Color(0, 0, 153));
				cellPanel.setPreferredSize(new Dimension(10, 10));
				cellPanel.setMaximumSize(new Dimension(10, 10));
				board.add(cellPanel);
			}
		}

	}
}
