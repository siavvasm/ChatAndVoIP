package com.miltos.communication.voip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Transmitter implements Runnable {
	
	static DatagramSocket sendingSocket;
	static int sendingPort = 38012;
	static InetAddress remoteAddress;
	static byte[] txBuffer;
	static byte[] remoteIP= { (byte)192,(byte)168,(byte)1,(byte)84 };
	static AudioFormat format;
	
	/**
	 * Construct the sending socket.
	 */
	public Transmitter(){
		//Create the sending socket.
		try {
			sendingSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Capture speech and send it to the remote IP Address.
	 */
	@Override
	public void run() {
		
		/*
		 * 1. Setup the capturing line and start capturing speech.
		 */
		
		TargetDataLine line = setupTargetDataLine();
		line.start();
		
		/*
		 * 2. Create a byte array for receiving voice data from the microphone.
		 */
		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		
		//Define the size of the buffer and of the sending data
		int numBytesRead;                                                
		byte[] data = new byte[line.getBufferSize() /5];				

		/*
		 * 3. Send the speech packets to the remote IP Address.
		 */
		boolean stopped=false;											  
		
		while (!stopped) {											      
			
			numBytesRead =  line.read(data, 0, data.length);				  		
			
			try{
				
				remoteAddress = InetAddress.getByAddress(remoteIP);
				DatagramPacket sendingPacket = new DatagramPacket(data,data.length, remoteAddress, sendingPort);
				
				try{
					  sendingSocket.send(sendingPacket);		
					}catch (IOException m) {
					  System.out.println(m);
					}
				
				}catch(UnknownHostException ed){
					System.out.println(ed);
				}
			
			out.write(data, 0, numBytesRead);
		}
	}
	
	private TargetDataLine setupTargetDataLine(){
		
		/*
		 * 1. Set the desired AudioFormat of the speech packets.
		 */
		format = new AudioFormat(8000, 8, 1, true, false);
		
		TargetDataLine line = null; 
		DataLine.Info info =  new DataLine.Info(TargetDataLine.class, format);
		
		if (!AudioSystem.isLineSupported(info)) {					
			System.out.println("Line does not support the specific Audio Format");
		}
		
		/*
		 * 2. Obtain and open the desired line with these info.
		 */
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);		 
			line.open(format);                                       
		} catch (LineUnavailableException ex) {						 
			System.out.println("Line is not Available!!");
		}
		
		//TODO: Remove this print
		System.out.println(line.getBufferSize());	
		
		/*
		 * 3. Return the constructed TargetDataLine.
		 */
		return line;
	}

}
