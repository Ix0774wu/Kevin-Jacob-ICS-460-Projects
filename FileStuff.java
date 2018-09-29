import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileStuff {
	public static void main (String args[]) throws IOException {
		//Take a file
		String fileName = "C:\\Users\\o0kma\\eclipse-workspace\\Testing Area\\src\\doggo.jpg";
		File file = new File(fileName);
		int fileSize = (int) file.length();
		//Put it into a byte[]
		byte[] bytesArray = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray);
		fis.close();
		
		//Break bit[] into pieces
		int packetSize = 400;
		int numPackets = 15;
		BufferedInputStream bis = null;
		//bis = new BufferedInputStream(fis);
		for (int i = 0; i < numPackets; i++) {
			byte[] piece = new byte[packetSize];
			//bis.read(piece, 0, piece.length);
			System.out.println(piece.length);
		}
		
		//Create a new file from byte[]
		FileOutputStream fos = new FileOutputStream("output.jpg");
		fos.write(bytesArray);
		System.out.println("File created.");
		
	}

}
