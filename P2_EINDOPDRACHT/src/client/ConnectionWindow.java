package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import game.Game;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import server.Server;



public class ConnectionWindow extends JFrame implements ActionListener, MessageUI, KeyListener {

	//---- Game related variables ----------------------
	
		private Game game;
		private String name;
	// Windows and Panels
		
		private Container c;
		private JPanel menu = new JPanel();
		private JPanel turndisp = new JPanel();
		private JPanel chatbox = new JPanel();
		private JButton connectB = new JButton("JOIN");
		private JButton bConnect;
		
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
	
	//---- Constructor ---------------------------------
		
	public ConnectionWindow() {
		super("LOTR Debug Launcher");
		
		//this.game = g;
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

	/**
	 * Creates the main GUI components.
	 */
	private void buildGUI() {
		
		//---- Main window ----
				setPreferredSize(new Dimension(800, 600));
				setMinimumSize(new Dimension(800, 600));
		//---- Menu ----
				menu.setPreferredSize(new Dimension(300, 300));
				menu.setMinimumSize(new Dimension(300, 300));
				
				JPanel p1 = new JPanel(new FlowLayout());
		        JPanel pp = new JPanel(new GridLayout(3, 2));
		 
		        JLabel lbAddress = new JLabel("Address: ");
		        tfAddress = new JTextField("localhost", 12);
		        tfAddress.addKeyListener(this);
		        myName = new JTextField("", 10);
		        myName.addKeyListener(this);
		        tfAddress.setEditable(true);

		        JLabel lbPort = new JLabel("Port:");
		        tfPort        = new JTextField("4242", 5);
		        tfPort.addKeyListener(this);

		        JLabel lbMyName = new JLabel("Naam: ");
		        
		        pp.add(lbAddress);
		        pp.add(tfAddress);
		        pp.add(lbPort);
		        pp.add(tfPort);
		        pp.add(lbMyName);
		        pp.add(myName);

		        bConnect = new JButton("Connect");
		        bConnect.setEnabled(false);
		        bConnect.setFocusable(false);
		        bConnect.addActionListener(this);

		        p1.add(pp, BorderLayout.WEST);
		        p1.add(bConnect, BorderLayout.EAST);

		        // Panel p2 - Messages

		        JPanel p2 = new JPanel();
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
		        
		        p3.add(myMessagelb, BorderLayout.NORTH);
		        p3.add(myMessage);
		        p2.add(p3, BorderLayout.NORTH);
		        
		        p2.add(lbMessages);
		        p2.add(taMessages, BorderLayout.SOUTH);

		        Container cc = getContentPane();
		        cc.setLayout(new FlowLayout());
		        cc.add(p1); 
		        cc.add(p2);
		    
		
		// TODO Auto-generated constructor stub
		
		//---- Defines Border styles
				Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);
				Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}
	
	/**
	 * Loads a game into the GUI for
	 * drawing.
	 * @param g Game to load
	 */
	protected void setGame(final Game g) {
		this.game = g;
	}
	
	//---- Action Events ------------------------
	
	@Override
	public void actionPerformed(ActionEvent e) {
		connect();
		
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
    	InetAddress addr = null;;
    	try {
			addr = InetAddress.getByName(tfAddress.getText());
		} catch (UnknownHostException e1) {
			System.out.println("UnknownHost");
		}
    	
    	int         port = 4242;
        try {
        	port  = Integer.parseInt(tfPort.getText());
        } catch(NumberFormatException e) {
        	System.out.println("Connection cannot be made, "+ tfPort.getText() + " is not a valid number");
        }
    	
        try {
        	client = new Client(myName.getText(), addr, port, this);
        	client.start();
		} catch (IOException e) {
			System.out.println("IOException");
		}
        addMessage("Server","Connected to server...");
        connected = true;
        myMessage.setEditable(true);
    }


	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_ENTER && connected)
		{
			//System.out.println("Enter pressed");
			client.sendMessage(myMessage.getText());
			myMessage.setText("");
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		
				if(event.getSource().equals(myName))
				{
					myNameChanged = true;
				}
				if(event.getSource().equals(tfAddress))
				{
					tfAddressChanged = true;
				}
				if(event.getSource().equals(tfPort))
				{
					tfPortChanged = true;
					
				}
				if(myNameChanged && tfAddressChanged && tfPortChanged)
				{
					bConnect.setEnabled(true);
				}
		
	}

	@Override
	public void addMessage(final String name, final String msg) {
		 taMessages.append("<" + name + "> " + msg +"\n");
		
	}

}
