package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import game.Game;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ActionWindow extends JFrame implements ActionListener, MouseListener {

	//---- Game related variables ----------------------
	
		private Game game;
		private String name;
	// Windows and Panels
		
		private Container c;
		private JPanel menu = new JPanel();
		private JPanel turndisp = new JPanel();
		private JPanel chatbox = new JPanel();

		private GamePanel gamePanel;
	
	//---- Constructor ---------------------------------
		
	public ActionWindow(final Game g, final String n) {
		super("Lord of the RINGZ");
		
		this.game = g;
		this.name = n;
		c = getContentPane();
		buildGUI();

		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        }
    );
				
		pack();
		validate();
		setVisible(true);
	}

	private void buildGUI() {
		
		//---- Main window ----
				setPreferredSize(new Dimension(500, 650));
				setMinimumSize(new Dimension(500, 650));
		
		//---- Game Panel -----
		gamePanel = new GamePanel(game, name);
		
		gamePanel.setPreferredSize(new Dimension(500, 600));
		gamePanel.addMouseListener(this);
		
		c.add(gamePanel, BorderLayout.LINE_START);
		
		gamePanel.setVisible(true);
		// TODO Auto-generated constructor stub
		
		//---- Defines Border styles
				Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);
				Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}
	protected void setGame(Game g){
		this.game = g;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		gamePanel.repaint();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getX());
		int x = e.getX();
		int y = e.getY();
		//System.out.println(gamePanel.findComponentAt(x, y));
		System.out.println("Component: " + e.getComponent());
		//System.out.println("Source: " + e.getSource());
		repaint();
		gamePanel.repaint();
		
		//((CellPanel.)
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
