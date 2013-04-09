package client;

import java.awt.BorderLayout;
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
import java.util.ArrayList;

import exceptions.InvalidPieceException;
import game.Board;
import game.Game;
import game.Piece;
import game.Player;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.SoundPlayer;

import client.InventoryPainter.MyCellComponent;

/**
 * ActionWindow for our implementation of the
 * RINGGZ game. Is created by a ConnectionWindow,
 * this ActionWindow renders the current game that
 * is in progress and is responsible for handling all
 * further user input related to the game.
 * 
 * Features:
 * -GLaDOS taunts (if supported by an AppertureScience certified server)
 * -Victory music if the game has been won. (Or if science got done)
 * 
 * @author martijnbruning
 *
 */
public class ActionWindow extends JFrame implements ActionListener, MouseListener, MessageUI, KeyListener, ListSelectionListener {

	//---- Server related varuables --------------------
	private MessageUI cw;
	//---- Game related variables ----------------------

	private Game game;
	private String name;
	private Player player;
	private Client client;

	//---- Move related variables -----------------------
	private int type = 42;
	private int color = 42;
	private Piece inventPiece; 

	//---- Swing Components -----------------------------

	private Container c;
	private JPanel menu = new JPanel();
	private JPanel turndisp = new JPanel();
	private JPanel chatbox = new JPanel();

	private GamePanel gamePanel;
	private JTextField	myMessage;
	private JTextArea   taMessages;
	private JButton bHint;

	//---- Constructor ---------------------------------

	/**
	 * Calls upon the super constructor of JFrame
	 * to create a basic window with title, sets the client
	 * and game instance variables before building a new
	 * GUI. Handles WindowEvents
	 * @param g
	 * @param p
	 * @param client
	 */
	public ActionWindow(final Game g, final Player p, final Client clnt) {
		super("Lord of the RINGGZ");

		this.client = clnt;
		this.game = g;
		this.player = p;
		cw = client.getMUI();
		client.setMUI(this);
		c = getContentPane();
		buildGUI();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				e.getWindow().dispose();
			}
			public void windowClosed(final WindowEvent e) {
				System.exit(0);
			}
		}
				);

		pack();
		validate();
		setVisible(true);
		SoundPlayer soundPlayer = new SoundPlayer();
		//---- Uncomment to enable BattleMusic -------------------
		//soundPlayer.playSound("resources/sounds2/BattleMusic.wav");
	}

	/**
	 * Creates the main GUI components.
	 * Calls on buildGame to handle the creation
	 * of the GamePanel.
	 */
	private void buildGUI() {

		//---- Main window ----------------------
		setPreferredSize(new Dimension(900, 650));
		setMinimumSize(new Dimension(900, 650));

		buildGame();

		//---- Panel p2 - Messages --------------

		chatbox.setPreferredSize(new Dimension(400, 650));
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p2.setLayout(new BorderLayout());
		p2.setPreferredSize(new Dimension(350, 600));

		bHint = new JButton("Hint");
		bHint.addActionListener(this);

		myMessage = new JTextField("");
		JLabel myMessagelb = new JLabel("My Message:");
		p2.add(myMessage, BorderLayout.NORTH);
		myMessage.setEditable(true);
		myMessage.addKeyListener(this);

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		//taMessages.setPreferredSize(new Dimension(300, 500));
		taMessages.setEditable(false);
		taMessages.setLineWrap(true);

		JScrollPane taScroll = new JScrollPane(taMessages, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		taScroll.setPreferredSize(new Dimension(300, 500));
		p3.add(myMessagelb, BorderLayout.NORTH);
		p3.add(myMessage);
		p2.add(p3, BorderLayout.NORTH);

		p2.add(lbMessages);
		p2.add(taScroll, BorderLayout.SOUTH);

		chatbox.add(bHint, BorderLayout.NORTH);
		chatbox.add(p2, BorderLayout.EAST);
		c.add(chatbox, BorderLayout.EAST);


	}
	/**
	 * Constructs the Game panel where the player
	 * can play on.
	 */
	protected void buildGame() {

		//---- Game Panel -----
		gamePanel = new GamePanel(game, player, this);

		gamePanel.setPreferredSize(new Dimension(500, 600));
		gamePanel.addMouseListener(this);

		c.add(gamePanel, BorderLayout.LINE_START);

		gamePanel.setVisible(true);
	}

	/**
	 * Updates the entire ActionWindow to reflect
	 * any changes that may have occurred.
	 * Should only be called by ActionWindow itself.
	 * NEVER call directly.
	 */
	private void updateAW() {
		repaint();
		gamePanel.repaint();
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
			System.out.println("doMove():  " + x + " " + y + " " + type + " " + color);
			inventPiece = game.getMovPiece(x, y, type, color);
			gamePanel.removePiece(inventPiece);
			updateAW();
		} catch (InvalidPieceException e) {
			// TODO Auto-generated catch block
		}

	}
	@Override
	public void addMessage(final String name1, final String msg) {
		taMessages.append("<" + name1 + "> " + msg + "\n");

	}

	/**
	 * Asks to play again after a game ends.
	 */
	public void playAgainDialog() {

		int response = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Confirm", 
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (response == JOptionPane.YES_OPTION) {
			
			client.setMUI(cw);
			this.setVisible(false);
			((ConnectionWindow) cw).enableMenu();
			((ConnectionWindow) cw).setVisible(true);

		} else {
			System.exit(0);
		}

	}

	//---- Action Events ------------------------

	//-------------------------------------------
	// All of these methods are used to trigger
	// methods when changes are detected.
	// The methods triggered depend on the changes
	// that are detected and the state of the objects
	// they are detected in.
	//-------------------------------------------
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == bHint) {
			ArrayList<Integer> moves = client.getAIMove();
			int pcx = moves.get(0);
			int pcy = moves.get(1);
			int pct = moves.get(2);
			String pcc = "any";
			if (moves.get(3) == 0) {
				pcc = "RED";
			} else if (moves.get(3) == 1) {
				pcc = "BLUE";
			} else if (moves.get(3) == 0) {
				pcc = "GREEN";
			} else if (moves.get(3) == 0) {
				pcc = "YELLOW";
			}
			JOptionPane.showMessageDialog(null, "Place color: " + pcc + " type:" + pct + " on: (" + pcx + "," + pcy + ")");
		}

	}

	@Override
	public void mouseClicked(final MouseEvent e) {

		if (e.getSource() instanceof CellPanel && type != 42 && color != 42) {
			for (int y = 0; y < Board.Y; y++) {
				for (int x = 0; x < Board.X; x++) {
					if (((CellPanel) e.getSource()).getCell() == game.getBoard().getCell(x, y)) {
						client.doHumanMove(x, y, type, color);
						type = 42;
						color = 42;
					}
				}
			}
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

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();

		if (lsm.isSelectionEmpty()) {
			taMessages.append(" No Piece Selected");
		} else {
			// Find out which indexes are selected.
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();
			for (int i = minIndex; i <= maxIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					Object item = gamePanel.getInventory().getModel().getElementAt(i);
					Piece p = (Piece) item;
					type = p.getType();
					color = p.getColor();
				}
			}
		}

	}

}
