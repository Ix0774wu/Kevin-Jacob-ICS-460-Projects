import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;

public class Server2 {
	private final static int PORT = 2000;
	public static void main(String[] args) {
		//Take a file.  Needs to be changed for cmd input
		String fileName = "C:\\Users\\o0kma\\eclipse-workspace\\Testing Area\\src\\doggo.jpg";
		File file = new File(fileName);
		int fileSize = (int) file.length();
		//Put it into a byte[]
		byte[] bytesArray = new byte[fileSize];
		try (DatagramSocket socket = new DatagramSocket(PORT)) {
			while (true) {
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);
					
					String daytime = new Date().toString();
					byte[] data = daytime.getBytes("US-ASCII");
					DatagramPacket response = new DatagramPacket(bytesArray, bytesArray.length, request.getAddress(), request.getPort());
					socket.send(response);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
		
	}

}
