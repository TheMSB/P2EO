package client;

import game.Board;
import game.Cell;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

public class CellPanel extends JPanel {

	//---- Instance Variables -----------------
	private Cell cell;
	final int PAD = 20;

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

		g2.setColor(Color.BLUE);

		// heel bord hier tekenen?
		int w = getWidth();
		int h = getHeight();
		double xInc = (double) (w - 2 * PAD) / Board.X;
		double yInc = (double) (h - 2 * PAD) / Board.Y;
		for (int i = 0; i < Board.Y; i++) {
			double y = PAD + i * yInc;
			for (int j = 0; j < Board.X; j++) {
				double x = PAD + j * xInc;
				Rectangle2D.Double r = new Rectangle2D.Double(x, y, xInc, yInc);
				
			}
		}
		// Kheb padding nodig, aka. teken grid lijnen.

		//g2.setColor(Color.BLUE);
		//g2.draw(new Rectangle2D.Double(cell.getX(), cell.getY(), 100, 100));
		//g2.fillRect(getSize().width + 100, getSize().height + 100, 100, 100);
		//g2.setColor(Color.GRAY);
		//g2.draw(new Ellipse2D.Double(r.width / 2, r.height / 2, 5, 5));

		// Fill werkt niet goed, rect opslaan in variable en dan toepassen?
		// Ellipse ook fillen en centreren

	}


}


