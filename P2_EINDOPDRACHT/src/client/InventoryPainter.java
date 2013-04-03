package client;

import game.Piece;
import game.Player;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 * The visual representation of a Player's
 * inventory. Contains all of the unused
 * pieces that the player has. Said pieces are
 * stored in their own array within the Player
 * class. Inventory uses a custom Cell renderer
 * to draw visual pieces instead of labels using
 * the PiecePainter class.
 * @author martijnbruning
 *
 */
public class InventoryPainter extends JList {
	
	//---- Instance Variable -----------------
	static DefaultListModel model = new DefaultListModel();
	//---- Constructor ------------------------
	
	/**
	 * The visual representation of a Player's
	 * inventory.
	 * @param arrayList 
	 */
	public InventoryPainter(final ArrayList<Piece> arrayList) {
		super(model);
		for (Piece piece : arrayList) {
			model.addElement(piece);
		}
		//setLayout(new GridLayout(9, 2));
		setPreferredSize(new Dimension(500, 200));
		this.setCellRenderer(new MyCellRenderer());
		this.setVisibleRowCount(-1);
		this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		//TODO word gek hiervan, wat ermee doen?
		this.setFixedCellHeight(40);
		this.setFixedCellWidth(40);
		
	}
	
	//---- Methods ---------------
	/**
	 * removes a piece from the listmodel.
	 * @param arg
	 */
	protected void removePiece(final Piece arg) {
		model.removeElement(arg);
	}
	
	/**
	 * Nested class that changes the visual appearance of a cell in
	 * Inventory using the PiecePainter class.
	 * @author martijnbruning
	 *
	 */
	class MyCellComponent extends JComponent {
		/**
		 * Piece to draw.
		 */
		private Piece piece;
		
		//---- Constructor ---------
		/**
		 * Constructor loads piece for drawing.
		 * @param piece
		 */
		protected MyCellComponent(final Piece p) {
			this.piece = p;
		}
		//---- Query --------------
		/**
		 * Returns the piece this component represents.
		 * @return piece
		 */
		protected Piece getPiece() {
			return piece;
		}
		//---- Methods -------------
		@Override
		protected void paintComponent(final Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			PiecePainter.paintComponent(g2, piece, this.getWidth());
		}
	}
	/**
	 * Custom renderer for the custom Inventory JList
	 * draws components as visual pieces rather than
	 * labels.
	 * @author martijnbruning
	 *
	 */
	class MyCellRenderer extends PiecePainter implements ListCellRenderer {
	    /**
	     * Standard constructor. 
	     */
		public MyCellRenderer() {
			
	         setOpaque(true);
	         //setPreferredSize(new Dimension(45, 45));
	     }
	
		/**
		 * Method to return an element from the list
		 * whose paint method will be invoked to populate
		 * the list with.
		 */
	     public Component getListCellRendererComponent(
	         JList list,
	         Object value,
	         int index,
	         boolean isSelected,
	         boolean cellHasFocus)
	     {
	
	         return new MyCellComponent((Piece) value);
	     }
	     
	     
	 }
}
