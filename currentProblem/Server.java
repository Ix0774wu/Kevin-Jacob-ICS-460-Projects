import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;



public class Server {

    private final static int PORT = 2001;
    private static boolean reSend = false;

    public static void main(String[] args) throws IOException {
    	int expected = 1;
    	DatagramPacket response = null;

        //Take a file.

        FileManager fileM = new FileManager(1024);

        System.out.println("enter path of file to send");

        Scanner input = new Scanner(System.in);

        String filePath = input.next();

/** if (args.length > 0) {
            fileName = args[0];
        }
        else fileName = "didn'twork.jpeg";
**/


        //Put it into a byte[]

        byte[] bytesArray = fileM.importFile(filePath);
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
                    int ackno = 0;
                    if (reSend == false) 
                    	packet = fileM.nextPacket();
                    while(packet != null || init) {
                        init = false;
                        if (reSend == false) {
                        	response = new DatagramPacket(packet, fileM.getPacketLength(), request.getAddress(), request.getPort());
                        }
                        byte[] ackPacket = new byte[8];
                        if(Math.random() < .02){
                            System.out.println("[SENDing]:"+ (fileM.getSeqno(packet)) + " " + System.currentTimeMillis() + " [DRPT]" );
                            
                        }else{
                        socket.send(response);


                        if(ackno == fileM.getSeqno(packet))
                            System.out.println("[ReSENDing]:"+ (fileM.getSeqno(packet)) + " " + System.currentTimeMillis() + " [SENT]" );
                        else
                            System.out.println("[SENDing]:"+ (fileM.getSeqno(packet)) +  " " + System.currentTimeMillis() + " [SENT]");
                        }
                        socket.setSoTimeout(2000);
                        socket.receive(acknowledgement);
                        System.out.print("[AckRcvd]:");

                        //Break down acknowledgement
                        byte [] acknodata = acknowledgement.getData();
                        ByteBuffer bb = ByteBuffer.wrap(acknodata);
                        int corrupted = bb.getInt();
                        ackno = bb.getInt();
                        System.out.print(ackno + " " + System.currentTimeMillis());
                        
                        if (ackno == expected) {
                        
	                        if(corrupted == 1){
	                            fileM.Corrupt(packet);
	                            System.out.println(" [ErrAck]");
	                        }
	                        else{
	                            System.out.println(" [MoveWnd]");
	                            packet = fileM.nextPacket();
	                            expected++;
	
	                        }
                        }
                        else
                        	System.out.println(" [DuplAck]");
                        	
                        if(response.getLength() < 1024)
                            break;

                        reSend = false;
                    }
                    byte[] end = null;
                    socket.send( new DatagramPacket(end, 0, request.getAddress(), request.getPort()));


                //System.out.println("File Sent");
                //Null Pointer signals end of file.
                } catch (NullPointerException ex) {
                    System.out.println("File Sent");
                    break;

                } catch (SocketTimeoutException ex) {
                	System.out.println("Timeout");
                	reSend = true;
            	}catch (IOException ex) {

                    ex.printStackTrace();

                }

            }

        } catch (IOException ex){

            ex.printStackTrace();

        }



    }



}