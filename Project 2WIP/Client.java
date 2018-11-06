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
		int ackno = 1;
		int packetStatus = 0;
		DatagramPacket acknoPacket = null;
		boolean failed = false;

		try (DatagramSocket socket = new DatagramSocket(0)){
			
			FileManager fileM = new FileManager(1024);
			
			System.out.println("enter download path");
			
			Scanner input = new Scanner(System.in);
			
			String path = input.next();

			socket.setSoTimeout(10000);

			InetAddress host = InetAddress.getByName(HOSTNAME);

			DatagramPacket request = new DatagramPacket(new byte[1], 1, host, PORT);

			DatagramPacket response = new DatagramPacket(new byte[1032], 1032);	
			
			

			socket.send(request);
			
			FileOutputStream fos = new FileOutputStream(path);
			
			socket.receive(response);
			boolean end = true;
			while(response.getLength() != 0 && end) {
				failed = false;
				try {
					
					//Break down packet
					byte[] packet = response.getData();
					ByteBuffer dataBB = ByteBuffer.wrap(packet);
					int inPacketStatus = dataBB.getInt();
					int seqno = dataBB.getInt();
					byte[] data = new byte[1024];
					dataBB.put(data);
					
					//Dealing with received packet problems
					if (ackno != seqno) {
						System.out.println(ackno +" "+ seqno);
						System.out.println("DUPL " + seqno + " !Seq");
					}
					else if (inPacketStatus == 0) {
						System.out.println("RECV " + seqno + " RECV");
						fileM.addPacket(data);
					}
					else if (inPacketStatus == 1) {
						System.out.println("RECV " + seqno + " CRPT");
						failed = true;
						
						/*ByteBuffer temp = ByteBuffer.allocate(8);
						temp.putInt(0);
						temp.putInt(seqno);
						System.out.println(seqno);
						byte[] tempArr = new byte[8];
						temp.get(tempArr);
						acknoPacket = new DatagramPacket(tempArr, tempArr.length, host, PORT);*/
					}
					
					
					//Set up acknowledgement
					byte[] ackArray = new byte[8];
					ByteBuffer ackBB = ByteBuffer.allocate(8);
					
					//Flag for corruption
					if(Math.random() < .1) {
						packetStatus = 1;
						ackBB.putInt(packetStatus);
					}
					//Flag for dropped packet
					if(Math.random() < .1) {
						packetStatus = 2;
						ackBB.putInt(packetStatus);
					}
					
					//Packet with no problems
					else
						ackBB.putInt(packetStatus);
					
					ackBB.putInt(ackno);
					ackBB.rewind();
					ackBB.get(ackArray);
					acknoPacket = new DatagramPacket(ackArray, ackArray.length, host, PORT);
					
					//Simulate packet drop
					if (failed != true) {
						if (packetStatus == 2) {
							System.out.println("SENDing ACK " + ackno + " DROP");
						}
						//Check for corruption
						else if(packetStatus == 1){
							socket.send(acknoPacket);
							System.out.println("SENDing ACK " + ackno + " ERR");
						}
						else {
							socket.send(acknoPacket);
							System.out.println("SENDing ACK " +ackno + " SENT");
							if (failed != true) {
								ackno++;
							}
						}
					}
					

					
					socket.receive(response);
				} catch (SocketTimeoutException e) {
					socket.send(acknoPacket);
					
					
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