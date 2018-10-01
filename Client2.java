import java.io.*;

import java.net.DatagramPacket;

import java.net.DatagramSocket;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;



public class Client2 {
	
	

	private final static int PORT = 2001;

	private final static String HOSTNAME = "127.0.0.1";
	
	

	public static void main(String[] args) throws IOException {

		try (DatagramSocket socket = new DatagramSocket(0)){
			
			FileManager fileM = new FileManager(1024);
			
			System.out.println("enter download path");
			
			Scanner input = new Scanner(System.in);
			
			String path = input.next();

			socket.setSoTimeout(10000);

			InetAddress host = InetAddress.getByName(HOSTNAME);

			DatagramPacket request = new DatagramPacket(new byte[1], 1, host, PORT);

			DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

			socket.send(request);
			
			FileOutputStream fos = new FileOutputStream(path);

			socket.receive(response);
			boolean end = true;
			while(response.getLength() != 0 && end) {
				try {
					fileM.addPacket(response.getData());
					socket.receive(response);
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					end = false;
					
				}
				
			}
			
			fos.write(fileM.fileContent);

			fos.close();

			System.out.println("File created."); //Image is not correct after transfer yet

			System.out.println(response.getData().length);

		} catch (IOException ex) {

			ex.printStackTrace();

		}

	}



}