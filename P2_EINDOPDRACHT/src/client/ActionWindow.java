package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import exceptions.InvalidMoveException;
import exceptions.InvalidPieceException;
import game.Board;
import game.Game;
import game.Piece;
import game.Player;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import util.SoundPlayer;

import client.InventoryPainter.MyCellComponent;

public class ActionWindow extends JFrame implements ActionListener, MouseListener, MessageUI, KeyListener {

	//---- Game related variables ----------------------

	private Game game;
	private String name;
	private Player player;
	private Client client;
	
	//--- Move related variables -----------------------
	private int type = 42;
	private int color = 42;
	private Piece inventPiece; 

	// Windows and Panels

	private Container c;
	private JPanel menu = new JPanel();
	private JPanel turndisp = new JPanel();
	private JPanel chatbox = new JPanel();

	private GamePanel gamePanel;
	private JTextField	myMessage;
	private JTextArea   taMessages;

	//---- Constructor ---------------------------------

	public ActionWindow(final Game g, final Player p, final Client client) {
		super("Lord of the RINGGZ");

		this.client = client;
		this.game = g;
		//this.name = n;
		this.player = p;
		client.setMUI(this);
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
		SoundPlayer soundPlayer = new SoundPlayer();
		soundPlayer.playSound("resources/sounds2/BattleMusic.wav");
	}

	/**
	 * Creates the main GUI components.
	 */
	private void buildGUI() {

		//---- Main window ----
		setPreferredSize(new Dimension(900, 650));
		setMinimumSize(new Dimension(900, 650));

		buildGame();
		
		// Panel p2 - Messages

				chatbox.setPreferredSize(new Dimension(400, 650));
				JPanel p2 = new JPanel();
				JPanel p3 = new JPanel();
				p3.setLayout(new BorderLayout());
				p2.setLayout(new BorderLayout());
				p2.setPreferredSize(new Dimension(350, 600));

				myMessage = new JTextField("");
				JLabel myMessagelb = new JLabel("My Message:");
				p2.add(myMessage, BorderLayout.NORTH);
				myMessage.setEditable(true);
				myMessage.addKeyListener(this);

				JLabel lbMessages = new JLabel("Messages:");
				taMessages = new JTextArea("", 15, 50);
				taMessages.setPreferredSize(new Dimension(300, 500));
				taMessages.setEditable(true);

				p3.add(myMessagelb, BorderLayout.NORTH);
				p3.add(myMessage);
				p2.add(p3, BorderLayout.NORTH);

				p2.add(lbMessages);
				p2.add(taMessages, BorderLayout.SOUTH);
				
				chatbox.add(p2, BorderLayout.EAST);
				c.add(chatbox, BorderLayout.EAST);

		
	}
	/**
	 * Constructs the Game panel where the player
	 * can play on.
	 */
	protected void buildGame() {
		
		//---- Game Panel -----
		gamePanel = new GamePanel(game, player);

		gamePanel.setPreferredSize(new Dimension(500, 600));
		gamePanel.addMouseListener(this);

		c.add(gamePanel, BorderLayout.LINE_START);

		gamePanel.setVisible(true);
	}

	/**
	 * Updates the entire ActionWindow to reflect
	 * any changes that may have occurred.
	 * Should only be called by ActionWindow itself.
	 */
	private void updateAW() {
		repaint();
		gamePanel.repaint();
		//TODO dit 'refreshed' niet het inventory
		
	}

	/**
	 * Handles a move for both Human
	 * and AI players. This ensures that Inventory
	 * is updated when pieces are removed.
	 * @param x
	 * @param y
	 * @param type
	 * @param color
	 */
	void doMove(final int x, final int y, final int typ, final int colo) {
		this.type = typ;
		this.color = colo;
		
		try {
			System.out.println("doMove():  "+x + " "+ y + " "+ type +" "+colo);
			inventPiece = game.getMovPiece(x, y, type, color);
			gamePanel.removePiece(inventPiece);
			updateAW();
		} catch (InvalidPieceException e) {
			// TODO Auto-generated catch block
		}
		
	}
	@Override
	public void addMessage(final String name, final String msg) {
		taMessages.append("<" + name + "> " + msg + "\n");

	}

	//---- Action Events ------------------------

	@Override
	public void actionPerformed(final ActionEvent e) {


	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		
		repaint();
		gamePanel.repaint();

		if (e.getSource() instanceof CellPanel && type != 42 && color != 42) {
			for (int y = 0; y < Board.Y; y++) {
				for (int x = 0; x < Board.X; x++) {
					if (((CellPanel) e.getSource()).getCell() == game.getBoard().getCell(x, y)) {
						//TODO do move on cell
						System.out.println("clicked Cell: " + x + "," + y);
						client.doHumanMove(x, y, type, color);
						type = 42;
						color = 42;
					}
				}
			}
		}
		if (e.getSource() instanceof MyCellComponent) {
			Piece p = ((MyCellComponent) e.getSource()).getPiece();
			type = p.getType();
			color = p.getColor();
			inventPiece = p;
		}

	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			client.sendMessage(myMessage.getText());
			myMessage.setText("");
		}

	}

	@Override
	public void keyReleased(final KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(final KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
