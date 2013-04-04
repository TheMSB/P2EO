package client;

import game.Board;
import game.Cell;
import game.Piece;
import game.PlayerColor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

public class PiecePainter{



	//---- Constants --------------------------


	//---- Instance Variables -----------------

	//private Piece piece;

	//---- Constructor ------------------------

	/**
	 * Default constructor for PiecePainter.
	 */
	public PiecePainter() {
		//this.piece = p;
		//setOpaque(true);
	}

	//---- Methods ----------------------------
	/**
	 * Visual representation for Piece in both Inventory
	 * and Board. Only draws one Piece per PiecePainter.
	 */
	public static void paintComponent(final Graphics2D g, final Piece piece, final int w) {

		//---- Position determination ----------
		int width = w;
		int height = w;

		int center = width / 2;


		//TODO maybe2
		double diameter0 = width * 0.30;
		double diameter1 = width * 0.40;
		double diameter2 = width * 0.50;
		double diameter3 = width * 0.60;
		double diameter4 = width * 0.80;

		//TODO maybe
		double start0 = center - (diameter0 / 2);
		double start1 = center - (diameter1 / 2);
		double start2 = center - (diameter2 / 2);
		double start3 = center - (diameter3 / 2);
		double start4 = center - (diameter4 / 2);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(6));

		//---- Color determination -------------
		Color color1 = null;
		if (piece.getColor() == PlayerColor.COLOR_0) {
			g2.setColor(Color.RED);
			color1 = Color.RED;
		}
		if (piece.getColor() == PlayerColor.COLOR_1) {
			g2.setColor(Color.BLUE);
			color1 = Color.BLUE;
		}
		if (piece.getColor() == PlayerColor.COLOR_2) {
			g2.setColor(Color.GREEN);
			color1 = Color.GREEN;
		}
		if (piece.getColor() == PlayerColor.COLOR_3) {
			g2.setColor(Color.YELLOW);
			color1 = Color.YELLOW;
		}

		Color color2 = color1.darker();
		GradientPaint gp = new GradientPaint(
				0, 0, color1, 0, height, color2);
		g2.setPaint(gp);


		//---- Type determination --------------
		Ellipse2D.Double circle = null;

		if (piece.getType() == Piece.RING_0) {
			circle = new Ellipse2D.Double(start0, start0, diameter0, diameter0);
			g2.fill(circle);
		}
		if (piece.getType() == Piece.RING_1) {
			circle = new Ellipse2D.Double(start1, start1, diameter1, diameter1);
		}
		if (piece.getType() == Piece.RING_2) {
			circle = new Ellipse2D.Double(start2, start2, diameter2, diameter2);
		}
		if (piece.getType() == Piece.RING_3) {
			circle = new Ellipse2D.Double(start3, start3, diameter3, diameter3);
		}
		if (piece.getType() == Piece.RING_4) {
			circle = new Ellipse2D.Double(start4, start4, diameter4, diameter4);
			g2.fill(circle);
		}
		g2.draw(circle);


	}

	/**public Piece getDrawPiece() {
		return piece;
	}

	public void setDrawPiece(Piece piece) {
		this.piece = piece;
	}*/




}


