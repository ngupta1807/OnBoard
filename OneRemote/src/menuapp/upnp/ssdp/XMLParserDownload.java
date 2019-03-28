package menuapp.upnp.ssdp;

import java.net.URL;

import java.util.ArrayList;

import menuapp.activity.intrface.AsyncTaskCompleteListenerLoadLan;
import menuapp.activity.intrface.XMLDownloadInterface;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.LanModel;
import menuapp.activity.util.xmldata.XMLParser;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class XMLParserDownload extends AsyncTask<String, Void, String> {

	XMLDownloadInterface callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	SharedPreferencesManager spm;
	int code = 0;

	String presentationURL = "";
	String receivedurl = "";
	String response = "";

	public XMLParserDownload(Context context, XMLDownloadInterface callback,
			String receiveurl) {
		this.context = context;
		this.callback = callback;
		receivedurl = receiveurl;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Please Wait...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		response = xmlFile(receivedurl);
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			mProgressDialog.dismiss();
			callback.XMLselected(response);

		} catch (Exception ex) {
			System.out.println("Error in login complition:..."
					+ ex.getMessage());
		}
	}

	/* *//** Create a new layout to display the view */
	/*
	 * LinearLayout layout = new LinearLayout(this); layout.setOrientation(1);
	 *//** Create a new textview array to display the results */
	/*
	 * TextView name[]; TextView website[]; TextView category[];
	 */
	public String xmlFile(String receiveurl) {
		try {
			URL url = new URL(receiveurl);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(url.openStream()));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("device");

			System.out.println("nodeList:.." + nodeList.getLength());

			for (int i = 0; i < nodeList.getLength(); i++) {

				Element e = (Element) nodeList.item(i);

				System.out.println("presentationURL : "
						+ new XMLParser().getValue(e, "presentationURL"));

				String[] urlnew = receiveurl.split(":");
				String output = "http:" + urlnew[1] + ":52199";
				System.out.println("output:.." + output);

				presentationURL = output + ""
						+ new XMLParser().getValue(e, "presentationURL");
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println("node:.." + node);

				}

			}
			String ar[] = presentationURL.split("http://");
			System.out.println("ar.length:.." + ar.length);
			if (ar.length > 2) {
				presentationURL = "http://" + ar[1];
			} else {
				presentationURL = presentationURL;
			}

		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e);
		}
		return presentationURL;
	}
}

/** Set the layout view to display */
/* setContentView(layout); */

