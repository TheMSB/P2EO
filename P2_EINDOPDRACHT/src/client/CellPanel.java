package client;

import game.Board;
import game.Cell;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

public class CellPanel extends JPanel {

	//---- Instance Variables -----------------
	private Cell cell;

	//---- Constructor ------------------------

	public CellPanel(Cell c) {
		cell = c;
	}

	//---- Methods ----------------------------
	/**
	 * Draws the CellPanel, sollid background with
	 * a circle in the middle to indicate it's a usable field.
	 */
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.LIGHT_GRAY);
		//---- Position determination ----------
		
		int xWidth = this.getWidth();
		int yHeight = this.getHeight();
		int xCenter = xWidth / 2;
		int yCenter = yHeight / 2;
		
		//---- lines ---------------------------

		if (cell.getX() != 0) {
			Line2D.Double line = new Line2D.Double(xCenter, yCenter, xCenter - 15, yHeight);
			g2.draw(line);
		}
		
		//---- center point --------------------
		Ellipse2D.Double circle = new Ellipse2D.Double(xCenter, yCenter, 5, 5);
		g2.fill(circle);
		g2.draw(circle);
		
		g2.setColor(Color.GRAY);
		//g2.draw(new Ellipse2D.Double(r.width / 2, r.height / 2, 5, 5));

		// Fill werkt niet goed, rect opslaan in variable en dan toepassen?
		// Ellipse ook fillen en centreren

	}


}


