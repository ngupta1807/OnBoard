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

import menuapp.activity.util.txtdata.NodeTXTParser;
import menuapp.activity.util.txtdata.TXTGenerator;
import menuapp.activity.util.txtdata.TXTParser;
import menuapp.activity.util.xmldata.XMLGenerator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ExtractingData extends AsyncTask<String, Void, String> {

	SdcardLoadInterface callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	String path = "";
	String unzipppath = "";
	DbAdapter mDbHelper;
	private ArrayList<AppModel> catList;
	private ArrayList<AppModel> catListOne;
	private ArrayList<ActionModel> actionlist;
	private ArrayList<ActionModel> actionlistOne;
	private ArrayList<SubCatItemModel> subCatItemList;
	private ArrayList<SubCatItemModel> subCatItemListOne;
	private ArrayList<LinkModel> linkList;
	private ArrayList<LinkModel> linkListOne;
	private ArrayList<SwitchModel> switchList;
	private ArrayList<SwitchModel> switchListOne;
	
	AppModel tempValues = null;
	ActionModel actionvalues = null;
	LinkModel linkvalues = null;
	SwitchModel switchvalues = null;
	SubCatItemModel subCatItemtempValues = null;
	SharedPreferencesManager spm;

	public ExtractingData(Context context, SdcardLoadInterface callback,
			String path, String unzipppath) {

		this.context = context;
		this.callback = callback;
		this.path = path;
		this.unzipppath = unzipppath;
		spm = new SharedPreferencesManager(context);
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
			File f = new File(zipFile);
			String unzipLocation = "";
			if (f.getName().contains("ORsave")) {
				unzipLocation = Environment.getExternalStorageDirectory() + "/"
						+ "OneRemote";
			} else {
				unzipLocation = Environment.getExternalStorageDirectory() + "/"
						+ "OneRemote/" + "node";
			}

			System.out.println("location:..." + zipFile);
			System.out.println("unzipLocation:..." + unzipLocation);

			Decompress d = new Decompress();
			d.unZipIt(zipFile, unzipLocation);

			if (f.getName().contains("ORsave")) {
				spm.saveStringValues("basefolder", unzipLocation);
				syncDb(context, unzipLocation);

			} else {
				File sourceLocation = new File(
						Environment.getExternalStorageDirectory()
								+ "/OneRemote/node/Menu/Photos");
				File targetLocation = new File(
						Environment.getExternalStorageDirectory()
								+ "/OneRemote/Photos");
				try {
					copyDirectoryOneLocationToAnotherLocation(sourceLocation,
							targetLocation);
				} catch (Exception ex) {
					System.out.println("Error in copying:.." + ex.getMessage());
				}
				SharedPreferencesManager spm = new SharedPreferencesManager(
						context);
				System.out.println("cat_id:.."
						+ spm.getStringValues(context
								.getString(R.string.Cat_id)));
				final int catid = Integer.parseInt(spm.getStringValues(context
						.getString(R.string.Cat_id)));
				syncNodeDb(context, unzipLocation, catid);
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
			callback.done("zip");
			mProgressDialog.dismiss();
		} catch (Exception ex) {
			mProgressDialog.dismiss();
			System.out.println("Error in logindonor/ngo complition:..."
					+ ex.getMessage());
		}
	}

	private void dbSetup(Context con) {
		// TODO Auto-generated method stub
		mDbHelper = new DbAdapter(con);
		mDbHelper.open();
		mDbHelper.dropDB();
		mDbHelper.createDB();

	}

	private void syncDb(Context con, String loc) {
		TXTParser txtparser = new TXTParser();
		catList = txtparser.getAllCategoryResult(con, mDbHelper);

		if (catList.size() > 0) {
			for (int i = 0; i < catList.size(); i++) {

				tempValues = (AppModel) catList.get(i);
				String pic = "";

				if (!tempValues.getPic().equals("&")) {
					pic = tempValues.getPic();
				}

				mDbHelper.createCategory(tempValues.getName(), "", pic,
						tempValues.getNest_id(), "0", tempValues.getMax_len());
				System.out.println("length" + tempValues.getPath());
				System.out.println("value of i" + i);
				String cat_path = "";
				String ar[] = tempValues.getPath().split(",");
				for (int j = 0; j < ar.length; j++) {
					if (j == 0) {
						cat_path = new GetDataFrmDB().getCatIDByName(ar[j],
								mDbHelper);
					} else {
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatIDByName(ar[j],
										mDbHelper);
					}
				}

				mDbHelper.updateCategoryPath((i + 1), cat_path);
			}
		}

		TXTParser devicetxtparser = new TXTParser();
		subCatItemList = devicetxtparser.getAllDevicesResults(con, mDbHelper);

		if (subCatItemList.size() > 0) {
			for (int i = 0; i < subCatItemList.size(); i++) {

				subCatItemtempValues = (SubCatItemModel) subCatItemList.get(i);
				String pic = "", link = "";
				if (!subCatItemtempValues.getKEY_PIC().equals("&")) {
					pic = subCatItemtempValues.getKEY_PIC();
				}
				if (!subCatItemtempValues.getKEY_LINK().equals("&")) {
					link = subCatItemtempValues.getKEY_LINK();
				}
				mDbHelper.createSubCategoryItem(
						Integer.parseInt(subCatItemtempValues.getKEY_CAT_ID()),
						subCatItemtempValues.getKey_DEVICE_PATH(),
						subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY(), pic,
						link);

			}
		}
		TXTParser actiontxtparser = new TXTParser();
		actionlist = actiontxtparser.getAllActionResults(con, mDbHelper);

		if (actionlist.size() > 0) {
			for (int i = 0; i < actionlist.size(); i++) {

				actionvalues = (ActionModel) actionlist.get(i);
				String pic = "";
				if (!actionvalues.getKEY_ACTION_PIC().equals("&")) {
					pic = actionvalues.getKEY_ACTION_PIC();
				}
				mDbHelper.createaction(
						Integer.parseInt(actionvalues.getKEY_CAT_ID()),
						actionvalues.getKey_ACTION_PATH(),
						actionvalues.getKEY_ACTION_BODY(), pic);

			}
		}
		
		TXTParser linktxtparser = new TXTParser();
		linkList = linktxtparser.getAllLinkResults(con, mDbHelper);

		if (linkList.size() > 0) {
			for (int i = 0; i < linkList.size(); i++) {

				linkvalues = (LinkModel) linkList.get(i);
				String pic = "",status="";
				if (!linkvalues.getKEY_LINK_PIC().equals("&")) {
					pic = linkvalues.getKEY_LINK_PIC();
				}
				if (!linkvalues.getkEY_LINK_STATUS().equals("&")) {
					status = linkvalues.getkEY_LINK_STATUS();
				}
				mDbHelper.createLinkItem(Integer.parseInt(linkvalues.getKEY_CAT_ID()),
						linkvalues.getKey_LINK_DEVICE_PATH(), linkvalues.getKEY_LINK_BODY(), pic, linkvalues.getKEY_LINK_LINK(), linkvalues.getKEY_LINK_DATA(),status);				

			}
		}
		
		TXTParser switchtxtparser = new TXTParser();
		switchList = switchtxtparser.getAllSwitchResults(con, mDbHelper);

		if (switchList.size() > 0) {
			for (int i = 0; i < switchList.size(); i++) {

				switchvalues = (SwitchModel) switchList.get(i);
				String pic = "",status="";
				if (!switchvalues.getKEY_SWITCH_PIC().equals("&")) {
					pic = switchvalues.getKEY_SWITCH_PIC();
				}
				if (!switchvalues.getkEY_SWITCH_STATUS().equals("&")) {
					status = switchvalues.getkEY_SWITCH_STATUS();
				}
				mDbHelper.createSwitchItem(Integer.parseInt(switchvalues.getKEY_CAT_ID()),
						switchvalues.getKey_SWITCH_DEVICE_PATH(), switchvalues.getKEY_SWITCH_BODY(), pic, switchvalues.getKEY_SWITCH_LINK(), switchvalues.getKEY_SWITCH_DATA(),status);				

			}
		}
		
		
		
		System.out.println("loc path:..." + loc);
		File newfe = new File(loc);
		if (newfe.exists()) {
			newfe.delete();
		}

		new XMLGenerator().generateCatXMLFile(
				new GetDataFrmDB().getAllCategoryResult(mDbHelper), context);

		new TXTGenerator().generateCatTXTFile(
				new GetDataFrmDB().getAllCategoryResult(mDbHelper), mDbHelper,
				context);

		new XMLGenerator().generateSubCatItemXMLFile(
				new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
				context);

		new TXTGenerator().generateSubCatItemTXTFile(
				new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
				mDbHelper, context);

		new XMLGenerator().generateActionXMLFile(
				new GetDataFrmDB().getAllAction(mDbHelper), context);

		new TXTGenerator().generateActionTXTFile(
				new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper, context);
		
		new XMLGenerator().generateLinkXMLFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper), context);

		new TXTGenerator().generateLinkTXTFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper), mDbHelper, context);
		
		
		new XMLGenerator().generateSwitchXMLFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper), context);

		new TXTGenerator().generateSwitchTXTFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper), mDbHelper, context);		
	}

	private void syncNodeDb(Context con, String loc, int nest_under) {
		Log.i("nest_under:..", "nest_under:.." + nest_under);
		TXTParser txtparser = new TXTParser();
		catList = txtparser.getAllCategoryResult(con, mDbHelper);
		int i = 0;
		int j = 0;
		if (catList.size() > 0) {
			for (i = 0; i < catList.size(); i++) {

				tempValues = (AppModel) catList.get(i);
				String pic = "";

				if (!tempValues.getPic().equals("&")) {
					pic = tempValues.getPic();
				}

				mDbHelper.createCategory(tempValues.getName(), "", pic,
						tempValues.getNest_id(), "0", tempValues.getMax_len());
				System.out.println("length" + tempValues.getPath());
				String cat_path = "";
				String ar[] = tempValues.getPath().split(",");
				for (j = 0; j < ar.length; j++) {
					if (j == 0) {
						cat_path = new GetDataFrmDB().getCatIDByName(ar[j],
								mDbHelper);
					} else {
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatIDByName(ar[j],
										mDbHelper);
					}
				}

				mDbHelper.updateCategoryPath((i + 1), cat_path);

			}
		}

		TXTParser devicetxtparser = new TXTParser();
		subCatItemList = devicetxtparser.getAllDevicesResults(con, mDbHelper);

		if (subCatItemList.size() > 0) {
			for (int q = 0; q < subCatItemList.size(); q++) {

				subCatItemtempValues = (SubCatItemModel) subCatItemList.get(q);
				String pic = "", link = "";
				if (!subCatItemtempValues.getKEY_PIC().equals("&")) {
					pic = subCatItemtempValues.getKEY_PIC();
				}
				if (!subCatItemtempValues.getKEY_LINK().equals("&")) {
					link = subCatItemtempValues.getKEY_LINK();
				}
				mDbHelper.createSubCategoryItem(
						Integer.parseInt(subCatItemtempValues.getKEY_CAT_ID()),
						subCatItemtempValues.getKey_DEVICE_PATH(),
						subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY(), pic,
						link);

			}
		}
		TXTParser actiontxtparser = new TXTParser();
		actionlist = actiontxtparser.getAllActionResults(con, mDbHelper);

		if (actionlist.size() > 0) {
			for (int q = 0; q < actionlist.size(); q++) {

				actionvalues = (ActionModel) actionlist.get(q);
				String pic = "";
				if (!actionvalues.getKEY_ACTION_PIC().equals("&")) {
					pic = actionvalues.getKEY_ACTION_PIC();
				}
				mDbHelper.createaction(
						Integer.parseInt(actionvalues.getKEY_CAT_ID()),
						actionvalues.getKey_ACTION_PATH(),
						actionvalues.getKEY_ACTION_BODY(), pic);

			}
		}

		TXTParser linktxtparser = new TXTParser();
		linkList = linktxtparser.getAllLinkResults(con, mDbHelper);

		if (linkList.size() > 0) {
			for (int q = 0; q < linkList.size(); q++) {

				linkvalues = (LinkModel) linkList.get(q);
				String pic = "", link = "", data = "",status="";
				if (!linkvalues.getKEY_LINK_PIC().equals("&")) {
					pic = linkvalues.getKEY_LINK_PIC();
				}
				if (!linkvalues.getKEY_LINK_LINK().equals("&")) {
					link = linkvalues.getKEY_LINK_LINK();
				}
				if (!linkvalues.getKEY_LINK_DATA().equals("&")) {
					data = linkvalues.getKEY_LINK_DATA();
				}
				if (!linkvalues.getkEY_LINK_STATUS().equals("&")) {
					status = linkvalues.getkEY_LINK_STATUS();
				}
				mDbHelper.createLinkItem(
						Integer.parseInt(linkvalues.getKEY_CAT_ID()),
						linkvalues.getKey_LINK_DEVICE_PATH(),
						linkvalues.getKEY_LINK_BODY(), pic, link, data,status);

			}
		}

		NodeTXTParser txtparsernode = new NodeTXTParser();
		catListOne = txtparsernode.getAllCategoryResult(con, mDbHelper);

		if (catListOne.size() > 0) {
			for (int m = 0; m < catListOne.size(); m++) {

				tempValues = (AppModel) catListOne.get(m);
				String pic = "";

				String idget = new GetDataFrmDB().getCatIDByName(
						tempValues.getName(), mDbHelper);
				if (!idget.equals("0")) {
					deletedata(Integer.parseInt(idget));
				}

				if (!tempValues.getPic().equals("&")) {
					pic = tempValues.getPic();
				}
				Log.i("Nest id:..", "Nest id:.." + tempValues.getNest_id());

				mDbHelper.createCategory(tempValues.getName(), "", pic,
						tempValues.getNest_id(), "0", tempValues.getMax_len());

				System.out.println("length" + tempValues.getPath());
				System.out.println("value of m" + m);
				String cat_path = "";
				String ar[] = tempValues.getPath().split(",");

				for (int n = 0; n < ar.length; n++) {
					if (n == 0) {
						cat_path = new GetDataFrmDB().getCatIDByName(ar[n],
								mDbHelper);
					} else {
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatIDByName(ar[n],
										mDbHelper);
					}
				}
				Log.i("path of category id", (i + 1) + ":" + cat_path);

				mDbHelper.updateCategoryPath((i + 1), cat_path);

				if (cat_path.contains(",")) {
					String cat_p[] = cat_path.split(",");

					System.out.println("nest unders:..."
							+ cat_p[cat_p.length - 2]);

					mDbHelper.updateCategoryNest((i + 1),
							cat_p[cat_p.length - 2]);
				} else {
					System.out.println("zero nest unders:...");
					mDbHelper.updateCategoryNest((i + 1), "" + 0);
				}

				i++;
			}
		}

		NodeTXTParser nodedevicetxtparser = new NodeTXTParser();
		subCatItemListOne = nodedevicetxtparser.getAllDevicesResults(con,
				mDbHelper);

		if (subCatItemListOne.size() > 0) {
			for (int q = 0; q < subCatItemListOne.size(); q++) {

				subCatItemtempValues = (SubCatItemModel) subCatItemListOne
						.get(q);
				String pic = "", link = "";
				if (!subCatItemtempValues.getKEY_PIC().equals("&")) {
					pic = subCatItemtempValues.getKEY_PIC();
				}
				if (!subCatItemtempValues.getKEY_LINK().equals("&")) {
					link = subCatItemtempValues.getKEY_LINK();
				}
				mDbHelper.createSubCategoryItem(
						Integer.parseInt(subCatItemtempValues.getKEY_CAT_ID()),
						subCatItemtempValues.getKey_DEVICE_PATH(),
						subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY(), pic,
						link);

			}
		}

		NodeTXTParser nodeactiontxtparser = new NodeTXTParser();
		actionlistOne = nodeactiontxtparser.getAllActionResults(con, mDbHelper);

		if (actionlistOne.size() > 0) {
			for (int q = 0; q < actionlistOne.size(); q++) {

				actionvalues = (ActionModel) actionlistOne.get(q);
				String pic = "";
				if (!actionvalues.getKEY_ACTION_PIC().equals("&")) {
					pic = actionvalues.getKEY_ACTION_PIC();
				}
				mDbHelper.createaction(
						Integer.parseInt(actionvalues.getKEY_CAT_ID()),
						actionvalues.getKey_ACTION_PATH(),
						actionvalues.getKEY_ACTION_BODY(), pic);

			}
		}

		NodeTXTParser nodelinktxtparser = new NodeTXTParser();
		linkListOne = nodelinktxtparser.getAllLinkResults(con, mDbHelper);

		if (linkListOne.size() > 0) {
			for (int q = 0; q < linkListOne.size(); q++) {

				linkvalues = (LinkModel) linkListOne.get(q);
				String pic = "", link = "", data = "",status="";
				if (!linkvalues.getKEY_LINK_PIC().equals("&")) {
					pic = linkvalues.getKEY_LINK_PIC();
				}
				if (!linkvalues.getKEY_LINK_LINK().equals("&")) {
					link = linkvalues.getKEY_LINK_LINK();
				}
				if (!linkvalues.getKEY_LINK_DATA().equals("&")) {
					data = linkvalues.getKEY_LINK_DATA();
				}
				if (!linkvalues.getkEY_LINK_STATUS().equals("&")) {
					status = linkvalues.getkEY_LINK_STATUS();
				}
				mDbHelper.createLinkItem(
						Integer.parseInt(linkvalues.getKEY_CAT_ID()),
						linkvalues.getKey_LINK_DEVICE_PATH(),
						linkvalues.getKEY_LINK_BODY(), pic, link, data,status);

			}
		}
		
		NodeTXTParser nodeswitchtxtparser = new NodeTXTParser();
		switchListOne = nodeswitchtxtparser.getAllSwitchResults(con, mDbHelper);

		if (switchListOne.size() > 0) {
			for (int q = 0; q < switchListOne.size(); q++) {

				switchvalues = (SwitchModel) switchListOne.get(q);
				String pic = "", link = "", data = "",status="";
				if (!switchvalues.getKEY_SWITCH_PIC().equals("&")) {
					pic = switchvalues.getKEY_SWITCH_PIC();
				}
				if (!switchvalues.getKEY_SWITCH_LINK().equals("&")) {
					link = switchvalues.getKEY_SWITCH_LINK();
				}
				if (!switchvalues.getKEY_SWITCH_DATA().equals("&")) {
					data = switchvalues.getKEY_SWITCH_DATA();
				}
				if (!switchvalues.getkEY_SWITCH_STATUS().equals("&")) {
					status = switchvalues.getkEY_SWITCH_STATUS();
				}
				mDbHelper.createSwitchItem(
						Integer.parseInt(linkvalues.getKEY_CAT_ID()),
						linkvalues.getKey_LINK_DEVICE_PATH(),
						linkvalues.getKEY_LINK_BODY(), pic, link, data,status);

			}
		}
		

		System.out.println("loc path:..." + loc);
		File newfe = new File(loc);
		if (newfe.exists()) {
			newfe.delete();
		}

		new XMLGenerator().generateCatXMLFile(
				new GetDataFrmDB().getAllCategoryResult(mDbHelper), context);

		new TXTGenerator().generateCatTXTFile(
				new GetDataFrmDB().getAllCategoryResult(mDbHelper), mDbHelper,
				context);

		new XMLGenerator().generateSubCatItemXMLFile(
				new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
				context);

		new TXTGenerator().generateSubCatItemTXTFile(
				new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
				mDbHelper, context);

		new XMLGenerator().generateActionXMLFile(
				new GetDataFrmDB().getAllAction(mDbHelper), context);

		new TXTGenerator().generateActionTXTFile(
				new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper, context);

		new XMLGenerator().generateLinkXMLFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper), context);

		new TXTGenerator().generateLinkTXTFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper), mDbHelper,
				context);
		new XMLGenerator().generateSwitchXMLFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper), context);
		
		new TXTGenerator().generateSwitchTXTFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper), mDbHelper,
				context);

	}

	private void deletedata(int parseInt) {
		int id = parseInt;

		mDbHelper.deleteCategory(parseInt);

		mDbHelper.deleteSubCategoryItemByCat_id(parseInt);

		mDbHelper.deleteactionByCat_id(parseInt);

		mDbHelper.deleteLinkByCat_id(parseInt);

		String cat_id = new GetDataFrmDB().getCatIDMatchesWithPath(id,
				mDbHelper);

		System.out.println("id deleteing;.." + cat_id);

		if (!cat_id.equals("")) {
			System.out.println("deleting caterories.." + cat_id);
			String cat_ids[] = cat_id.split(",");

			for (int i = 0; i < cat_ids.length; i++) {
				mDbHelper.deleteCategory(Integer.parseInt(cat_ids[i]));
			}
			mDbHelper.deleteSubCategoryItemByCat_id(id);

			mDbHelper.deleteactionByCat_id(id);

			new XMLGenerator()
					.generateCatXMLFile(
							new GetDataFrmDB().getAllCategoryResult(mDbHelper),
							context);

			new TXTGenerator().generateCatTXTFile(
					new GetDataFrmDB().getAllCategoryResult(mDbHelper),
					mDbHelper, context);

			new XMLGenerator().generateSubCatItemXMLFile(
					new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
					context);

			new TXTGenerator().generateSubCatItemTXTFile(
					new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
					mDbHelper, context);

			new XMLGenerator().generateActionXMLFile(
					new GetDataFrmDB().getAllAction(mDbHelper), context);

			new TXTGenerator().generateActionTXTFile(
					new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
					context);
		}
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