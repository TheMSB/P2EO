package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import game.Game;
import game.Player;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import server.Server;
import util.SoundPlayer;

/**
 * ConnectionWindow Class for our implementation of the
 * RINGGZ game, this window will be responsible for rendering
 * all of the components required for the client to connect to
 * a server and join a game. Upon game start this
 * window becomes invisible and calls upon a new ActionWindow
 * to render to actual game.
 * 
 * Featured Options:
 * -Russian Spam Bot
 * -Cyrillic writing
 * -Menu music
 * -Varied AI selection
 * 
 * @author martijnbruning
 *
 */

public class ConnectionWindow extends JFrame implements ActionListener, MessageUI, KeyListener, ItemListener {

	//---- Game related variables ----------------------
	//CHECKSTYLE:OFF
	private Game game;
	private String name;
	private String[] aiListing = {"None", "SmartAI","RandomAI","E-WallAI"};
	private String[] pnrListing = {"2","3","4"};
	private SoundPlayer soundPlayer;

	//---- Swing Elements ------------------------------

	private Container c;
	private JPanel menu = new JPanel();
	private JPanel joinPanel;
	private JPanel p2 = new JPanel();
	private JPanel turndisp = new JPanel();
	private JPanel chatbox = new JPanel();

	private JButton bConnect;
	private JButton bJoin;

	private JComboBox aiList;
	private JComboBox nrPlayers;

	private JCheckBox bFlame;
	private JCheckBox bCyrillic;
	private JCheckBox bMusic;

	private JTextField  tfPort;
	private JTextField	tfAddress;
	private JTextField	myName;

	private boolean  	tfPortChanged;
	private boolean  	tfAddressChanged;
	private boolean  	myNameChanged;

	private JTextField	myMessage;
	private JTextArea   taMessages;

	private Server      server;
	private Client	client;
	private boolean connected;
	//CHECKSTYLE:ON
	//---- Constructor ---------------------------------

	/**
	 * Main Constructor for Connection Window.
	 * Adds WindowListeners and calls the buildGUI
	 * and playSound methods.
	 * Finally packs and validates the window before
	 * making it visible.
	 */
	public ConnectionWindow() {
		super("LOTR Debug Launcher");

		//playSound();
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


	}

	/**
	 * Creates the main GUI components.
	 * First the Main Window is defines and populated.
	 * the Section Panel 2 Messages handles the creation
	 * and population of the chat window that is added to the
	 * main window.
	 */
	private void buildGUI() {

		//---- Main window ----
		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(800, 600));
		//---- Menu ------------------------------
		menu.setPreferredSize(new Dimension(300, 300));
		menu.setMinimumSize(new Dimension(300, 300));

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(4, 2));
		joinPanel = new JPanel(new GridLayout(6, 2));

		
		//---- Connection Menu -------------------
		JLabel lbAddress = new JLabel("Address: ");
		tfAddress = new JTextField("localhost", 12);
		tfAddress.addKeyListener(this);
		tfAddress.setEditable(true);

		JLabel lbMyName = new JLabel("Naam: ");
		myName = new JTextField("", 10);
		myName.addKeyListener(this);

		JLabel lbPort = new JLabel("Port:");
		tfPort        = new JTextField("4242", 5);
		tfPort.addKeyListener(this);

		//---- Join Menu --------------------------
		
		buildJoinMenu();
		
		//---- Menu Assembly ------------------------------------
		pp.add(lbAddress);
		pp.add(tfAddress);
		pp.add(lbPort);
		pp.add(tfPort);
		pp.add(lbMyName);
		pp.add(myName);
		
		//---- Button Initialization -----------------------
		bConnect = new JButton("Connect");
		bConnect.setEnabled(false);
		bConnect.setFocusable(false);
		bConnect.addActionListener(this);
		
		//---- Panel Wrapping ------------------------------
		
		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);

		//---- Panel p2 - Messages -------------------------

		buildChatMenu();

		//---- Content Window Wrapping -------------------
		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(joinPanel);
		cc.add(p2);

	}
	
	/**
	 * Called on by the buildGUI method to create
	 * the chat menu portion of the screen.
	 * Separated from main build method to reduce
	 * the amount of Executable statements.
	 */
	private void buildChatMenu() {
		
		p2.setLayout(new BorderLayout());

		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());

		myMessage = new JTextField("");
		JLabel myMessagelb = new JLabel("My Message:");
		p2.add(myMessage, BorderLayout.NORTH);
		myMessage.setEditable(false);
		myMessage.addKeyListener(this);

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		taMessages.setLineWrap(true);
		
		JScrollPane taScroll = new JScrollPane(taMessages, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    
		p3.add(myMessagelb, BorderLayout.NORTH);
		p3.add(myMessage);
		p2.add(p3, BorderLayout.NORTH);
		p2.add(lbMessages);
		p2.add(taScroll, BorderLayout.SOUTH);
		
	}

	/**
	 * Called on by the buildGUI method to create
	 * the join menu portion of the screen.
	 * Separated from main build method to reduce
	 * the amount of Executable statements.
	 */
	private void buildJoinMenu() {


		//---- Join Menu --------------------------
		JLabel lbNrPlayers = new JLabel("Desired number of Players: ");
		nrPlayers = new JComboBox(pnrListing);
		nrPlayers.setSelectedIndex(0);
		nrPlayers.addActionListener(this);
		nrPlayers.setEnabled(false);

		JLabel lbAI = new JLabel("Desired AI: ");
		aiList = new JComboBox(aiListing);
		aiList.setSelectedIndex(0);
		aiList.addActionListener(this);
		aiList.setEnabled(false);

		JLabel lbSpam = new JLabel("Flame Bot: ");
		bFlame = new JCheckBox("Enabled");
		bFlame.setEnabled(false);
		bFlame.addItemListener(this);

		JLabel lbCyrillic = new JLabel("Cyrillic Writing: ");
		bCyrillic = new JCheckBox("Enabled");
		bCyrillic.setEnabled(false);
		bCyrillic.addItemListener(this);

		//---- Connection Window Feature Controllers ------------
		
		JLabel lbMusic = new JLabel("Music: ");
		bMusic = new JCheckBox("Enabled");
		bMusic.setEnabled(true);
		bMusic.addItemListener(this);
		
		//---- Menu Assembly -------------------
		joinPanel.add(lbNrPlayers);
		joinPanel.add(nrPlayers);
		joinPanel.add(lbAI);
		joinPanel.add(aiList);
		joinPanel.add(lbSpam);
		joinPanel.add(bFlame);
		joinPanel.add(lbCyrillic);
		joinPanel.add(bCyrillic);
		joinPanel.add(lbMusic);
		joinPanel.add(bMusic);

		//---- Button Creation -----------------------------
		bJoin = new JButton("Join");
		bJoin.setEnabled(false);
		bJoin.setFocusable(false);
		bJoin.addActionListener(this);
		//---- Panel Wrapping ------------------------------
		joinPanel.add(bJoin, BorderLayout.EAST);

	}

	/**
	 * Loads a game into the GUI for
	 * drawing. Should only ever be called by client
	 * after being notified that a game will start.
	 * @param g Game to load
	 */
	void setGame(final Game g, final Player p) {
		this.game = g;
		this.setVisible(false);
		stopSound();
		new ActionWindow(game, p, client);

	}

	/**
	 * Joins a lobby in the attempt to start a game
	 * with the specified amount of players.
	 * @param slots Number of desired player slots.
	 */
	protected void join(final int slots) {
		client.joinLobby(slots);
		bJoin.setEnabled(false);
	}

	@Override
	public void addMessage(final String name1, final String msg) {
		taMessages.append("<" + name1 + "> " + msg + "\n");

	}

	/**
	 * Attempts to create a new socket connection
	 * with the server, if all connection fields
	 * have valid parameters a connection will
	 * be made, the connection button will be disabled and
	 * the chat window will be enabled. The Client will
	 * remain in this window until the server lobby is full
	 * and starts a new game. At this point ConnectionWindow will
	 * close and a new ActionWindow will be created.
	 */
	public void connect() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(tfAddress.getText());
		} catch (UnknownHostException e1) {
			addMessage("Exception", "UnknownHost");
		}

		int         port = 4242;
		try {
			port  = Integer.parseInt(tfPort.getText());
		} catch (NumberFormatException e) {
			addMessage("Exception", "Connection cannot be made, " + tfPort.getText() + " is not a valid number");	
		}

		try {
			client = new Client(myName.getText(), addr, port, this);
			client.start();
			
		} catch (IOException e) {
			
			System.out.println("IOException");
		}
		
	}

	/**
	 * Plays a relaxing waiting tune
	 * while players wait to connect to 
	 * a server or join a game.
	 */
	private void playSound() {
		soundPlayer = new SoundPlayer();
		soundPlayer.setRadioSong(true);
		soundPlayer.start();
	}
	/**
	 * Stops the waiting tune after the current loop.
	 */
	private void stopSound() {
		soundPlayer.setRadioSong(false);
	}
	
	/**
	 * Called after handshaking is complete.
	 * Enables the rest of the GUI for operation.
	 */
	public void enableMenu() {
		connected = client.isConnected();
		
		if (connected) {
			addMessage("Server", "Connected to server...");
			myMessage.setEditable(true);
			bConnect.setEnabled(false);
			nrPlayers.setEnabled(true);
			aiList.setEnabled(true);
			bFlame.setEnabled(true);
			bCyrillic.setEnabled(true);
			bJoin.setEnabled(true);
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
		if (e.getSource() == bConnect) {
			connect();
			
		} else if (e.getSource() == bJoin) {
			join(Integer.parseInt((String) nrPlayers.getSelectedItem()));
		} else if (e.getSource() == aiList) {
			if (aiList.getSelectedIndex() == 0) {
				client.setIsPlaying(true);
				client.setAI(2);
			}
			if (aiList.getSelectedIndex() == 1) {
				client.setIsPlaying(false);
				client.setAI(1);
			}
			if (aiList.getSelectedIndex() == 2) {
				client.setIsPlaying(false);
				client.setAI(2);
			}
			if (aiList.getSelectedIndex() == 3) {
				client.setIsPlaying(false);
				client.setAI(3);
			}

		} 

	}


	@Override
	public void keyPressed(final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ENTER && connected) {
			client.sendMessage(myMessage.getText());
			myMessage.setText("");
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(final KeyEvent event) {

		if (event.getSource().equals(myName)) {
			myNameChanged = true;
		}
		if (event.getSource().equals(tfAddress)) {
			tfAddressChanged = true;
		}
		if (event.getSource().equals(tfPort)) {
			tfPortChanged = true;

		}
		if (myNameChanged && !connected) {
			bConnect.setEnabled(true);
		}


	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		if (e.getSource() == bFlame) {
			if (bFlame.isSelected()) {
				client.setFlame(true);
			} else {
				client.setFlame(false);
			}
		} 
		if (e.getSource() == bCyrillic) {
			if (bCyrillic.isSelected()) {
				client.setCyrillic(true);
			} else {
				client.setCyrillic(false);
			}
		}
		if (e.getSource() == bMusic) {
			if (bMusic.isSelected()) {
				playSound();	
			} else {
				stopSound();
			}
		}

	}

}

