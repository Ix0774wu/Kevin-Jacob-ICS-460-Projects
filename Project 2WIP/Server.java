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
		int packetStatus;  //signals if packet is intact, corrupt, or dropped
		int seqno = 1;  //The sequence number of the packet
		int fileSize;

		
		//Create a file Manager
		FileManager fileM = new FileManager(1024);

		System.out.println("enter path of file to send");
		
		Scanner input = new Scanner(System.in);
		
		String filePath = input.next();
		
/**	if (args.length > 0) {
			fileName = args[0];
		}
		else fileName = "didn'twork.jpeg";
**/		
		

		//Put it into a byte[]

		byte[] bytesArray = fileM.importFile(filePath);
		//determine size of file
		fileSize = fileM.getFileSize();
/**
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray);
		fis.close();
**/
		try (DatagramSocket socket = new DatagramSocket(PORT)) {

			System.out.println("Waiting for Request...");

			while (true) {

				try {

					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					DatagramPacket acknowledgement = new DatagramPacket(new byte[1024],1024);

					socket.receive(request);

					System.out.println("Request Received.");
					byte[] packet = null;
					boolean init = true;
					while(packet != null || init) {
						init = false;
						packet = fileM.nextPacket();
						
						//Simulate Noise
						//Corrupt
						if (Math.random() < .1) {
							packetStatus = 1;
						}
						//Drop
						else if (Math.random() < .1) {
							packetStatus = 2;
						}
						//Intact
						else {
							packetStatus = 0;
						}
						
						//Use byte buffer to create packet with header
						ByteBuffer sendBB = ByteBuffer.allocate(1036);
						sendBB.putInt(packetStatus);
						sendBB.putInt(seqno);
						sendBB.putInt(fileSize);
						sendBB.put(packet);
						sendBB.rewind();
						packet = new byte[1036];
						sendBB.get(packet);
						DatagramPacket response = new DatagramPacket(packet, 1036, request.getAddress(), request.getPort());
						if (fileSize > 1024) {
							fileSize = fileSize - 1024 ;
						}
						else {
							fileSize = 0;
						}
						
						socket.send(response);
						
						//Messages based on packet status
						//Corrupt
						if (packetStatus == 1) {
							System.out.println("SENDing " + seqno + " ERR");
						}
						//Drop
						else if (packetStatus == 2) {
							packetStatus = 2;
							System.out.println("SENDing " + seqno + " DROP");
						}
						//Intact
						else {
							System.out.println("SENDing " + seqno + " SENT");
							seqno++;
							
						}

						
						
						//Acknowledgement
						socket.receive(acknowledgement);
						
						//Break down acknowledgement
						byte [] acknodata = acknowledgement.getData();
						ByteBuffer bb = ByteBuffer.wrap(acknodata);
						int ackPacketStatus = bb.getInt();
						int ackno = bb.getInt();
							
						//Packet is intact
						if (ackPacketStatus == 0) {
							System.out.println("ackRcvd " + ackno + " MoveWnd");
						}
						//Packet is Corrupted
						else if (ackPacketStatus == 1) {
							System.out.println("ackRcvd " + ackno + " ErrAck");
							acknodata = acknowledgement.getData();
							ByteBuffer ackBB = ByteBuffer.wrap(acknodata);
							ackPacketStatus = bb.getInt();
							ackno = bb.getInt();
						}
						//Packet is dropped
						else  {
							System.out.println("dropped " + ackno);
							acknodata = acknowledgement.getData();
							ByteBuffer ackBB = ByteBuffer.wrap(acknodata);
							ackPacketStatus = bb.getInt();
							ackno = bb.getInt();
							
						}
						//System.out.println(ackno);
						//System.out.println(corrupted);
						}
						
					
					byte[] end = null;
					socket.send( new DatagramPacket(end, 0, request.getAddress(), request.getPort()));
					

				//System.out.println("File Sent");
				//Null Pointer signals end of file.
				} catch (SocketTimeoutException ex) {
					//do something
				} catch (NullPointerException ex) {
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