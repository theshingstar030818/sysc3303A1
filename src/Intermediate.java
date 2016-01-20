
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Intermediate {
	
	private static DatagramSocket receiveSocket;
	private static DatagramSocket sendReceiveSocket;
	
	private static DatagramPacket sendClientPacket;
	private static DatagramPacket receiveClientPacket;
	
	private static DatagramPacket sendServerPacket;
	private static DatagramPacket receiveServerPacket;
	
	public static void main(String[] args) throws Exception {
		
		receiveSocket = new DatagramSocket(6868);
		sendReceiveSocket = new DatagramSocket();
		
		System.out.println("Intermediate receiveSocket open at : " + receiveSocket.getLocalPort());
		System.out.println("Intermediate sendReceiveSocket open at : " + sendReceiveSocket.getLocalPort());
		
		while(true){
			
			byte[] buf = new byte[512];
			receiveClientPacket = new DatagramPacket(buf, buf.length);			
			receiveSocket.receive(receiveClientPacket);
			
			//print out the information as byte array and string
			System.out.println("Intermediate : Packet Received : " );
			System.out.println("From Address : " + receiveClientPacket.getAddress() + ":" + receiveClientPacket.getPort() );			
			System.out.println("Byte Array Value : " + Arrays.toString( Arrays.copyOfRange(receiveClientPacket.getData(),0,receiveClientPacket.getLength())));
			System.out.println(new String(buf,0,receiveClientPacket.getLength()));
			
			byte[] bufToServer = Arrays.copyOfRange(receiveClientPacket.getData(),0,receiveClientPacket.getLength());
			InetAddress serverAddress = InetAddress.getLocalHost();
			int serverPort = 6969;
			sendServerPacket = new DatagramPacket(bufToServer, bufToServer.length, serverAddress, serverPort);
			
			//print out the information as byte array and string
			System.out.println("Intermediate Sending Packet : ");
			System.out.println("Sending to Address : " + serverAddress + ":" + serverPort);
			System.out.println("Byte Array Value : " + Arrays.toString(sendServerPacket.getData()));
			System.out.println( new String(sendServerPacket.getData()) );
			
			//now forward this packet to the server
			sendReceiveSocket.send(sendServerPacket);
			
			
			receiveServerPacket = new DatagramPacket(buf, buf.length);
			sendReceiveSocket.receive(receiveServerPacket);
			
			//print out the information as byte array and string
			System.out.println("Intermediate : Packet Received : " );
			System.out.println("From Address : " + receiveServerPacket.getAddress() + ":" + receiveServerPacket.getPort() );			
			System.out.println("Byte Array Value : " + Arrays.toString( Arrays.copyOfRange(receiveServerPacket.getData(),0,receiveServerPacket.getLength())));
			System.out.println(new String(buf,0,receiveServerPacket.getLength()));
			
			
			byte[] bufToClient = Arrays.copyOfRange(receiveServerPacket.getData(),0,receiveServerPacket.getLength());
			InetAddress clientAddress = receiveClientPacket.getAddress();
			int clientPort = receiveClientPacket.getPort();
			sendClientPacket = new DatagramPacket(bufToClient, bufToClient.length, clientAddress, clientPort);
			
			//print out the information as byte array and string
			System.out.println("Intermediate Sending Packet : ");
			System.out.println("Sending to Address : " + clientAddress + ":" + clientPort);
			System.out.println("Byte Array Value : " + Arrays.toString(sendClientPacket.getData()));
			System.out.println( new String(sendClientPacket.getData()) );
			
			//now forward this packet to the client by creating a new DatagramSocket 
			DatagramSocket tmpSendSocket = new DatagramSocket();
			tmpSendSocket.send(sendClientPacket);
			tmpSendSocket.close();
		}
		
	}

}
