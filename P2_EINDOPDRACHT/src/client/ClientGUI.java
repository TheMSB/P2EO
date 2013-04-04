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
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
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
	//CHECKSTYLE:ON

	//---- Constructor ---------------------------------

	/**
	 * Constructs a new ClientGUI and
	 * launches a ConnectionWindow.
	 */
	public ClientGUI() {
		new ConnectionWindow();
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, fall back to cross-platform
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				
			}
		}
		new ClientGUI();
	}


}


