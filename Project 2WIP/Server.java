import java.io.File;

import java.io.FileInputStream;

import java.io.IOException;

import java.net.DatagramPacket;

import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Date;

import java.util.Scanner;



public class Server {

	private final static int PORT = 2001;

	public static void main(String[] args) throws IOException {
		int seqno = 1;
		int ackno = 0;
		int packetStatus = 0;
		boolean resend = false;
		DatagramPacket acknowledgement = null;
		DatagramPacket response = null;

		//Take a file.
		
		FileManager fileM = new FileManager(1024);

		System.out.println("enter path of file to send");
		
		Scanner input = new Scanner(System.in);
		
		String filePath = input.next();
		

		//Put it into a byte[]

		byte[] bytesArray = fileM.importFile(filePath);

		try (DatagramSocket socket = new DatagramSocket(PORT)) {

			System.out.println("Waiting for Request...");

			while (true) {

				try {

					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					acknowledgement = new DatagramPacket(new byte[1024],1024);

					socket.receive(request);

					byte[] packet = null;
					boolean init = true;
					while(packet != null || init) {
						init = false;
						packet = fileM.nextPacket();
						ByteBuffer sendBB = ByteBuffer.allocate(1032);
						
						//Simulate packet drop
						if (Math.random() < .1) {
							packetStatus = 1;
							System.out.println("SENDing " + seqno + " ERR");
						}
						
						else if (Math.random() < .1) {
							packetStatus = 2;
							System.out.println("SENDing " + seqno + " DROP");
						}
						
						else {
							packetStatus = 0;
							System.out.println("SENDing " + seqno + " SENT");
						}
						
						//Create packet with Header
						sendBB.putInt(packetStatus);
						sendBB.putInt(seqno);
						sendBB.put(packet);
						sendBB.rewind();
						packet = new byte[1032];
						sendBB.get(packet);
						response = new DatagramPacket(packet, fileM.getPacketLength() + 8, request.getAddress(), request.getPort());
						
						//Send Packet
						if (packetStatus != 2) {
							socket.send(response);
							System.out.println("waiting for ack");
						}

						socket.setSoTimeout(2000);
						
						socket.receive(acknowledgement);
						//System.out.println("ack received");
						
						//Break down acknowledgement
						byte [] acknodata = acknowledgement.getData();
						ByteBuffer bb = ByteBuffer.wrap(acknodata);
						int ackPacketStatus = bb.getInt();
						ackno = bb.getInt();
						
						
						//If the packet is a repeat
						if(ackno != seqno) {
							System.out.println("AckRcvd " + ackno + " DuplAck");
							socket.send(response);
						}
						
						else if (ackPacketStatus == 0) {
							System.out.println("AckRcvd " + ackno + " MoveWnd");
							seqno++;
						}
						else if (ackPacketStatus == 1){
							System.out.println("AckRcvd " + ackno + " ErrAck");
						}
							
						
					}
					byte[] end = null;
					socket.send( new DatagramPacket(end, 0, request.getAddress(), request.getPort()));
					

				//System.out.println("File Sent");
				//Null Pointer signals end of file.
				} catch (SocketTimeoutException ex) {
					System.out.println("Timeout " + (ackno + 1));
					resend = true;
					continue;
				}
				
				catch (NullPointerException ex) {
					System.out.println("File Sent");
					break;
					
				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}

		} catch (IOException ex){

			ex.printStackTrace();

		}

		

	}



}