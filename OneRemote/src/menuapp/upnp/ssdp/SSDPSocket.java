package menuapp.upnp.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

public class SSDPSocket {
	SocketAddress mSSDPMulticastGroup;
	MulticastSocket mSSDPSocket;
	InetAddress broadcastAddress;

	public SSDPSocket() throws IOException {
		mSSDPSocket = new MulticastSocket(); // Bind some random port for
		
		mSSDPSocket.setSoTimeout(20000);	// receiving datagram
		broadcastAddress = InetAddress.getByName(SSDPConstants.ADDRESS);
		System.out.println("broadcastAddress:."+broadcastAddress);
		
		mSSDPSocket.joinGroup(broadcastAddress);
	}

	/* Used to send SSDP packet */
	public void send(String data) throws IOException {
		System.out.println("Send");
		DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(),
				broadcastAddress, SSDPConstants.PORT);

		mSSDPSocket.send(dp);
	}

	/* Used to receive SSDP packet */
	public DatagramPacket receive() throws IOException {
		System.out.println("Receive");
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);

		System.out.println("mSSDPSocket"+mSSDPSocket);
		
		System.out.println("dp:..."+dp);
		
		mSSDPSocket.receive(dp);

		/*if (mSSDPSocket == null) {
			mSSDPSocket.close();
		}*/
		
		return dp;
	}

	public void close() {
		if (mSSDPSocket != null) {
			mSSDPSocket.close();
		}
	}
}
