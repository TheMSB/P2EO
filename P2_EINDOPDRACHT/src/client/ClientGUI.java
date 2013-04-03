package client;

import exceptions.InvalidMoveException;
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
	
	// Windows and Panels
	private ActionWindow aWindow;
    private Server      server;
	
	//CHECKSTYLE:ON

	//---- Constructor ---------------------------------

	public ClientGUI(Game game) {
		aWindow = new ActionWindow(game, name);
		aWindow.pack();
		aWindow.validate();
		aWindow.setVisible(true);
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// Alleen voor testen, verwijder na implementatie
		ArrayList<String> names = new ArrayList<String>();
		names.add(args[0]);
		names.add(args[1]);
		// eigen naam zoeken, hoe te implementeren?
		name = args[0];

		try {
			ClientGUI gui = new ClientGUI(new Game(2, 2, names));
		} catch (InvalidMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Client client = new Client(name + Math.random());
		client.start();

	}

}
