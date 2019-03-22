package menuapp.activity.util.txtdata;

import java.io.File;



import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;

public class NodeTXTGenerator {
	public void generateCatTXTFile(ArrayList<AppModel> data, DbAdapter mDbHelper,Context con,String name) {
		String path = "";

		for (int i = 0; i < data.size(); i++) {
			AppModel am = data.get(i);

			path = "";
			String ids[] = am.getPath().split(",");

			for (int j = 0; j < ids.length; j++) {
				if (ids[j].equals("0")) {

				} else {
					String cat_name = new GetDataFrmDB().getCatNameByID(
							Integer.parseInt(ids[j]), mDbHelper);
					path = path + "/" + cat_name;
				}
			}
			Log.v("path", path);

			final File dir = new File(Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/Menu/"+ path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			TXTHandler handler = new TXTHandler();
			try {
				File file = handler.getFileNameForSave("Categories",con);
				ArrayList<AppModel> subcatitem = new GetDataFrmDB()
						.getAllCategoryResult(mDbHelper);

				handler.writenestCategoryFile(data, file, mDbHelper,name);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void generateSubCatItemTXTFile(ArrayList<SubCatItemModel> data,
			DbAdapter mDbHelper,Context con) {
		String path = "";

		for (int i = 0; i < data.size(); i++) {
			SubCatItemModel am = data.get(i);

			path = "";
			String ids[] = am.getKey_DEVICE_PATH().split(",");
			System.out.println("in device path get:.."
					+ am.getKey_DEVICE_PATH());
			for (int j = 0; j < ids.length; j++) {
				if (ids[j].equals("0")) {

				} else {

					String cat_name = new GetDataFrmDB().getCatNameByID(
							Integer.parseInt(ids[j]), mDbHelper);
					path = path + "/" + cat_name;

				}
			}
			Log.v("path", path);
			SharedPreferencesManager smp = new SharedPreferencesManager(con);
			final File dir = new File(Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/Menu/" + path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {

				File yourFile = new File(dir, am.getKEY_SUB_CAT_ITEM_BODY());
				if (!yourFile.exists()) {
					yourFile.createNewFile();
				}
				// File file = new File(dir, am.getKEY_SUB_CAT_ITEM_BODY());
				// FileOutputStream outputStream = new FileOutputStream(file);
				// outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		TXTHandler handler = new TXTHandler();
		try {
			File file = handler.getFileNameForSave("Devices",con);
			ArrayList<SubCatItemModel> subcatitem = new GetDataFrmDB()
					.getAllSubCategoryItemOrderByResult2(mDbHelper);

			handler.writeSubCategoryItemsFile(data, file, mDbHelper);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void generateLinkTXTFile(ArrayList<LinkModel> data,
			DbAdapter mDbHelper,Context con) {
		String path = "";

		for (int i = 0; i < data.size(); i++) {
			LinkModel am = data.get(i);

			path = "";
			String ids[] = am.getKey_LINK_DEVICE_PATH().split(",");
			System.out.println("in device path get:.."
					+ am.getKey_LINK_DEVICE_PATH());
			for (int j = 0; j < ids.length; j++) {
				if (ids[j].equals("0")) {

				} else {

					String cat_name = new GetDataFrmDB().getCatNameByID(
							Integer.parseInt(ids[j]), mDbHelper);
					path = path + "/" + cat_name;

				}
			}
			Log.v("path", path);
			final File dir = new File(Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/Menu/" + path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				File yourFile = new File(dir, am.getKEY_LINK_BODY());
				if (!yourFile.exists()) {
					yourFile.createNewFile();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		TXTHandler handler = new TXTHandler();
		try {
			File file = handler.getFileNameForSave("Link",con);
			ArrayList<LinkModel> linkitem = new GetDataFrmDB()
					.getAllLinkResult(mDbHelper);

			handler.writeLinkItemsFile(data, file, mDbHelper);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void generateSwitchTXTFile(ArrayList<SwitchModel> data,
			DbAdapter mDbHelper,Context con) {
		String path = "";

		for (int i = 0; i < data.size(); i++) {
			SwitchModel am = data.get(i);

			path = "";
			String ids[] = am.getKey_SWITCH_DEVICE_PATH().split(",");
			System.out.println("in device path get:.."
					+ am.getKey_SWITCH_DEVICE_PATH());
			for (int j = 0; j < ids.length; j++) {
				if (ids[j].equals("0")) {

				} else {

					String cat_name = new GetDataFrmDB().getCatNameByID(
							Integer.parseInt(ids[j]), mDbHelper);
					path = path + "/" + cat_name;

				}
			}
			Log.v("path", path);
			final File dir = new File(Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/Menu/" + path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				File yourFile = new File(dir, am.getKEY_SWITCH_BODY());
				if (!yourFile.exists()) {
					yourFile.createNewFile();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		TXTHandler handler = new TXTHandler();
		try {
			File file = handler.getFileNameForSave("Switch",con);
			ArrayList<SwitchModel> switchitem = new GetDataFrmDB()
					.getAllSwitchResult(mDbHelper);

			handler.writeSwitchItemsFile(data, file, mDbHelper);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generateActionTXTFile(ArrayList<ActionModel> data,
			DbAdapter mDbHelper,Context con) {
		String path = "";

		for (int i = 0; i < data.size(); i++) {
			ActionModel am = data.get(i);

			path = "";
			String ids[] = am.getKey_ACTION_PATH().split(",");

			for (int j = 0; j < ids.length; j++) {
				if (ids[j].equals("0")) {

				} else {
					System.out.println("get ids:..." + ids[j]);
					String cat_name = new GetDataFrmDB().getCatNameByID(
							Integer.parseInt(ids[j]), mDbHelper);
					path = path + "/" + cat_name;

				}
			}
			Log.v("path", path);
			final File dir = new File(Environment.getExternalStorageDirectory()
					+ "/ORsave" + "/Menu/" + path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				File yourFile = new File(dir, am.getKEY_ACTION_BODY());
				if (!yourFile.exists()) {
					yourFile.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		TXTHandler handler = new TXTHandler();
		try {
			File file = handler.getFileNameForSave("App",con);
			ArrayList<ActionModel> actionitem = new GetDataFrmDB()
					.getAllAction(mDbHelper);

			handler.writeActionFile(actionitem, file, mDbHelper);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
