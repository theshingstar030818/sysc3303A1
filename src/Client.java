
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
	
	private enum RequestType {READ,WRITE,INVALID};
	private static DatagramPacket sendPacket;
	private static DatagramPacket receivePacket;
	private static DatagramSocket sendReceiveSocket;
	private static boolean flip = true;
	
	public static void main(String[] args) throws Exception {
		
		sendReceiveSocket = new DatagramSocket();
		System.out.println("Client sendReceiveSocket open at : " + sendReceiveSocket.getLocalPort());
		
		for(int i=0; i<11; i++){
			if(i == 10){
				sendRequest(RequestType.INVALID, "", "");
				receiveRequest();
				continue;
			}
			
			if(flip){
				sendRequest(RequestType.READ, "readFileName.txt", "netascci");
				receiveRequest();
			}else{
				sendRequest(RequestType.WRITE, "writeFileName.txt", "netascci");
				receiveRequest();
			}
			flip = !flip;
		}
	}
	
	private static void receiveRequest() throws Exception {
		byte[] buf = new byte[512];
		receivePacket = new DatagramPacket(buf, buf.length);
		sendReceiveSocket.receive(receivePacket);
		
		//line break to view pretty in console
		System.out.println();
		
		System.out.println("Client : Packet Received : " );
		System.out.println("From Address : " + receivePacket.getAddress() + ":" + receivePacket.getPort() );
		
		System.out.println("Byte Array Value : " + Arrays.toString( Arrays.copyOfRange(receivePacket.getData(),0,receivePacket.getLength())));
		System.out.println(new String(buf,0,receivePacket.getLength()));
		
		
	}

	private static void sendRequest(RequestType request, String fileName, String mode) throws Exception{
		 
		ArrayList<Byte> buf = new ArrayList<Byte>();
		
		if(request == RequestType.READ){
			System.out.println("Client Sending Read Request : ");
			buf.add((byte) 0b00);
			buf.add((byte)0b01);
			
		}else if(request == RequestType.WRITE){
			System.out.println("Client Sending Write Request : ");
			buf.add((byte) 0b00);
			buf.add((byte)0b10);
			
		}else {
			//manage invalid request
			System.out.println("Client Sending Invalid Request : ");
		}
		
		byte[] fileNameByteArray = fileName.getBytes();
		for(byte b : fileNameByteArray){
			buf.add(b);
		}
		
		buf.add( (byte) 0b00);
		byte[] modeByteArray = mode.getBytes();
		for(byte b : modeByteArray){
			buf.add(b);
		}
		buf.add( (byte) 0b00);
		byte[] byteBuf = new byte[buf.size()];
		for(int i=0; i<buf.size(); i++){
			byteBuf[i] = buf.get(i);
		}
		
		InetAddress serverAddress = InetAddress.getLocalHost();
		int serverPort = 6868;
		sendPacket = new DatagramPacket(byteBuf,byteBuf.length,serverAddress,serverPort);
		
		//print out the information as byte array and string
		System.out.println("Sending to Address : " + serverAddress + ":" + serverPort);
		System.out.println("Byte Array Value : " + Arrays.toString(sendPacket.getData()));
		System.out.println( new String(sendPacket.getData()) );
		
		//send data using sendReceiveSocket to the intermediate
		sendReceiveSocket.send(sendPacket);
		
	}

}
