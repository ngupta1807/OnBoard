package menuapp.activity.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import menuapp.activity.R;
import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.Constants;
import menuapp.activity.secure.MySSLSocketFactory;
import menuapp.activity.secure.MyTrustManager;
import menuapp.activity.util.SharedPreferencesManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class HttpManager extends AsyncTask<String, Void, String> {
	JSONObject jsonObject;
	String Url;
	Context context;
	AsyncTaskCompleteListener callback;
	SharedPreferencesManager spm;         
	ProgressDialog mProgressDialog;
	String objReturened = "";
	String file = "";
	int code = 0;
	FileEntity tmp = null;
	public static Boolean useClientAuth=true;
	private static final String TRUSTSTORE_PASSWORD = "mysecret";
    private static final String KEYSTORE_PASSWORD = "";
    private File localTrustStoreFile;
    private KeyStore keyStore;
	public HttpManager(Context context, AsyncTaskCompleteListener callback,
			String url, String file) {
		this.context = context;
		this.callback = callback;
		System.out.println("url:..." + url);
		Url = url;
		this.file = file;
		spm = new SharedPreferencesManager(context);
		localTrustStoreFile = new File(context.getFilesDir(), "mytruststore.bks");		
	    copyTrustStore(context);
	}
	private void copyTrustStore(final Context mContext) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (localTrustStoreFile.exists()) {
                    return null;
                }

                try {
                    InputStream in = mContext.getResources().openRawResource(
                            R.raw.mytruststore);
                    FileOutputStream out = new FileOutputStream(
                            localTrustStoreFile);
                    byte[] buff = new byte[1024];
                    int read = 0;

                    try {
                        while ((read = in.read(buff)) > 0) {
                            out.write(buff, 0, read);
                        }
                    } finally {
                        in.close();

                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        }.execute();
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
		byte bytes[] = null;
		try {

			File f = new File(file);

			tmp = new FileEntity(f, "UTF-8");

			HttpUriRequest method = new HttpPost(Url);

			SSLContext sslContext = createSslContext(useClientAuth,context);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(
                    sslContext, new BrowserCompatHostnameVerifier());
			
			HttpClient client = createHttpClient(socketFactory);
			

			client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
					CookiePolicy.RFC_2109);

			((HttpPost) method).setEntity(tmp);			

			((HttpPost) method).addHeader("access-token",
					spm.getStringValues(Constants.Token));
			((HttpPost) method).setHeader("client",
					spm.getStringValues(Constants.Client));
			((HttpPost) method).setHeader("expiry",
					spm.getStringValues(Constants.Expiry));
			((HttpPost) method).setHeader("token-type", "Bearer");
			((HttpPost) method).setHeader("uid",
					spm.getStringValues(Constants.Uid));
			((HttpPost) method).setHeader("Content-Type",
					"application/octet-stream");

			HttpResponse response = client.execute(method);
			HttpEntity entity1 = response.getEntity();

			code = response.getStatusLine().getStatusCode();

			objReturened = convertStreamToString(entity1.getContent());
			System.out.println("objReturened:....." + objReturened);

			Log.d("Tag", "Response = " + objReturened);

		} catch (Exception e) {
			Log.d("Tag", "Error Encountered" + e.getMessage());
			objReturened = "error";
		}
		return objReturened;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			mProgressDialog.dismiss();
			callback.onTaskComplete(objReturened, code);

		} catch (Exception ex) {
			System.out.println("Error in login complition:..."
					+ ex.getMessage());
		}
	}
	 private static final int MAX_CONN_PER_ROUTE = 10;
	 private static final int MAX_CONNECTIONS = 20;
	 private static final int TIMEOUT = 10 * 1000;
	 public HttpClient createHttpClient(SocketFactory socketFactory) {
	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setContentCharset(params,
	                HTTP.DEFAULT_CONTENT_CHARSET);
	        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
	        ConnPerRoute connPerRoute = new ConnPerRouteBean(MAX_CONN_PER_ROUTE);
	        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
	        ConnManagerParams.setMaxTotalConnections(params, MAX_CONNECTIONS);

	        SchemeRegistry schemeRegistry = new SchemeRegistry();
	        schemeRegistry.register(new Scheme("http", PlainSocketFactory
	                .getSocketFactory(), 80));
	        SocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
	        if (socketFactory != null) {
	            sslSocketFactory = socketFactory;
	        }
	        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
	        ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
	                schemeRegistry);

	        return new DefaultHttpClient(cm, params);
	    }
	 public SSLContext createSslContext(boolean clientAuth,Context mContext)
	            throws GeneralSecurityException {
	        KeyStore trustStore = loadTrustStore();
	        KeyStore keyStore = loadKeyStore(mContext);

	        MyTrustManager myTrustManager = new MyTrustManager(trustStore);
	        TrustManager[] tms = new TrustManager[] { myTrustManager };

	        KeyManager[] kms = null;
	        if (clientAuth) {
	            KeyManagerFactory kmf = KeyManagerFactory
	                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
	            kmf.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
	            kms = kmf.getKeyManagers();
	        }

	        SSLContext context = SSLContext.getInstance("TLS");
	        context.init(kms, tms, null);

	        return context;
	    }
	 private KeyStore loadTrustStore() {
	        try {
	            KeyStore localTrustStore = KeyStore.getInstance("BKS");
	            //            InputStream in = getResources().openRawResource(R.raw.mytruststore);
	            InputStream in = new FileInputStream(localTrustStoreFile);
	            try {
	                localTrustStore.load(in, TRUSTSTORE_PASSWORD.toCharArray());
	            } finally {
	                in.close();
	            }

	            return localTrustStore;
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private KeyStore loadKeyStore(Context con) {
	        if (keyStore != null) {
	            return keyStore;
	        }

	        try {
	            keyStore = KeyStore.getInstance("PKCS12");
	            InputStream in = con.getResources().openRawResource(R.raw.mykeystore);
	            try {
	                keyStore.load(in, KEYSTORE_PASSWORD.toCharArray());
	            } finally {
	                in.close();
	            }

	            return keyStore;
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
	    public static String convertStreamToString(InputStream is) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}
}
