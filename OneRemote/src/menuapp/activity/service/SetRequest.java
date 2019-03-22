package menuapp.activity.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import menuapp.activity.R;
import menuapp.activity.intrface.Constants;
import menuapp.activity.secure.MyTrustManager;
import menuapp.activity.util.SharedPreferencesManager;
import android.content.Context;
import android.os.AsyncTask;

public class SetRequest {
	HttpsURLConnection connection;
	SharedPreferencesManager spm;
	boolean useClientAuth =true;
	private static final String TRUSTSTORE_PASSWORD = "mysecret";
    private static final String KEYSTORE_PASSWORD = "";
    //private static String trustStorePropDefault;
	public HttpsURLConnection postRequestonServer(HttpsURLConnection connection,
			URL url, String query, Context mcon) {
		try {
			
			spm = new SharedPreferencesManager(mcon);
			connection= (HttpsURLConnection)url.openConnection();
			localTrustStoreFile = new File(mcon.getFilesDir(), "mytruststore.bks");
			copyTrustStore(mcon);		   
			SSLContext sslCtx = createSslContext(useClientAuth,mcon);
		       			
			connection.setSSLSocketFactory(sslCtx.getSocketFactory());			
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(query.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
		}
		return connection;

	}

	public HttpsURLConnection getRequestonServer(HttpsURLConnection connection, URL url,
			Context mcon) {
		try {
			
			spm = new SharedPreferencesManager(mcon);
			localTrustStoreFile = new File(mcon.getFilesDir(), "mytruststore.bks");
			copyTrustStore(mcon);
		   
			SSLContext sslCtx = createSslContext(useClientAuth,mcon);
		       
			connection= (HttpsURLConnection)url.openConnection();
			connection.setSSLSocketFactory(sslCtx.getSocketFactory());
			connection.setRequestMethod("GET");	
			
			connection.setRequestProperty("access-token",
					spm.getStringValues(Constants.Token));
			connection.setRequestProperty("client",
					spm.getStringValues(Constants.Client));
			connection.setRequestProperty("expiry",
					spm.getStringValues(Constants.Expiry));
			connection.setRequestProperty("uid", spm.getStringValues(Constants.Uid));
			connection.setRequestProperty("token-type", "Bearer");
			
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
		}
		return connection;

	}

	public HttpsURLConnection postHeaderRequestonServer(HttpsURLConnection connection,
			URL url, String query, Context mcon) {
		try {
			
			spm = new SharedPreferencesManager(mcon);
			localTrustStoreFile = new File(mcon.getFilesDir(), "mytruststore.bks");
			copyTrustStore(mcon);
		    
			SSLContext sslCtx = createSslContext(useClientAuth,mcon);
		       
			connection= (HttpsURLConnection)url.openConnection();
			connection.setSSLSocketFactory(sslCtx.getSocketFactory());
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(query.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("access-token",
					spm.getStringValues(Constants.Token));
			connection.setRequestProperty("client",
					spm.getStringValues(Constants.Client));
			connection.setRequestProperty("expiry",
					spm.getStringValues(Constants.Expiry));
			connection.setRequestProperty("token-type", "Bearer");
			connection.setRequestProperty("uid", spm.getStringValues(Constants.Uid));

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
		}
		return connection;

	}

	public HttpsURLConnection deleteRequestonServer(HttpsURLConnection connection,
			URL url, Context mcon) {
		try {
			
			spm = new SharedPreferencesManager(mcon);
			localTrustStoreFile = new File(mcon.getFilesDir(), "mytruststore.bks");
			copyTrustStore(mcon);
		   
			SSLContext sslCtx = createSslContext(useClientAuth,mcon);
		       
			connection= (HttpsURLConnection)url.openConnection();
			connection.setSSLSocketFactory(sslCtx.getSocketFactory());
			connection.setRequestMethod("DELETE");

			connection.setRequestProperty("access-token",
					spm.getStringValues(Constants.Token));
			connection.setRequestProperty("client",
					spm.getStringValues(Constants.Client));
			connection.setRequestProperty("expiry",
					spm.getStringValues(Constants.Expiry));
			connection.setRequestProperty("token-type", "Bearer");
			connection.setRequestProperty("uid", spm.getStringValues(Constants.Uid));

		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
		}
		return connection;

	}

	public HttpsURLConnection putRequestonServer(HttpsURLConnection connection, URL url,
			String query, Context mcon) {
		try {
			
			spm = new SharedPreferencesManager(mcon);
			localTrustStoreFile = new File(mcon.getFilesDir(), "mytruststore.bks");
			copyTrustStore(mcon);		    
			SSLContext sslCtx = createSslContext(useClientAuth,mcon);
		       
			connection= (HttpsURLConnection)url.openConnection();
		    connection.setSSLSocketFactory(sslCtx.getSocketFactory());
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(query.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
		

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
		}
		return connection;

	}
	private void copyTrustStore(final Context con) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (localTrustStoreFile.exists()) {
                    return null;
                }

                try {
                    InputStream in = con.getResources().openRawResource(
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
	
	private SSLContext createSslContext(boolean clientAuth,Context com)
            throws GeneralSecurityException {
        KeyStore trustStore = loadTrustStore();
        KeyStore keyStore = loadKeyStore(com);

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
	 private File localTrustStoreFile;
	 private KeyStore keyStore;
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
	
}
