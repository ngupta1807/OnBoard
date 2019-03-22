/*package menuapp.upnp.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import android.util.Log;
public class SendMSearch {
	public void SendMSearchMessage() {

		SSDPSearchMsg searchContentDirectory = new SSDPSearchMsg(
				SSDPConstants.ST_ContentDirectory);
		SSDPSearchMsg searchAVTransport = new SSDPSearchMsg(
				SSDPConstants.ST_AVTransport);
		SSDPSearchMsg searchProduct = new SSDPSearchMsg(
				SSDPConstants.ST_Product);

		SSDPSocket sock;
		try {
			sock = new SSDPSocket();
			for (int i = 0; i < 2; i++) {
				sock.send(searchContentDirectory.toString());
				sock.send(searchAVTransport.toString());
				sock.send(searchProduct.toString());
			}

			while (true) {
				DatagramPacket dp = sock.receive(); // Here, I only receive the
													// same packets I initially
													// sent above
				String c = new String(dp.getData());
				System.out.println(c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("M-SEARCH", e.getMessage());

		}
	}
}
*/