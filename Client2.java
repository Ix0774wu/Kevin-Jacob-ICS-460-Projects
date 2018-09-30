import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client2 {
	private final static int PORT = 2001;
	private final static String HOSTNAME = "127.0.0.1";
	public static void main(String[] args) throws IOException {
		try (DatagramSocket socket = new DatagramSocket(0)){
			socket.setSoTimeout(10000);
			InetAddress host = InetAddress.getByName(HOSTNAME);
			DatagramPacket request = new DatagramPacket(new byte[1], 1, host, PORT);
			DatagramPacket response = new DatagramPacket(new byte[5999], 5999);
			socket.send(request);
			socket.receive(response);
			FileOutputStream fos = new FileOutputStream("output.jpg");
			fos.write(response.getData());
			fos.close();
			System.out.println("File created."); //Image is not correct after transfer yet
			System.out.println(response.getData().length);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
