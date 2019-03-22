package menuapp.activity.util;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import menuapp.activity.ChooseOption;
import menuapp.activity.ListCategory;
import menuapp.activity.R;
import menuapp.activity.Setting;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.intrface.AlertInterface;
import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.AsyncTaskCompleteListenerLoadLan;
import menuapp.activity.intrface.Constants;
import menuapp.activity.intrface.ImageInterface;
import menuapp.activity.intrface.SdcardLoadInterface;
import menuapp.activity.intrface.XMLDownloadInterface;
import menuapp.activity.service.DownloadZip;
import menuapp.activity.service.GetListOfDownload;
import menuapp.activity.service.HttpManager;
import menuapp.activity.service.LoadLan;
import menuapp.activity.util.adapter.SdcardDataAdapter;
import menuapp.activity.util.adapter.ServerDataAdapter;
import menuapp.activity.util.model.GetDownloadModel;
import menuapp.activity.util.model.LanModel;
import menuapp.activity.validation.LoadList;
import menuapp.upnp.ssdp.XMLParserDownload;

import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAlertDialog implements SdcardLoadInterface,
		AsyncTaskCompleteListener, AsyncTaskCompleteListenerLoadLan,
		XMLDownloadInterface {
	Context mcon;
	AlertInterface ai;
	ImageInterface imageLoad;
	ArrayList<File> fileList = new ArrayList<File>();
	ArrayList<File> folderList = new ArrayList<File>();
	ArrayList<LanModel> landata = new ArrayList<LanModel>();
	String touchedevent = "";
	ArrayList<GetDownloadModel> arraylist = new ArrayList<GetDownloadModel>();
	InternetReachability ir;
	String dialogload = "";
	String click = "";
	SharedPreferencesManager spm;
	String tv_ip, tv_url;

	public CustomAlertDialog(Context con, AlertInterface ai) {
		mcon = con;
		this.ai = ai;
	}

	public CustomAlertDialog(Context con, ImageInterface imageLoad, String type) {
		mcon = con;
		this.imageLoad = imageLoad;
	}

	public CustomAlertDialog(Context con) {
		mcon = con;
	}

	public void showValidate(final String message, boolean isCancelable) {

		AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mcon);

		builder.setCancelable(isCancelable);
		if (message.equals(mcon.getString(R.string.server_error))) {
			builder.setTitle("");
		} else {
			builder.setTitle(mcon.getResources().getString(R.string.app_name));
		}
		builder.setMessage(message);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();

	}

	public void showDialog(final String message, boolean isCancelable) {

		AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mcon);

		builder.setCancelable(isCancelable);

		builder.setTitle(mcon.getResources().getString(R.string.app_name));

		builder.setMessage(message);

		builder.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ai.selected("delete");
					}
				});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ai.selected("cancel");
					}
				});

		builder.show();

	}

	public void showOkDialog(final String message, boolean isCancelable) {

		AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mcon);

		builder.setCancelable(isCancelable);

		builder.setTitle(mcon.getResources().getString(R.string.app_name));

		builder.setMessage(message);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ai.selected("ok");
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ai.selected("cancel");
					}
				});

		builder.show();

	}

	String pvalue = "";

	public void CustomImageDialog() {

		final Dialog dialog = new Dialog(mcon);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.load_image);
		dialog.setCancelable(true);

		Button photo = (Button) dialog.findViewById(R.id.photo);
		Button gallery = (Button) dialog.findViewById(R.id.gallery);
		Button cancel = (Button) dialog.findViewById(R.id.cancel);
		ImageView change = (ImageView) dialog.findViewById(R.id.change);

		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mcon, ChooseOption.class);
				mcon.startActivity(intent);
			}
		});

		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Photo");
				imageLoad.Pvalue("Photo");
				dialog.dismiss();
			}
		});

		gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Gallery");
				imageLoad.Pvalue("Gallery");
				dialog.dismiss();

			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	Dialog imagedialog;

	public void CustomzippedTestDialog() {

		imagedialog = new Dialog(mcon);
		imagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		imagedialog.setContentView(R.layout.load_sdcard_data);
		imagedialog.setCancelable(true);
		ir = new InternetReachability(mcon);
		listView = (ListView) imagedialog.findViewById(R.id.load);
		tv = (TextView) imagedialog.findViewById(R.id.tv);
		final TextView choose = (TextView) imagedialog
				.findViewById(R.id.choose);

		TextView app_text = (TextView) imagedialog.findViewById(R.id.app_text);

		Button save_local = (Button) imagedialog.findViewById(R.id.save_local);

		Button save_cloud = (Button) imagedialog.findViewById(R.id.save_cloud);

		Button save_broker = (Button) imagedialog
				.findViewById(R.id.save_broker);

		ImageView change = (ImageView) imagedialog.findViewById(R.id.change);

		app_text.setVisibility(View.GONE);

		change.setVisibility(View.GONE);
		choose.setVisibility(View.GONE);

		save_local.setVisibility(View.GONE);
		save_local.setText("Device");

		save_cloud.setVisibility(View.GONE);
		save_cloud.setText("Cloud");

		save_broker.setVisibility(View.GONE);
		save_broker.setText("LAN");

		choose.setText("Choose Backup");

		WifiManager wm = (WifiManager) mcon
				.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock = wm
				.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
		dialogload = "test";  

		LoadLan ll = new LoadLan(mcon, CustomAlertDialog.this);
		ll.execute();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				click = "click";
				String url = ((TextView) arg1.findViewById(R.id.tv_host))
						.getText().toString();

				XMLParserDownload xmd = new XMLParserDownload(mcon,
						CustomAlertDialog.this, url);
				xmd.execute();

			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				click = "longclick";
				spm = new SharedPreferencesManager(mcon);

				tv_ip = ((TextView) arg1.findViewById(R.id.tv_ip)).getText()
						.toString();
				tv_url = ((TextView) arg1.findViewById(R.id.tv_url)).getText()
						.toString();
				String url = ((TextView) arg1.findViewById(R.id.tv_host))
						.getText().toString();
				System.out.println("tv_url:..." + tv_url);
				XMLParserDownload xmd = new XMLParserDownload(mcon,
						CustomAlertDialog.this, url);
				xmd.execute();

				/*
				 * listView.setVisibility(View.GONE);
				 * back.setVisibility(View.VISIBLE);
				 * next.setVisibility(View.VISIBLE);
				 * ex_data.setVisibility(View.VISIBLE);
				 * link.setVisibility(View.VISIBLE);
				 * name.setVisibility(View.VISIBLE);
				 * 
				 * back.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { if
				 * (name.getText().toString().length() == 0) {
				 * Toast.makeText(mcon, "Please add name.", 2000) .show(); }
				 * else if (link.getText().toString().length() == 0) {
				 * Toast.makeText(mcon, "Please add link.", 2000) .show(); }
				 * else { listView.setVisibility(View.VISIBLE);
				 * back.setVisibility(View.GONE); next.setVisibility(View.GONE);
				 * ex_data.setVisibility(View.GONE);
				 * link.setVisibility(View.GONE); name.setVisibility(View.GONE);
				 * } } });
				 */

				return false;
			}
		});

	}

	File root = null;
	ListView listView;
	TextView tv;
	String btnclicked = "";
	String file_name = "";

	public void CustomzippedDialog() {

		final Dialog dialog = new Dialog(mcon);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.load_sdcard_data);
		dialog.setCancelable(true);
		ir = new InternetReachability(mcon);
		spm = new SharedPreferencesManager(mcon);
		listView = (ListView) dialog.findViewById(R.id.load);
		tv = (TextView) dialog.findViewById(R.id.tv);
		final TextView choose = (TextView) dialog.findViewById(R.id.choose);

		Button save_local = (Button) dialog.findViewById(R.id.save_local);

		Button save_cloud = (Button) dialog.findViewById(R.id.save_cloud);

		Button save_broker = (Button) dialog.findViewById(R.id.save_broker);

		ImageView change = (ImageView) dialog.findViewById(R.id.change);

		change.setVisibility(View.VISIBLE);

		save_local.setVisibility(View.VISIBLE);
		save_local.setText("Device");

		
		if (spm.getStringValues(Constants.Token).equals("0")) {
			save_cloud.setVisibility(View.GONE);
			save_cloud.setText("Cloud");
		} else {
			save_cloud.setVisibility(View.VISIBLE);
			save_cloud.setText("Cloud");
		}
		
		

		save_broker.setVisibility(View.VISIBLE);
		save_broker.setText("LAN");

		choose.setText("Choose Backup");

		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mcon, "In progress...", 2000).show();
			}
		});

		save_broker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * btnclicked = "broker"; fileList.clear(); arraylist.clear();
				 * landata.clear(); dialogload = "live"; WifiManager wm =
				 * (WifiManager) mcon .getSystemService(Context.WIFI_SERVICE);
				 * WifiManager.MulticastLock multicastLock = wm
				 * .createMulticastLock("multicastLock");
				 * multicastLock.setReferenceCounted(true);
				 * multicastLock.acquire();
				 * 
				 * LoadLan ll = new LoadLan(mcon, CustomAlertDialog.this);
				 * ll.execute();
				 */

				Toast.makeText(mcon, "In progress...", 2000).show();

				// saveBroker();
			}

		});

		save_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fileList.clear();
				arraylist.clear();
				landata.clear();
				btnclicked = "local";
				root = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath());
				fileList = getfile();
				if (fileList.size() > 0) {
					listView.setVisibility(View.VISIBLE);
					Collections.reverse(fileList);
					SdcardDataAdapter dataAdapter = new SdcardDataAdapter(mcon,
							fileList, mcon.getResources());

					listView.setAdapter(dataAdapter);

				} else {
					listView.setVisibility(View.GONE);
					tv.setVisibility(View.VISIBLE);
				}

			}
		});

		save_cloud.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fileList.clear();
				arraylist.clear();
				landata.clear();
				btnclicked = "cloud";
				touchedevent = "list";

				if (ir.isConnected()) {
					GetListOfDownload gld = new GetListOfDownload(mcon,
							CustomAlertDialog.this, Constants.getlist
									+ "?format=json");
					gld.execute();
				} else {
					showValidate(mcon.getString(R.string.internet_error), true);
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String path = ((TextView) view.findViewById(R.id.tv_id))
						.getText().toString();
				System.out.println("Id:.." + path);
				if (btnclicked.equals("cloud")) {
					touchedevent = "download";
					dialog.dismiss();
					file_name = ((TextView) view.findViewById(R.id.tv_name))
							.getText().toString();
					if (ir.isConnected()) {
						DownloadZip Pu = new DownloadZip(mcon,
								CustomAlertDialog.this, Constants.URL + ""
										+ Constants.download + path, file_name);
						Pu.execute();
					}

				} else if (btnclicked.equals("broker")) {
					click = "click";
					String url = ((TextView) view.findViewById(R.id.tv_host))
							.getText().toString();

					XMLParserDownload xmd = new XMLParserDownload(mcon,
							CustomAlertDialog.this, url);
					xmd.execute();
				} else {

					showOkDialog(path, true, "zipp");
					dialog.dismiss();
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				click = "longclick";

				spm = new SharedPreferencesManager(mcon);

				tv_ip = ((TextView) arg1.findViewById(R.id.tv_ip)).getText()
						.toString();
				tv_url = ((TextView) arg1.findViewById(R.id.tv_url)).getText()
						.toString();
				String url = ((TextView) arg1.findViewById(R.id.tv_host))
						.getText().toString();

				XMLParserDownload xmd = new XMLParserDownload(mcon,
						CustomAlertDialog.this, url);
				xmd.execute();

				/*
				 * String tv_ip = ((TextView) arg1.findViewById(R.id.tv_ip))
				 * .getText().toString(); String tv_url = ((TextView)
				 * arg1.findViewById(R.id.tv_url)) .getText().toString();
				 * 
				 * name.setText(tv_ip); link.setText(tv_url);
				 * 
				 * listView.setVisibility(View.GONE);
				 * back.setVisibility(View.VISIBLE);
				 * next.setVisibility(View.VISIBLE);
				 * ex_data.setVisibility(View.VISIBLE);
				 * link.setVisibility(View.VISIBLE);
				 * name.setVisibility(View.VISIBLE);
				 * 
				 * back.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) {
				 * 
				 * 
				 * if (name.getText().toString().length() == 0) {
				 * Toast.makeText(mcon, "Please add name.", 2000) .show(); }
				 * else if (link.getText().toString().length() == 0) {
				 * Toast.makeText(mcon, "Please add link.", 2000) .show(); }
				 * else { listView.setVisibility(View.VISIBLE); } } });
				 */

				return false;
			}
		});

		dialog.show();

	}

	/*
	 * public void saveBroker() {
	 * 
	 * SSDPSearchMsg search = new SSDPSearchMsg(SSDP.ST_ContentDirectory);
	 * System.out.println("data:.." + search.toString());
	 * 
	 * SSDPSocket sock; try { StrictMode.ThreadPolicy policy = new
	 * StrictMode.ThreadPolicy.Builder() .detectAll().penaltyLog().build();
	 * StrictMode.setThreadPolicy(policy);
	 * 
	 * sock = new SSDPSocket();
	 * 
	 * sock.send(search.toString()); DatagramPacket dp = sock.receive();
	 * 
	 * String c = new String(dp.getData()); if (c.length() > 0) { LanModel lm =
	 * new LanModel();
	 * 
	 * System.out.println("data:.." + c); lm.setKEY_name(c); landata.add(lm); }
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	String valuesave = "";
	EditText value;

	public void CustomFolderDialog(final DbAdapter mhelper) {

		final Dialog dialog = new Dialog(mcon);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.load_sdcard_data);
		dialog.setCancelable(true);
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		ir = new InternetReachability(mcon);
		System.out.println("cat_id:.."
				+ spm.getStringValues(mcon.getString(R.string.Cat_id)));
		final int catid = Integer.parseInt(spm.getStringValues(mcon
				.getString(R.string.Cat_id)));
		final ListView listView = (ListView) dialog.findViewById(R.id.load);
		final TextView choose = (TextView) dialog.findViewById(R.id.choose);
		final TextView tv = (TextView) dialog.findViewById(R.id.tv);

		final String path = Environment.getExternalStorageDirectory()
				+ "/OneRemote";
		value = (EditText) dialog.findViewById(R.id.value);
		final Button save_branch = (Button) dialog
				.findViewById(R.id.save_branch);
		final Button save_root = (Button) dialog.findViewById(R.id.save_root);

		final Button save_cloud = (Button) dialog.findViewById(R.id.save_cloud);
		final Button save_local = (Button) dialog.findViewById(R.id.save_local);
		final Button save_broker = (Button) dialog
				.findViewById(R.id.save_broker);

		value.setVisibility(View.VISIBLE);

		if (spm.getStringValues(Constants.Token).equals("0")) {
			save_cloud.setVisibility(View.GONE);
		} else {
			save_cloud.setVisibility(View.VISIBLE);
		}

		
		
		save_local.setVisibility(View.VISIBLE);
		save_broker.setVisibility(View.VISIBLE);

		save_branch.setVisibility(View.GONE);
		save_root.setVisibility(View.GONE);

		save_broker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				 /*
				 * LoadLan ll=new LoadLan(mcon, CustomAlertDialog.this);
				 * ll.execute();
				 */
				// saveBroker();
				
				Toast.makeText(mcon, "In progress...", 2000).show();
			}
		});
		save_cloud.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save_cloud.setVisibility(View.GONE);
				save_local.setVisibility(View.GONE);
				save_broker.setVisibility(View.GONE);
				valuesave = "cloud";
				if (catid == 0) {
					touchedevent = "upload";
					if (ir.isConnected()) {
						ZippingData mobileAPI = new ZippingData(mcon,
								CustomAlertDialog.this, path, value.getText()
										.toString(), "0");
						mobileAPI.execute();
						dialog.dismiss();
					} else {
						showValidate(mcon.getString(R.string.internet_error),
								true);
					}
				} else {
					save_branch.setVisibility(View.VISIBLE);
					save_root.setVisibility(View.VISIBLE);
				}

			}
		});

		save_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				save_local.setVisibility(View.GONE);
				save_cloud.setVisibility(View.GONE);
				save_broker.setVisibility(View.GONE);
				valuesave = "local";
				if (catid == 0) {
					ZippingData mobileAPI = new ZippingData(mcon,
							CustomAlertDialog.this, path, value.getText()
									.toString(), "0");
					mobileAPI.execute();
					dialog.dismiss();
				} else {
					System.out.println("checking 120 mb.");
					save_branch.setVisibility(View.VISIBLE);
					save_root.setVisibility(View.VISIBLE);
				}

			}
		});
		listView.setVisibility(View.GONE);
		choose.setText("Save");
		final String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

		if (spm.getStringValues(mcon.getString(R.string.Cat_id)).equals("0")) {
			value.setText("" + date + "_ORsave");
			// value.setText("" + System.currentTimeMillis() + "_ORsave");
		} else {
			String cat_name = new GetDataFrmDB().getCatNameByID(catid, mhelper);
			value.setText("" + date + "_" + cat_name);
			// value.setText("" + System.currentTimeMillis() + "_" + cat_name);
		}
		save_root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				System.out.println("checking device root.");

				if (valuesave.equals("cloud")) {
					touchedevent = "upload";
					if (ir.isConnected()) {
						ZippingData mobileAPI = new ZippingData(mcon,
								CustomAlertDialog.this, path, value.getText()
										.toString(), "0");
						mobileAPI.execute();
						dialog.dismiss();
					} else {
						showValidate(mcon.getString(R.string.internet_error),
								true);
					}
				} else {

					ZippingData mobileAPI = new ZippingData(mcon,
							CustomAlertDialog.this, path, value.getText()
									.toString(), "0");
					mobileAPI.execute();
					dialog.dismiss();

				}
			}
		});
		save_branch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				System.out.println("checking 120 mb clikc device branch.");

				if (valuesave.equals("cloud")) {
					touchedevent = "upload";
					if (ir.isConnected()) {
					
						ZippingData mobileAPI = new ZippingData(mcon,
								CustomAlertDialog.this, path, value.getText()
										.toString(), "1");
						mobileAPI.execute();
						dialog.dismiss();
						/*ZippingData mobileAPI = new ZippingData(mcon,
								CustomAlertDialog.this, path, value.getText()
										.toString(), "0");
						mobileAPI.execute();
						dialog.dismiss();*/
						
						
					} else {
						showValidate(mcon.getString(R.string.internet_error),
								true);
					}
				} else {
					ZippingData mobileAPI = new ZippingData(mcon,
							CustomAlertDialog.this, path, value.getText()
									.toString(), "1");
					mobileAPI.execute();
					dialog.dismiss();
				}

			}
		});
		dialog.show();

	}

	String m_chosenDir = "";
	boolean m_newFolderEnabled = true;

	public void showOkDialog(final String message, boolean isCancelable,
			final String type) {

		AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mcon);

		builder.setCancelable(isCancelable);

		builder.setTitle(mcon.getResources().getString(R.string.app_name));

		builder.setMessage("Do you want to replace the current menu");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ExtractingData mobileAPI = new ExtractingData(mcon,
						CustomAlertDialog.this, message, m_chosenDir);
				mobileAPI.execute();
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();

	}

	@Override
	public void done(String result) {
		final File dir = new File(Environment.getExternalStorageDirectory()
				+ "/OneRemote" + "/node");

		DeleteRecursive(dir);

		final File dirone = new File(Environment.getExternalStorageDirectory()
				+ "/ORsave" + "/Menu");
		DeleteRecursive(dirone);

		if (result.equals("folder") && touchedevent.equals("upload")) {

			// touchedevent = "delete";
			/*
			 * DeleteProfile dp = new DeleteProfile(mcon,
			 * CustomAlertDialog.this, Constants.deleteprofile); dp.execute();
			 */

			String zipLocation = Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/" + value.getText().toString() + ".zip";
			System.out.println("path:.." + zipLocation);

			String chksum = fileToMD5(zipLocation);
			touchedevent = "upload";
			HttpManager Hm = new HttpManager(mcon, CustomAlertDialog.this,
					Constants.URL + "" + Constants.getupload + "?name="
							+ value.getText().toString() + "&description=" + ""
							+ "&version=1&original_filename="
							+ value.getText().toString() + ".zip&checksum="
							+ chksum, zipLocation);

			Hm.execute();
			
			
		} else {
			//ai.selected("ok");
			Intent intent = new Intent(mcon, ListCategory.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    mcon.startActivity(intent);
		}
	}

	void DeleteRecursive(File fileOrDirectory) {

		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();

	}

	public ArrayList<File> getfile() {

		String dirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/ORsave";
		File dir = new File(dirPath);
		File listFile[] = dir.listFiles();
		System.out.println("dir:.." + dir);

		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) {
				} else {
					// System.out.println("name:..." + listFile[i].getName());
					fileList.add(listFile[i]);
				}

			}
		}
		return fileList;
	}

	@Override
	public void onTaskComplete(String result, int code) {
		System.out.println("result;.." + result);
		if (touchedevent.equals("list")) {
			if (result.equals("error")) {
				showValidate(mcon.getString(R.string.server_error), true);
			} else if (code == 200) {
				LoadList ll = new LoadList();
				arraylist = ll.load(result);
				if (arraylist.size() > 0) {
					listView.setVisibility(View.VISIBLE);
					tv.setVisibility(View.GONE);
					Collections.reverse(arraylist);
					ServerDataAdapter dataAdapter = new ServerDataAdapter(mcon,
							arraylist, mcon.getResources());

					listView.setAdapter(dataAdapter);

				} else {
					listView.setVisibility(View.GONE);
					tv.setVisibility(View.VISIBLE);
				}
			} else {
				listView.setVisibility(View.GONE);
				tv.setVisibility(View.VISIBLE);
				try {
					JSONObject obj = new JSONObject(result);
					JSONObject ar = new JSONObject(obj.getString("errors"));
					showValidate(ar.getString("upload") + "", true);
				} catch (Exception ex) {
					System.out.println("Error:.." + ex.getMessage());
					showValidate(mcon.getString(R.string.api_error), true);
				}
			}
		} else if (touchedevent.equals("download")) {
			if (result.equals("error")) {
				showValidate(mcon.getString(R.string.server_error), true);
			} else if (code == 200) {
				File appDir = new File(
						Environment.getExternalStorageDirectory()
								+ File.separator + "OrSave");
				File zipFile = new File(appDir.getPath() + "/" + file_name
						+ ".zip");
				new CustomAlertDialog(mcon).showOkDialog(zipFile.toString(),
						true, "zipp");
			} else {
				try {
					JSONObject obj = new JSONObject(result);
					showValidate(obj.getString("full_messages"), true);
				} catch (Exception ex) {
					System.out.println("Error:.." + ex.getMessage());
					showValidate(mcon.getString(R.string.api_error), true);
				}
			}
		} else if (touchedevent.equals("upload")) {
			if (result.equals("error")) {
				showValidate(mcon.getString(R.string.server_error), true);
			} else if (code == 200) {
				 ai.selected("ok");
				/*String public_link="";
				try{
					JSONObject obj=new JSONObject(result);					
					JSONObject pobj=new JSONObject(obj.getString("profile"));					
					public_link=pobj.getString("public_link");
					System.out.println("public_link:."+public_link);
				}
				catch(Exception ex){
					System.out.println("Eror:."+ex.getMessage());
				}
				Cursor c = mcon.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null); 
		        c.moveToFirst();		       				
				String email = "ngupta1807@gmail.com";
                String subject = "OneRemote";
                String message = "Here is your latest upload: \n"+c.getString(c.getColumnIndex("display_name"))+" — Created at "+ Html.fromHtml("<a>"+public_link+"</a>") ;		        
                new SendEmail().sendMail(mcon,email, subject, message,c.getString(c.getColumnIndex("display_name")),ai);
                c.close();*/
				
			} else {
				try {
					JSONObject obj = new JSONObject(result);
					showValidate(obj.getString("full_messages"), true);
				} catch (Exception ex) {
					System.out.println("Error:.." + ex.getMessage());
					showValidate(mcon.getString(R.string.api_error), true);
				}
			}

		} else if (touchedevent.equals("delete")) {
			String zipLocation = Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/" + value.getText().toString() + ".zip";
			System.out.println("path:.." + zipLocation);

			String chksum = fileToMD5(zipLocation);
			touchedevent = "upload";
			HttpManager Hm = new HttpManager(mcon, CustomAlertDialog.this,
					Constants.URL + "" + Constants.getupload + "?name="
							+ value.getText().toString() + "&description=" + ""
							+ "&version=1&original_filename="
							+ value.getText().toString() + ".zip&checksum="
							+ chksum, zipLocation);

			Hm.execute();
		} else {
			ai.selected("ok");
		}
	}

	/*
	 * public ArrayList<File> getFolder(File dir) { File listFile[] =
	 * dir.listFiles(); if (listFile != null && listFile.length > 0) { for (int
	 * i = 0; i < listFile.length; i++) {
	 * 
	 * if (listFile[i].toString().contains(".")) {
	 * 
	 * } else folderList.add(listFile[i]);
	 * 
	 * } } return folderList; }
	 */

	public static String fileToMD5(String filePath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			byte[] buffer = new byte[1024];
			MessageDigest digest = MessageDigest.getInstance("MD5");
			int numRead = 0;
			while (numRead != -1) {
				numRead = inputStream.read(buffer);
				if (numRead > 0)
					digest.update(buffer, 0, numRead);
			}
			byte[] md5Bytes = digest.digest();
			return convertHashToString(md5Bytes);
		} catch (Exception e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private static String convertHashToString(byte[] md5Bytes) {
		String returnVal = "";
		for (int i = 0; i < md5Bytes.length; i++) {
			returnVal += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return returnVal;
	}

	@Override
	public void onTaskComplete(String result, ArrayList<LanModel> data) {
		if (dialogload.equals("test")) {
			imagedialog.show();
		} else {

		}
		System.out.println("data:" + data.size());

		if (data.size() > 0) {
			tv.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);

			Collections.sort(data, new Comparator<LanModel>() {
				@Override
				public int compare(LanModel u1, LanModel u2) {
					return u1.KEY_ip.compareToIgnoreCase(u2.KEY_ip);
				}
			});

			SdcardDataAdapter dataAdapter = new SdcardDataAdapter(mcon, data,
					mcon.getResources(), "string");

			listView.setAdapter(dataAdapter);

		} else {
			listView.setVisibility(View.GONE);
			tv.setText("No Device Found.");
			tv.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void XMLselected(String action) {
		System.out.println("action:.." + action);
		if (click.equals("click")) {
			try {
				Intent myIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(action));
				mcon.startActivity(myIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(
						mcon,
						"No application can handle this request."
								+ " Please install a webbrowser",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else {
			/*
			 * name.setText(tv_ip); link.setText(tv_url);
			 */
			/*
			 * imagedialog.dismiss(); LoadLan ll = new LoadLan(mcon,
			 * CustomAlertDialog.this); ll.execute();
			 */

			spm.saveStringValues("tv_id", tv_ip);
			spm.saveStringValues("tv_url", action);

			imagedialog.dismiss();
			ai.selected("finish");
		}

	}
}
