

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;


public class FileManager {
	
	private File file;
	private int packetLength;
	private int index;
	private int packetCount;
	byte[] fileContent = null;
	public FileManager(int packetLength) {
		index = 0;
		packetCount = 1;
		this.packetLength = packetLength;
	}
	
	
	public int getIndex() {
		return index;
	}
	
	public int getPacketLength() {
		return packetLength;
	}
	
	public int getPacketCount() {
		return packetCount;
	}
	
	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}
	
    public byte[] importFile(String path) {
    	file = new File(path);
    	try {
			fileContent = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return fileContent;
    }
   
    public void exportFile(byte[] byteArray,String path) {
    	try (FileOutputStream outputStream = new FileOutputStream(path)) {
    		   outputStream.write(byteArray);
    		   
    		} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    public void addPacket(byte[] packet) {
    	int pStart = index;
    	int pEnd = index+packet.length;
    	System.out.println("["+packetCount+"]-["+pStart+"]-["+pEnd+"]");
    	fileContent = new byte[index+packet.length];
    	for(int i = 0; i < packet.length; i++) {
    		fileContent[i+index] = packet[i];
    		index ++;
    	}
    	
    	packetCount++;
    }
    
    public byte[] nextPacket() {
    	int pStart = index;
    	if((index+packetLength) > fileContent.length){
    		packetLength = fileContent.length - index;
    	}else if((index == fileContent.length)) {
    		byte[] end = null;
    		return end;
    	}
    	
    	int pEnd = index+packetLength;
    	System.out.println("["+packetCount+"]-["+pStart+"]-["+pEnd+"]");
    	byte[] packet = new byte[packetLength];
    	for(int i = 0; i < packetLength; i++) {
    		packet[i] = fileContent[index];
    		index++;
    	}
    	packetCount++;
    	return packet;
    }
}
