package client;

import game.Board;
import game.Cell;
import game.Piece;

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

public class CellPanel extends JPanel {

	//---- Constants --------------------------
	
	private int LINESTART = 15;
	private int LINEEND = 50;
	
	//---- Instance Variables -----------------
	
	private Cell cell;

	//---- Constructor ------------------------

	public CellPanel(Cell c) {
		cell = c;
		setPreferredSize(new Dimension(100, 100));
	}

	//---- Methods ----------------------------
	/**
	 * Draws the CellPanel, solid background with
	 * a circle in the middle to indicate it's a usable field.
	 */
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.WHITE);
		//---- Position determination ----------
		
		int xCenter = this.getWidth() / 2;
		int yCenter = this.getHeight() / 2;
		
		//---- lines ---------------------------

		if (cell.getX() != 0) {
			Line2D.Double line = new Line2D.Double(xCenter - LINESTART, 
					yCenter, xCenter - LINEEND, yCenter);
			g2.draw(line);
		}
		if (cell.getX() != 4) {
			Line2D.Double line = new Line2D.Double(xCenter + LINESTART, 
					yCenter, xCenter + LINEEND, yCenter);
			g2.draw(line);
		}
		if (cell.getY() != 0) {
			Line2D.Double line = new Line2D.Double(xCenter, 
					yCenter - LINESTART, xCenter, yCenter - LINEEND);
			g2.draw(line);
		}
		if (cell.getY() != 4) {
			Line2D.Double line = new Line2D.Double(xCenter, 
					yCenter + LINESTART, xCenter, yCenter + LINEEND);
			g2.draw(line);
		}
		//---- center point --------------------
		
		Ellipse2D.Double circle = new Ellipse2D.Double(
				xCenter - 2.5,
				yCenter + 2.5, 
				5, 5);
		g2.fill(circle);
		g2.draw(circle);
		
		//---- Pieces --------------------------
		
		if (cell.getPieces() != null) {
			for (Piece piece : cell.getPieces()) {
				if (piece != null) {
					
					PiecePainter paint = new PiecePainter();
					paint.paintComponent(g2, piece, this.getWidth());
					/**
					 PiecePainter paint = new PiecePainter();
					
					paint.setOpaque(true);
					this.add(paint);
					paint.setDrawPiece(piece);
					*/
					// g2 meegeven methode
					
					System.out.println(cell.getX() + " " + cell.getY());
					System.out.println(piece);
					System.out.println(cell.getPieces());
					//paint.repaint();
				}
				
			}
		}
		
		//this.repaint();

	}


}


