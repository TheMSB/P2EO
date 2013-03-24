package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import game.Board;
import game.Cell;
import game.Game;
import game.Piece;
import game.Player;
import game.PlayerColor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ClientGUI {

	//---- Instance Variables -------------------------
	private Game game;

	//---- Windows and Panels -------------------------
	private JFrame window = new JFrame("RINGZ");
	private JPanel board = new JPanel();
	private JPanel menu = new JPanel();
	private JPanel inventory = new JPanel();
	private JPanel chatbox = new JPanel();

	// Buttons and Labels
	//TODO build these.


	//TODO creating board as grid still seems best as logic
	// will get confusing for the player, (where can he place?)
	// 5x5 grid, diagonal is still viable since the comparative
	// alignments are still understandable.


	/**
	 * The GUI for the Client of the RINGZ game.
	 * Responsible for drawing the initial game screen,
	 * use provided draw methods to modify the field.
	 */
	public ClientGUI() {
		
		//TODO awesome styling als klaar is met steampunk, rings zijn tandwielen!
		
		// Defines Border styles
        Border paneEdge = BorderFactory.createEmptyBorder(0,10,10,10);
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        
		// Main Window

		window.setSize(440, 320);
		board.setPreferredSize(new Dimension(300, 300));

		window.add(board);
		window.add(menu);
		menu.add(inventory);
		menu.add(chatbox);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  

		// Board
		
		// Separate definition for the menu background
        
        menu.setOpaque(true);
        menu.setBackground(new Color(144, 125, 67));
        menu.setPreferredSize(new Dimension(130, 320));
        menu.setBorder(paneEdge);
        
		// Inventory
        
        inventory.setLayout(new GridLayout(4,3));
			// Draw all pieces in inventory, drag and drop function?
		// Chat
			// import from chat server



		// Makes the window visible
		window.pack();
		window.validate();
		window.setVisible(true);
	}
	//TODO Constructor that initializes all components.

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		//Client client = new Client("Derk");
		//client.start();
		new ClientGUI();

	}

	//TODO ActionListeners for board and inventory.

	//TODO observer of chat?
}
