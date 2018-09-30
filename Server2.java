import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;

public class Server2 {
	private final static int PORT = 2001;
	public static void main(String[] args) throws IOException {
		//Take a file.
		String fileName;
		if (args.length > 0) {
			fileName = args[0];
		}
		else fileName = "didn'twork.jpeg";
		File file = new File(fileName);
		int fileSize = (int) file.length();
		//Put it into a byte[]
		byte[] bytesArray = new byte[fileSize];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray);
		fis.close();
		try (DatagramSocket socket = new DatagramSocket(PORT)) {
			System.out.println("Waiting for Request...");
			while (true) {
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);
					System.out.println("Request Received.");
					
					DatagramPacket response = new DatagramPacket(bytesArray, bytesArray.length, request.getAddress(), request.getPort());
					socket.send(response);
					System.out.println("File Sent");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
		
	}

}
