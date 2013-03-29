package client;

import game.Board;
import game.Cell;
import game.Piece;
import game.PlayerColor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

public class PiecePainter extends JPanel{

	

	//---- Constants --------------------------
	
	private int LINESTART = 15;
	private int LINEEND = 50;
	
	//---- Instance Variables -----------------
	
	private Piece piece;

	//---- Constructor ------------------------

	public PiecePainter(Piece p) {
		this.piece = p;
	}
	
	//---- Methods ----------------------------
	/**
	 * Visual representation for Piece in both Inventory
	 * and Board. Only draws one Piece per PiecePainter.
	 */
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		//---- Color determination -------------
		if (piece.getColor() == PlayerColor.COLOR_0) {
			g2.setColor(Color.RED);
		}
		if (piece.getColor() == PlayerColor.COLOR_1) {
			g2.setColor(Color.BLUE);
		}
		if (piece.getColor() == PlayerColor.COLOR_2) {
			g2.setColor(Color.GREEN);
		}
		if (piece.getColor() == PlayerColor.COLOR_3) {
			g2.setColor(Color.YELLOW);
		}
		//---- Position determination ----------
		
		int xCenter = this.getWidth() / 2;
		int yCenter = this.getHeight() / 2;
		
		//---- Type determination --------------
		Ellipse2D.Double circle = null;
		
		if (piece.getType() == Piece.RING_0) {
			circle = new Ellipse2D.Double(xCenter, yCenter, 10, 10);
			g2.fill(circle);
		}
		if (piece.getType() == Piece.RING_1) {
			circle = new Ellipse2D.Double(xCenter, yCenter, 20, 20);
		}
		if (piece.getType() == Piece.RING_2) {
			circle = new Ellipse2D.Double(xCenter, yCenter, 30, 30);
		}
		if (piece.getType() == Piece.RING_3) {
			circle = new Ellipse2D.Double(xCenter, yCenter, 40, 40);
		}
		if (piece.getType() == Piece.RING_4) {
			circle = new Ellipse2D.Double(xCenter, yCenter, 40, 40);
			g2.fill(circle);
		}
		g2.draw(circle);
			

	}


}


