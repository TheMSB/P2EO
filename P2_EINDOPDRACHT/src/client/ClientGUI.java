package client;

import game.Board;
import game.Cell;
import game.Game;

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
	//---- Instance Variables --------------------------


	// Windows and Panels
	private JFrame window = new JFrame("RINGZ");
	private JPanel menu = new JPanel();
	private JPanel turndisp = new JPanel();
	private JPanel chatbox = new JPanel();

	private JPanel board = new JPanel();
	//private JPanel cells[] = new JPanel[25];
	private Cell[][] cells;
	//CHECKSTYLE:ON

	//---- Constructor ---------------------------------

	public ClientGUI(Game game) {

		cells = game.getBoard().getCells();

		//---- Create a new application window

		window.setSize(440, 320);
		board.setPreferredSize(new Dimension(500, 500));

		//---- Defines Border styles
		Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		//---- Separate definition for the menu background

		menu.setOpaque(true);
		menu.setBackground(new Color(154, 165, 127));
		menu.setPreferredSize(new Dimension(130, 320));
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

		//---- Adds the panels to the frame
		Container contentPane = window.getContentPane();
		contentPane.add(board, BorderLayout.LINE_START);
		contentPane.add(menu, BorderLayout.LINE_END);     	

		if (turndisp != null) {
			menu.add(turndisp);
		}

		// Adds drawn cells to the Board
		for (Cell[] cell : cells) {
			for (Cell cel : cell) {
				JPanel cellPanel = new CellPanel(cel);
				board.add(cellPanel);
			}	
		}


		//---- Makes the window visible
		window.pack();
		window.validate();
		window.setVisible(true);

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Alleen voor testen, verwijder na implementatie
		ArrayList<String> names = new ArrayList<String>();
		  names.add(args[0]);
		  names.add(args[1]);
		  names.add(args[2]);
		  names.add(args[3]);
		  
		ClientGUI gui = new ClientGUI(new Game(2, 2, names));
		Client client = new Client("Derk"+Math.random());
		client.start();

	}



}
