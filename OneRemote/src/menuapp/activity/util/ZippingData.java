package menuapp.activity.util;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import menuapp.activity.R;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.intrface.SdcardLoadInterface;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;
import menuapp.activity.util.txtdata.NodeTXTGenerator;
import menuapp.activity.util.xmldata.NodeXMLGenerator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class ZippingData extends AsyncTask<String, Void, String> {

	SdcardLoadInterface callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	String path = "";

	String ziplocation = "";
	
	String savein = "";
	ArrayList<File> fileList = new ArrayList<File>();
	DbAdapter mDbHelper;

	public ZippingData(Context context, SdcardLoadInterface callback,
			String path, String ziplocation,String savein) {
		this.context = context;
		this.callback = callback;
		this.path = path;
		this.ziplocation = ziplocation;
		this.savein= savein;
		dbSetup(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		try {
			String zipFile = "" + path;

			File pt = new File(path);

			File fl = new File(Environment.getExternalStorageDirectory()
					+ "/ORsave");
			if (!fl.exists()) {
				fl.mkdir();
			}

			String unzipLocation = Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/" + ziplocation + ".zip";
			File unzipLocatio = new File(unzipLocation);
			if (unzipLocatio.exists()) {
				System.out.println("unzipLocation:..." + unzipLocation);
				String splt[] = unzipLocation.replace(".", "&").split("&");
				System.out.println("splt" + splt[0]);
				String a = "" + splt[0];
				fileList = getFilelocation(ziplocation);
				int num = (int) 1 + getMaxNumber(fileList);
				System.out.println("num:..." + num);
				unzipLocation = splt[0] + "_" + num + ".zip";
			}

			SharedPreferencesManager spm = new SharedPreferencesManager(context);

			String cat_id = spm.getStringValues(context
					.getString(R.string.Cat_id));

			if (savein.equals("0")) {
				System.out.println("location:..." + unzipLocation);
				System.out.println("zipFile:..." + zipFile);
				Compress d = new Compress();
				d.zipFileAtPath(zipFile, unzipLocation, pt.getName());
			} else {
				zipFile = Environment.getExternalStorageDirectory()
						+ "/ORsave/Menu";
				syncDb(context, cat_id, zipFile, unzipLocation, pt.getName());
			}

		} catch (Exception ex) {
			System.out.println("Error in Editing ngo." + ex.getMessage());
		}

		return webResponse;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			callback.done("folder");
			mProgressDialog.dismiss();
		} catch (Exception ex) {
			System.out.println("Error in logindonor/ngo complition:..."
					+ ex.getMessage());
		}
	}

	private void dbSetup(Context con) {
		// TODO Auto-generated method stub
		mDbHelper = new DbAdapter(con);
		mDbHelper.open();

	}

	public ArrayList<File> getFilelocation(String filename) {

		String dirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/ORsave";
		File dir = new File(dirPath);
		File listFile[] = dir.listFiles();
		// System.out.println("dir:.." + dir);

		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) {
				} else {
					if (listFile[i].getName().contains(filename)) {

						String ar[] = listFile[i].getName().replace(".", "&")
								.split("&");
						/*
						 * System.out.println("name:..." + ar[0]);
						 * System.out.println("filename:..." + filename);
						 */
						File fl = new File(ar[0]);
						fileList.add(fl);

					}
				}
			}
		}
		return fileList;
	}

	private int getMaxNumber(ArrayList<File> fileList2) {
		String filename = "";
		int number = 0;
		for (int i = 0; i < fileList2.size(); i++) {
			System.out.println("get file:.." + fileList2.get(i));
			if (!fileList2.get(i).toString().contains("_")) {
				filename = fileList2.get(i).toString() + "_0";
				String ar[] = filename.split("_");
				if (number < Integer.parseInt(ar[2])) {
					number = Integer.parseInt(ar[2]);
				}
			} else {
				filename = fileList2.get(i).toString();
				String ar[] = filename.split("_");
				try {
					if (number < Integer.parseInt(ar[2])) {
						System.out.println("in else if");
						number = Integer.parseInt(ar[2]);
					}
				} catch (Exception ex) {
					System.out.println("Errror:.." + ex.getMessage());
					number = 0;
				}
			}
		}
		return number;
	}

	private ArrayList<AppModel> catList;
	private ArrayList<ActionModel> actionlist;
	private ArrayList<SubCatItemModel> subCatItemList;
	private ArrayList<LinkModel> linkitem;
	private ArrayList<SwitchModel> switchtem;

	private void syncDb(Context con, String cat_id, String zipFile,
			String unzipLocation, String name) {
		int catid = Integer.parseInt(cat_id);

		System.out.println("catid sync db:.." + catid);

		catList = new GetDataFrmDB().getCatIDMatchesWithPath(mDbHelper, catid);

		subCatItemList = new GetDataFrmDB().getAllSubCategoryItemByCatId(
				mDbHelper, catid);

		actionlist = new GetDataFrmDB().getAllActionCatIdResults(mDbHelper,
				catid);

		linkitem = new GetDataFrmDB().getAllLinkItemByCatid(mDbHelper, catid);
		
		switchtem = new GetDataFrmDB().getAllSwitchItemByCatid(mDbHelper, catid);
		try {
			new NodeXMLGenerator().generateCatXMLFile(catList, context);

			new NodeXMLGenerator().generateSubCatItemXMLFile(subCatItemList,
					context);

			new NodeXMLGenerator().generateActionXMLFile(actionlist, context);

			new NodeXMLGenerator().generateLinkXMLFile(linkitem, context);
			
			new NodeXMLGenerator().generateSwitchXMLFile(switchtem, context);

			new NodeTXTGenerator().generateCatTXTFile(catList, mDbHelper,
					context,name);

			new NodeTXTGenerator().generateSubCatItemTXTFile(subCatItemList,
					mDbHelper, context);

			new NodeTXTGenerator().generateActionTXTFile(actionlist, mDbHelper,
					context);

			new NodeTXTGenerator().generateLinkTXTFile(linkitem, mDbHelper,
					context);
			
			new NodeTXTGenerator().generateSwitchTXTFile(switchtem, mDbHelper,
					context);


		}

		catch (Exception ex) {
			System.out.println("error in creating files:.." + ex.getMessage());
		}

		System.out.println("location:..." + unzipLocation);
		System.out.println("zipFile:..." + zipFile);

		File sourceLocation = new File(
				Environment.getExternalStorageDirectory() + "/OneRemote/Photos");
		File targetLocation = new File(
				Environment.getExternalStorageDirectory() + "/ORsave/Menu/Photos");

		try {
			copyDirectoryOneLocationToAnotherLocation(sourceLocation,
					targetLocation);
		} catch (Exception ex) {
			System.out.println("Error in copying:.." + ex.getMessage());
		}
		Compress d = new Compress();
		d.zipFileAtPath(zipFile, unzipLocation, "ORsave");
	}

	public static void copyDirectoryOneLocationToAnotherLocation(
			File sourceLocation, File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < sourceLocation.listFiles().length; i++) {

				copyDirectoryOneLocationToAnotherLocation(new File(
						sourceLocation, children[i]), new File(targetLocation,
						children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);

			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}

	}

}