package com.miltos.communication.voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Receiver implements Runnable {
	
	static DatagramSocket receivingSocket;
	static DatagramPacket receivedPacket;
	static byte[] rxBuffer;
	static AudioFormat formatPCM;	
	static int receivingPort = 48012;
	
	/**
	 * Defining the receiving socket.
	 */
	public Receiver() {
		
		try {
			receivingSocket = new DatagramSocket(receivingPort);
		} catch (SocketException ec){
			System.out.println(ec);
		}
	}
	
	@Override
	public void run() {
		
		//Set the format of the receiving sound packets.
		formatPCM=new AudioFormat(8000,8,1,true,false);
		
		try {
			
			//Create an audio stream in order to play the speech packets
			SourceDataLine lineOut = AudioSystem.getSourceDataLine(formatPCM);
			lineOut.open(formatPCM, 32000);
			lineOut.start();
			
			//Create a buffer and link it to the receiving packets.
			rxBuffer = new byte[800];
			receivedPacket = new DatagramPacket(rxBuffer, rxBuffer.length);
			
			/*
			 * Listening phase - Receive packets sequentially and play them 
			 *                   to the speakers.
			 */
			do {
				
				try {
					receivingSocket.receive(receivedPacket);
					lineOut.write(rxBuffer, 0, rxBuffer.length);
				} catch (Exception e) {
					System.out.println(e);
				}
				
			} while(true);
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

}
