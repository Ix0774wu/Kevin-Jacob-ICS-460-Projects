import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;

public class Server2 {
	private final static int PORT = 2001;
	public static void main(String[] args) throws IOException {
		//Take a file.  Needs to be changed for cmd input
		String fileName = "C:\\Users\\o0kma\\eclipse-workspace\\Testing Area\\src\\doggo.jpg";
		File file = new File(fileName);
		int fileSize = (int) file.length();
		//Put it into a byte[]
		byte[] bytesArray = new byte[fileSize];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray);
		fis.close();
		try (DatagramSocket socket = new DatagramSocket(PORT)) {
			System.out.println("Waiting...");
			while (true) {
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);
					
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
