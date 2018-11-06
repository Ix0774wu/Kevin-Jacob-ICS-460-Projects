import java.io.*;

import java.net.DatagramPacket;

import java.net.DatagramSocket;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Scanner;



public class Client {
	
	

	private final static int PORT = 2001;

	private final static String HOSTNAME = "127.0.0.1";
	
	
	
	

	public static void main(String[] args) throws IOException {
		boolean packetsRemaining = true;
		int ackno = 1;
		int seqno;  //The sequence number of the received packet
		int packetStatus;  //Signals if the received packet is corrupted
		int ackPacketStatus;

		try (DatagramSocket socket = new DatagramSocket(0)){
			
			FileManager fileM = new FileManager(1024);
			
			System.out.println("enter download path");
			
			Scanner input = new Scanner(System.in);
			
			String path = input.next();

			socket.setSoTimeout(10000);

			InetAddress host = InetAddress.getByName(HOSTNAME);

			DatagramPacket request = new DatagramPacket(new byte[1], 1, host, PORT);

			DatagramPacket response = new DatagramPacket(new byte[1036], 1036);	
			
			

			socket.send(request);
			
			FileOutputStream fos = new FileOutputStream(path);
			
			
			socket.receive(response);
			
			while(packetsRemaining == true) {
				try {
					
					//Extract Header from response
					byte[] packet = response.getData();
					ByteBuffer bb = ByteBuffer.wrap(packet);
					packetStatus = bb.getInt();
					seqno = bb.getInt();
					if (bb.getInt() > 0) {
						packetsRemaining = true;
					}
					else {
						packetsRemaining = false;
					}
					System.out.println(packetsRemaining);
					
					//Send file data to file manager
					byte[] data = new byte[1024];
					System.arraycopy(packet, 12, data, 0, 1024);
					fileM.addPacket(data);
					
					
					//Set up acknowledgement
					byte[] ackArray = new byte[8];
					ByteBuffer ackBB = ByteBuffer.allocate(8);
					
					//Simulate Noise
					//Corrupt
					if (Math.random() < .1) {
						ackPacketStatus = 1;
					}
					//Drop
					else if(Math.random() < .1) {
						ackPacketStatus = 2;
					}
					//Intact
					else {
						ackPacketStatus = 0;
					}
					ackBB.putInt(ackPacketStatus);
					System.out.println(ackPacketStatus);
					ackBB.putInt(ackno);
					ackBB.rewind();
					ackBB.get(ackArray);
					DatagramPacket acknoPacket = new DatagramPacket(ackArray, ackArray.length, host, PORT);
					socket.send(acknoPacket);
					System.out.println("ackno " + ackno);
					System.out.println("seqno " + seqno);
					System.out.println("Sending ack " + ackno);
					socket.setSoTimeout(3000);
					if (packetStatus == 0) {
						ackno++;
					}

					
					socket.receive(response);
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					
				}
				
			}
			fos.write(fileM.fileContent);

			fos.close();

			System.out.println("File created.");

			//Testing System.out.println(response.getData().length);

		} catch (IOException ex) {

			ex.printStackTrace();

		}

	}
	




}