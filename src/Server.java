import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;


public class Server {
	
	private static DatagramSocket receiveSocket;
	private static DatagramSocket tmpSendSocket;
	
	private static DatagramPacket sendPacket;
	private static DatagramPacket receivePacket;
	
	private static enum PacketType {READ, WRITE, INVALID};
	private static boolean serverIsAlive = true;
	
	private static String receivedFileName = "";
	private static String receivedModeName = "";

	public static void main(String[] args) throws Exception {
		
		receiveSocket = new DatagramSocket(6969);
		System.out.println("Server receiveSocket open at : " + receiveSocket.getLocalPort());
		
		while(serverIsAlive){
			byte[] buf = new byte[512];
			receivePacket = new DatagramPacket(buf, buf.length);
			receiveSocket.receive(receivePacket);
			
			//print out the information as byte array and string
			System.out.println("Server : Packet Received : " );
			System.out.println("From Address : " + receivePacket.getAddress() + ":" + receivePacket.getPort() );			
			System.out.println("Byte Array Value : " + Arrays.toString( Arrays.copyOfRange(receivePacket.getData(),0,receivePacket.getLength())));
			System.out.println(new String(buf,0,receivePacket.getLength()));
			
			respond(verifyIncomingPacket(receivePacket), receivePacket);			
			
		}
		
	}

	private static void respond(PacketType packetReceivedType, DatagramPacket receivePacket) throws Exception {
		if(packetReceivedType == PacketType.READ || packetReceivedType == PacketType.WRITE){
			
			byte[] sendBuf; 
			
			if(packetReceivedType == PacketType.READ){
				sendBuf = new byte[] {0,3,0,1};
			}else{
				sendBuf = new byte[] {0,4,0,0};
			}
			
			tmpSendSocket = new DatagramSocket();
			sendPacket = new DatagramPacket(sendBuf, sendBuf.length, receivePacket.getAddress(), receivePacket.getPort());
			
			System.out.println("Server : Packet Sending : ");
			System.out.println("From Address : " + sendPacket.getAddress() + ":" + sendPacket.getPort() );			
			System.out.println("Byte Array Value : " + Arrays.toString( Arrays.copyOfRange(sendPacket.getData(),0,sendPacket.getLength())));
			System.out.println(new String(sendBuf,0,sendPacket.getLength()));
			
			tmpSendSocket.send(sendPacket);
			tmpSendSocket.close();
			
		}else if(packetReceivedType == PacketType.INVALID){
			// server should shutdown smoothly here
		}
	}

	private static PacketType verifyIncomingPacket(DatagramPacket receivePacket) {
		
		byte[] receivedBuf = Arrays.copyOfRange(receivePacket.getData(),0,receivePacket.getLength());
		
		if(receivePacket.getLength() < 6 || receivedBuf[0] != 0){
			// invalid request
			return PacketType.INVALID;
		}
		
		if(receivedBuf[1] == 1 || receivedBuf[1] == 2){
			
			//figure out if valid file name provided
			for(int i=2; i<receivedBuf.length; i++){
				if(receivedBuf[i] == 0){
					if(receivedFileName.length() == 0){
						return PacketType.INVALID;
					}else{
						break;
					}
				}
				if(receivedBuf[i] != 0){
					receivedFileName += Character.toString( (char)receivedBuf[i] );
	            }
			}
			
			//figure out if valid mode name is specified
			int modeIndex = 1+1+ receivedFileName.length() + 1;
			for(int y = modeIndex; y<receivedBuf.length; y++){
				if(receivedBuf[y] == 0){
					if(receivedModeName.length() == 0){
						return PacketType.INVALID;
					}else{
						break;
					}
				}
				if(receivedBuf[y] != 0){
					receivedModeName += Character.toString( (char)receivedBuf[y] );
	            }
			}
			
			if(receivedFileName.length() == 0 || receivedModeName.length() < 1){
				return PacketType.INVALID;
			}
			
			if(receivedBuf[1] == 1){
				//read request
				return PacketType.READ;
			}else if(receivedBuf[1] == 2){
				//write request
				return PacketType.WRITE;
			}
			
		}else{
			//the request is invalid
			return PacketType.INVALID;
		}
		
		return PacketType.READ;
	}

}
