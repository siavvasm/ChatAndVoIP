package com.miltos.communication.main;

import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import com.miltos.communication.voip.Receiver;
import com.miltos.communication.voip.Transmitter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.lang.Thread;

public class Lan4 extends Frame implements WindowListener, ActionListener {

	/*
	 * Definition of the app's fields
	 */
	static TextField inputTextField;			// The TextField used for writing the new message
	static JTextArea textArea;					// The TextArea where the sent and received messages are printed 
	static JFrame frame;						// The Frame that implements the app's GUI 
	static JButton sendButton;				    // The Button for sending the text written in the TextField 
	static JTextField meesageTextField;			// The TextField used for writing the new message  
	static DatagramSocket receivingSocket;		// The Socket for receiving UDP Text Packets 
	public static Color gray;					// The Color of the Frame 
	final static String newline="\n";			// A static variable that is used for changing line in TextArea 
	static JButton callButton;					// The Button for enabling and disabling Voip Calls
	static int flag = 0;						// A flag that indicates if the call button was previously pressed 		
	
	
	/*
	 * Definition of the app's ports.
	 */
	static int messagesRecPort = 48011;
	static int messagesSenPort = 38011;
	
	static byte[] remoteIP= { (byte)192,(byte)168,(byte)1,(byte)84 };
	
	/**
	 * Construct the app's frame and initialize important parameters
	 */
	public Lan4(String title) {
		
		/*
		 * 1. Defining the components of the GUI.
		 */
		
		//Setting up the frame's characteristics.
		super(title);									
		gray = new Color(254, 254, 254);		
		setBackground(gray);
		setLayout(new FlowLayout());			
		addWindowListener(this);	
		
		//Setting up the TextField and the TextArea.
		inputTextField =new TextField();
		inputTextField.setColumns(20);
		
		textArea=new JTextArea(10,40);			
		textArea.setLineWrap(true);				
		textArea.setEditable(false);			
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Setting up the buttons.
		sendButton=new JButton("Send");			
		callButton=new JButton("Call");			
					
		
		/*
		 * 2. Adding the components to the GUI.
		 */
		add(scrollPane);								
		add(inputTextField);
		add(sendButton);
		add(callButton);
		
		/*
		 * 3. Linking the buttons to the ActionListener.
		 */
		sendButton.addActionListener(this);			
		callButton.addActionListener(this);	

		/*
		 * 4. Create a socket for receiving messages.  
		 */
		try{		
			receivingSocket=new DatagramSocket(messagesRecPort);
			
		}catch(SocketException ec){
			System.out.println(ec);	
		}			
	}
	
	/**
	 * The main method of the application. It continuously listens for
	 * new messages.
	 */
	public static void main(String[] args){
	
		/*
		 * 1. Create the app's window.
		 */
		Lan4 lan4 = new Lan4("LAN UDP Chat");  																		  
		lan4.setSize(500,250);				  
		lan4.setVisible(true);				  
	
		/*
		 * 2. Create the receiving packet
		 */
		byte[] rxBuffer = new byte[256];  
		DatagramPacket receivedPacket = new DatagramPacket(rxBuffer, rxBuffer.length); 

		/*
		 * 3. Listening Phase - Continuously listen for new messages.
		 */
		do{
			
			try{
				
				//TODO: Remove this print.
				System.out.println("receive"); 
				Toolkit.getDefaultToolkit().beep();				
				
				//Get the message into String format
				String message;	
				receivingSocket.receive(receivedPacket);		
				message = new String(rxBuffer, 0, receivedPacket.getLength());  
				
				//TODO: Remove this print
				System.out.println(message);					
				
				//Print the message to the TextArea of the frame
				textArea.append("away: "+ message + newline);		
				
			}catch (Exception e){							
					System.out.println(e);
			}
			
		}while(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			
			/*
			 * 1. Create the receiving and sending sockets.
			 */
			byte[] txBuffer; 
			DatagramSocket sendingSocket = new DatagramSocket();
			
			byte[] rxBuffer = new byte[256];
			DatagramPacket receivingPacket = new DatagramPacket(rxBuffer, rxBuffer.length);
			
			InetAddress remoteAddress=null;
			
			/*
			 * 2. Check which button was clicked.
			 */
			if (e.getSource() == sendButton){
				
				/*
				 * 2.1. Get the message from the text field and add it to the buffer.
				 */
				String packetInfo = inputTextField.getText();
				
				textArea.append("home: "+packetInfo+ newline);
				
				txBuffer = packetInfo.getBytes();
				
				String clear="";
				inputTextField.setText(clear);
				
				/*
				 * 2.2. Send the message to the remote IP Address.
				 */
				try {
					
					//Construct a packet containing this message
					remoteAddress = InetAddress.getByAddress(remoteIP);
					DatagramPacket sendingPacket = new DatagramPacket(txBuffer, txBuffer.length, remoteAddress, messagesSenPort);	
					
					//Send the packet to the reomote IP address.
					try {
						sendingSocket.send(sendingPacket);
					} catch (IOException ex) {
						System.out.println(ex);
					}
					
				}catch (UnknownHostException ed) {
					System.out.println(ed);
				}
				
			}else if(e.getSource() == callButton){
				
				/*
				 * Create and start the Rx and Tx threads.
				 */
				
				//TODO: Remove this print
				System.out.println("Call Button Pressed...");
				
				if (flag == 0){
					
					//Set the flag so that it will not get in this block again.
					flag = 1;
					
					//TODO: Remove this print
					System.out.println("Creating Receiver and Transmitter objects ...");
					
					//Instantiate the Receiver and Transmitter classes.
					Receiver receiver = new Receiver();
					Transmitter transmitter = new Transmitter();
					
					//Create the threads and start them.
					new Thread(receiver).start();
					new Thread(transmitter).start();
					
					//TODO: Remove this print
					System.out.println("Receiver and Transmitter objects where successfully created!");
					
				}
			}
			
		}catch(SocketException ec){
			System.out.println(ec);
		}	
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		dispose();
        System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
