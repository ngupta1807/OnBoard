package menuapp.activity.util.txtdata;

import java.io.BufferedWriter;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class TXTHandler {
	String checkCat = "";
	String checkCatForSubCat = "";
	String checkSubCatForSubCat = "";

	public void writeCategoryFile(int max_len, File file, DbAdapter mDbHelper)
			throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));

			catoutput.write(max_len);
			catoutput.write("\n");

			/*
			 * for (int i = 0; i < data.size(); i++) { AppModel am =
			 * data.get(i);
			 * 
			 * System.out.println("name:.." + am.getName());
			 * catoutput.write(am.getName()); catoutput.write("\n"); if
			 * (am.getPic().equals("")) { catoutput.write("\t" + "&");
			 * catoutput.write("\n"); } else { catoutput.write("\t" +
			 * am.getPic()); catoutput.write("\n"); } catoutput.write("\t" +
			 * am.getNest_id()); catoutput.write("\n");
			 * 
			 * String path[] = am.getPath().split(","); String cat_path = "";
			 * for (int j = 0; j < path.length; j++) { if (j == 0) cat_path =
			 * new GetDataFrmDB().getCatNameByID( Integer.parseInt(path[j]),
			 * mDbHelper); else cat_path = cat_path + "," + new
			 * GetDataFrmDB().getCatNameByID( Integer.parseInt(path[j]),
			 * mDbHelper); } catoutput.write("\t" + cat_path);
			 * catoutput.write("\n"); catoutput.write("\t" + am.getMax_len());
			 * catoutput.write("\n"); }
			 */
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}

	public void writeCategoryFile(ArrayList<AppModel> data, File file,
			DbAdapter mDbHelper) throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));
			Log.v("category root size", "" + data.size());
			for (int i = 0; i < data.size(); i++) {
				AppModel am = data.get(i);

				System.out.println("name:.." + am.getName());
				catoutput.write(am.getName());
				catoutput.write("\n");
				if (am.getPic().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getPic());
					catoutput.write("\n");
				}
				catoutput.write("\t" + am.getNest_id());
				catoutput.write("\n");

				String path[] = am.getPath().split(",");
				String cat_path = "";
				for (int j = 0; j < path.length; j++) {
					if (j == 0)
						cat_path = new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(path[j]), mDbHelper);
					else
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatNameByID(
										Integer.parseInt(path[j]), mDbHelper);
				}
				catoutput.write("\t" + cat_path);
				catoutput.write("\n");
				catoutput.write("\t" + am.getMax_len());
				catoutput.write("\n");
				catoutput.write("\t" + am.getPos());
				catoutput.write("\n");
			}
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}

	public void writenestCategoryFile(ArrayList<AppModel> data, File file,
			DbAdapter mDbHelper, String name) throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));
			Log.v("category root size", "" + data.size());
			for (int i = 0; i < data.size(); i++) {
				AppModel am = data.get(i);

				System.out.println("name:.." + am.getName());
				catoutput.write(am.getName());
				catoutput.write("\n");
				if (am.getPic().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getPic());
					catoutput.write("\n");
				}

				catoutput.write("\t" + am.getNest_id());
				catoutput.write("\n");

				String path[] = am.getPath().split(",");
				String cat_path = "";
				for (int j = 0; j < path.length; j++) {
					if (j == 0)
						cat_path = new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(path[j]), mDbHelper);
					else
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatNameByID(
										Integer.parseInt(path[j]), mDbHelper);
				}
				catoutput.write("\t" + cat_path);
				catoutput.write("\n");
				catoutput.write("\t" + am.getMax_len());
				catoutput.write("\n");
			}
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}

	public void writeSubCategoryItemsFile(ArrayList<SubCatItemModel> data,
			File file, DbAdapter mDbhelper) throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < data.size(); i++) {

				SubCatItemModel am = data.get(i);

				String cat_name = new GetDataFrmDB().getCatNameByID(
						Integer.parseInt(am.getKEY_CAT_ID()), mDbhelper)
						.toString();

				if (!checkCatForSubCat.equals(cat_name)) {
					checkCatForSubCat = cat_name;
					catoutput.write(cat_name);
					catoutput.write("\n");
				}

				if (am.getKEY_SUB_CAT_ITEM_BODY().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_SUB_CAT_ITEM_BODY());
					catoutput.write("\n");
				}

				if (am.getKEY_PIC().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_PIC());
					catoutput.write("\n");
				}
				if (am.getKEY_LINK().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_LINK());
					catoutput.write("\n");
				}
				if (am.getKEY_SUB_CAT_ITEM_Pos().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_SUB_CAT_ITEM_Pos());
					catoutput.write("\n");
				}
				
				/*
				 * String path[] = am.getKey_DEVICE_PATH().split(","); String
				 * cat_path = ""; for (int j = 0; j < path.length; j++) { if (j
				 * == 0) { cat_path = new GetDataFrmDB().getCatNameByID(
				 * Integer.parseInt(path[j]), mDbhelper); } else { cat_path =
				 * cat_path + "," + new GetDataFrmDB().getCatNameByID(
				 * Integer.parseInt(path[j]), mDbhelper); } }
				 * catoutput.write("\t" + cat_path); catoutput.write("\n");
				 */
			}
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}
	
	
	public void writeLinkItemsFile(ArrayList<LinkModel> data, File file,
			DbAdapter mDbhelper) throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < data.size(); i++) {

				LinkModel am = data.get(i);

				String cat_name = new GetDataFrmDB().getCatNameByID(
						Integer.parseInt(am.getKEY_CAT_ID()), mDbhelper)
						.toString();

				if (!checkCatForSubCat.equals(cat_name)) {
					checkCatForSubCat = cat_name;
					catoutput.write(cat_name);
					catoutput.write("\n");
				}
				if (am.getKEY_LINK_BODY().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_LINK_BODY());
					catoutput.write("\n");
				}
				if (am.getKEY_LINK_DATA().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_LINK_DATA());
					catoutput.write("\n");
				}
				if (am.getKEY_LINK_PIC().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_LINK_PIC());
					catoutput.write("\n");
				}
				if (am.getKEY_LINK_LINK().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_LINK_LINK());
					catoutput.write("\n");
				}
				if (am.getkEY_LINK_STATUS().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getkEY_LINK_STATUS());
					catoutput.write("\n");
				}
				if (am.getKEY_LINK_POS().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_LINK_POS());
					catoutput.write("\n");
				}

			}
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}

	public void writeSwitchItemsFile(ArrayList<SwitchModel> data, File file,
			DbAdapter mDbhelper) throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));
			
			System.out.println("Size:.."
					+ data.size());
			for (int i = 0; i < data.size(); i++) {

				SwitchModel sm = data.get(i);

				String cat_name = new GetDataFrmDB().getCatNameByID(
						Integer.parseInt(sm.getKEY_CAT_ID()), mDbhelper)
						.toString();

				System.out.println("cat_name:.."
						+ cat_name);
				if (!checkCatForSubCat.equals(cat_name)) {
					checkCatForSubCat = cat_name;
					catoutput.write(cat_name);
					catoutput.write("\n");
				}
				if (sm.getKEY_SWITCH_BODY().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + sm.getKEY_SWITCH_BODY());
					catoutput.write("\n");
				}
				System.out.println("KEY_SWITCH_DATA():.."
						+ sm.getKEY_SWITCH_DATA());
				if (sm.getKEY_SWITCH_DATA().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + sm.getKEY_SWITCH_DATA());
					catoutput.write("\n");
				}
				System.out.println("KEY_SWITCH_PIC():.."
						+ sm.getKEY_SWITCH_PIC());
				if (sm.getKEY_SWITCH_PIC().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + sm.getKEY_SWITCH_PIC());
					catoutput.write("\n");
				}
				System.out.println("KEY_SWITCH_LINK():.."
						+ sm.getKEY_SWITCH_LINK());
				if (sm.getKEY_SWITCH_LINK().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + sm.getKEY_SWITCH_LINK());
					catoutput.write("\n");
				}
				System.out.println("kEY_SWITCH_STATUS():.."
						+ sm.getkEY_SWITCH_STATUS());
				if (sm.getkEY_SWITCH_STATUS().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + sm.getkEY_SWITCH_STATUS());
					catoutput.write("\n");
				}
				if (sm.getKEY_SWITCH_POS().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + sm.getKEY_SWITCH_POS());
					catoutput.write("\n");
				}
				

			}
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}

	
	public void writeActionFile(ArrayList<ActionModel> data, File file,
			DbAdapter mDbhelper) throws Exception {
		BufferedWriter catoutput = null;
		try {
			catoutput = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < data.size(); i++) {

				ActionModel am = data.get(i);

				String cat_name = new GetDataFrmDB().getCatNameByID(
						Integer.parseInt(am.getKEY_CAT_ID()), mDbhelper)
						.toString();

				if (!checkCatForSubCat.equals(cat_name)) {
					checkCatForSubCat = cat_name;
					catoutput.write(cat_name);
					catoutput.write("\n");
				}

				if (am.getKEY_ACTION_BODY().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_ACTION_BODY());
					catoutput.write("\n");
				}

				if (am.getKEY_ACTION_PIC().equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_ACTION_PIC());
					catoutput.write("\n");
				}
				
				String path[] = am.getKey_ACTION_PATH().split(",");
				String cat_path = "";
				for (int j = 0; j < path.length; j++) {
					if (j == 0)
						cat_path = new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(path[j]), mDbhelper);
					else
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatNameByID(
										Integer.parseInt(path[j]), mDbhelper);
				}
				catoutput.write("\t" + cat_path);
				catoutput.write("\n");
				if (String.valueOf(am.getKEY_ACTION_POS()).equals("")) {
					catoutput.write("\t" + "&");
					catoutput.write("\n");
				} else {
					catoutput.write("\t" + am.getKEY_ACTION_POS());
					catoutput.write("\n");
				}
			}
			catoutput.close();
		} catch (Exception ex) {
			System.out.println("Error:.." + ex.getMessage());
		}
	}

	public File getFileName(String fileNmae, Context con) {
		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		final File dir = new File(smp.getStringValues("basefolder") + "/Menu/");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		final File myFile = new File(dir, fileNmae + ".txt");

		if (myFile.exists())
			myFile.delete();

		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return myFile;
	}

	public File getFileNameForSave(String fileNmae, Context con) {
		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		final File dir = new File(Environment.getExternalStorageDirectory()
				+ "/ORsave" + "/Menu/");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		final File myFile = new File(dir, fileNmae + ".txt");

		if (myFile.exists())
			myFile.delete();

		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return myFile;
	}

	public void deleteTxtFiles(Context con) {
		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		final File dir = new File(smp.getStringValues("basefolder") + "/Menu/");

		System.out.println("deleting txt files...");
		File cat_file = new File(dir, "Categories" + ".txt");
		File sub_cat_file = new File(dir, "Sub_Categories" + ".txt");
		File sub_cat_item_file = new File(dir, "Sub_Categories_Items" + ".txt");

		if (cat_file.exists())
			cat_file.delete();
		if (sub_cat_file.exists())
			sub_cat_file.delete();
		if (sub_cat_item_file.exists())
			sub_cat_item_file.delete();

	}
	 public void writeVersionFile(File file, DbAdapter mDbHelper)
			   throws Exception {
			  BufferedWriter catoutput = null;
			  
			  try {
			   catoutput = new BufferedWriter(new FileWriter(file));
			   Log.v("Value set :", ""+mDbHelper.fetch_current_ver().getInt(1));
			   catoutput.write(""+mDbHelper.fetch_current_ver().getInt(1));
			   catoutput.close();
			  } catch (Exception e) {
			  // e.printStackTrace();
			   System.out.println("No version data");
			  }
			  
			 }
	/*
	 * public void writeToFile(String data, File a) { FileOutputStream fOut; try
	 * { fOut = new FileOutputStream(a);
	 * 
	 * OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
	 * 
	 * myOutWriter.append(data);
	 * 
	 * myOutWriter.close();
	 * 
	 * fOut.close(); } catch (FileNotFoundException e) { e.printStackTrace(); }
	 * catch (IOException e) { e.printStackTrace(); } }
	 */
}
