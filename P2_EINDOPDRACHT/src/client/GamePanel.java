package client;

import game.Board;
import game.Game;
import game.Piece;
import game.Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.InventoryPainter.MyCellComponent;

/**
 * GamePanel is an extension of JPanel
 * it is responsible for drawing the
 * game board and player inventory
 * with all their pieces in them.
 * Note that the actual drawing of the pieces
 * is delegated to the PiecePainter Class.
 * 
 * @author martijnbruning
 *
 */
public class GamePanel extends JPanel {

	//---- Instance Variables -----------------------------------
	private InventoryPainter invent;
	private ActionWindow aw;
	private Game game;
	private Player player;
	/**
	 * Variables for loading a piece from the inventory
	 * so it can be placed on the board.
	 */
	private int type;
	private int color;
	private Piece inventPiece;
	//---- Swing Elements ------------------------------
	private JPanel board = new JPanel();
	private JPanel inventory = new JPanel();

	//---- Constructor ------------------------------------------		
	/**
	 * Main Constructor for GamePanel
	 * it represents the elements that
	 * are essential for the game that is
	 * being played.
	 * @param g Game that is being played
	 * @param p Player that is playing
	 */
	public GamePanel(final Game g, final Player p, final ActionWindow a) {
		this.aw = a;
		this.game = g;
		this.player = p;

		setLayout(new BorderLayout());
		board.setLayout(new GridLayout(5, 5));

		//---- Draws cells to the board ------------------
		drawCells();
		add(board, BorderLayout.LINE_START);
		//---- Draws inventory ---------------------------
		drawInventory();
		add(inventory, BorderLayout.SOUTH);
	}

	//---- Query -------------------------------

	/**
	 * Returns the InventoryPainter for this game.
	 * @return
	 */
	protected InventoryPainter getInventory(){
		return invent;
	}
	/**
	 * Removes a selected piece from the Inventory List.
	 * @param p
	 */
	protected void removePiece(Piece p) {
		invent.removePiece(p);
	}

	//---- Methods -----------------------------	

	/**
	 * Draws all of the cells onto the board.
	 */
	public void drawCells() {
		for (int y = 0; y < Board.Y; y++) {
			for (int x = 0; x < Board.X; x++) {
				JPanel cellPanel = new CellPanel(game.getBoard().getCell(x, y));
				cellPanel.setOpaque(false);
				//cellPanel.setBackground(new Color(0, 0, 153));
				cellPanel.setPreferredSize(new Dimension(100, 100));
				cellPanel.setMaximumSize(new Dimension(100, 100));	
				cellPanel.addMouseListener(aw);
				board.add(cellPanel);
			}
		}

	}

	/**
	 * Draws pieces to the visual inventory.
	 * Contents are determined by the inventory field
	 * of the Player Class of the currently playing Player.
	 */
	public void drawInventory() {
		invent = new InventoryPainter(player.getPieces());
		invent.getSelectionModel().addListSelectionListener(aw);
		inventory.setPreferredSize(new Dimension(500, 150));
		inventory.add(invent);

	}

}
