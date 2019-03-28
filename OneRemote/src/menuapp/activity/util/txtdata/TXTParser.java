package menuapp.activity.util.txtdata;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

public class TXTParser {
	static final String KEY_ID = "id";
	static final String KEY_NAME = "name";
	SharedPreferencesManager spm;
	int value = 0;
	String stored_cat_id = "0";
	String stored_sub_cat_id = "0";
	SubCatItemModel am;
	LinkModel lm;
	SwitchModel sm;
	int catvalue = 0;
	String rowcheck = "";
	int cat_value = 0;

	// new updates..
	String stored_cat_name = "";

	// constructor
	public TXTParser() {

	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element
	 *            node
	 * @param key
	 *            string
	 * */
	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	/*
	 * Categories
	 */
	public ArrayList<AppModel> getAllCategoryResult(Context con,
			DbAdapter mDbHelper) {
		BufferedReader reader = null;

		ArrayList<AppModel> resultList = new ArrayList<AppModel>();

		AppModel am = new AppModel();

		System.out.println("Inside txt categories method:..");

		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		File file = new File(smp.getStringValues("basefolder")
				+ "/Menu/Categories.txt");
		try {
			FileInputStream fIn = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fIn));
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.contains("\t")) {

					stored_cat_name = line;

				} else {
					String newchar[] = line.split("\t");
					if (newchar.length == 2 && value == 0) {
						System.out.println("name:.." + stored_cat_name);
						System.out.println("char:.." + newchar[1]);
						am = new AppModel();
						am.setName(stored_cat_name);
						am.setPic(newchar[1]);
						value = value + 1;
					} else if (value == 1) {
						am.setNest_id(Integer.parseInt(newchar[1]));
						value = value + 1;
					} else if (value == 2) {
						am.setPath(newchar[1]);
						value = value + 1;
					} else if (value == 3) {
						am.setMax_len(Integer.parseInt(newchar[1]));
						value = value + 1;					
					}
					else if (value == 4) {
						am.setPos(Integer.parseInt(newchar[1]));
						value = 0;
						resultList.add(am);						
					}
				}

			}

			reader.close();

		} catch (IOException e) { // log the exception }
		}
		return resultList;
	}

	/*
	 * Devices
	 */
	public ArrayList<SubCatItemModel> getAllDevicesResults(Context con,
			DbAdapter dbhelper) {
		BufferedReader reader = null;

		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();
		spm = new SharedPreferencesManager(con);

		System.out.println("Inside Devices method:..");

		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		File file = new File(smp.getStringValues("basefolder")
				+ "/Menu/Devices.txt");

		System.out.println("read path:.." + file.getPath());

		try {
			FileInputStream fIn = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fIn));
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.contains("\t")) {
					stored_cat_id = new GetDataFrmDB().getCatIDByName(line,
							dbhelper);

				} else {
					String newchar[] = line.split("\t");
					if (newchar.length == 2 && value == 0) {
						am = new SubCatItemModel();
						am.setKEY_CAT_ID(stored_cat_id);
						am.setKEY_SUB_CAT_ITEM_BODY(newchar[1]);
						value = value + 1;
					} else if (value == 1) {
						am.setKEY_PIC(newchar[1]);
						value = value + 1;

					} else if (value == 2) {
						am.setKEY_LINK(newchar[1]);
						value = value + 1;
					}

					else if (value == 3) {
						am.setKEY_SUB_CAT_ITEM_Pos(newchar[1]);
						value = value + 1;
						String path = new GetDataFrmDB().getCatPathByCatID(
								Integer.parseInt(stored_cat_id), dbhelper);
						am.setKey_DEVICE_PATH(path);
						value = 0;
						resultList.add(am);
					}
				}

			}
			reader.close();

		} catch (IOException e) {
			// log the exception
		}
		return resultList;
	}

	/*
	 * Action
	 */
	public ArrayList<ActionModel> getAllActionResults(Context con,
			DbAdapter dbhelper) {
		BufferedReader reader = null;

		ArrayList<ActionModel> resultList = new ArrayList<ActionModel>();
		spm = new SharedPreferencesManager(con);

		System.out.println("Inside Action method:..");

		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		File file = new File(smp.getStringValues("basefolder")
				+ "/Menu/App.txt");
		ActionModel am = null;
		System.out.println("read path:.." + file.getPath());
		try {
			FileInputStream fIn = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fIn));
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.contains("\t")) {
					stored_cat_id = new GetDataFrmDB().getCatIDByName(line,
							dbhelper);

				} else {
					String newchar[] = line.split("\t");
					if (newchar.length == 2 && value == 0) {
						am = new ActionModel();
						am.setKEY_CAT_ID(stored_cat_id);
						am.setKEY_ACTION_BODY(newchar[1]);
						value = value + 1;
					} else if (value == 1) {
						am.setKEY_ACTION_PIC(newchar[1]);
						value = value + 1;
					} else if (value == 2) {
						String cat_id = "";
						String ar[] = newchar[1].split(",");
						for (int i = 0; i < ar.length; i++) {
							if (i == 0) {
								cat_id = new GetDataFrmDB().getCatIDByName(
										newchar[1], dbhelper);
							} else
								cat_id = cat_id
										+ ","
										+ new GetDataFrmDB().getCatIDByName(
												newchar[1], dbhelper);
						}
						am.setKey_ACTION_PATH(cat_id);
						value = value + 1;
						
					}
					 else if (value == 3) {
						 	am.setKEY_ACTION_POS(Integer.parseInt(newchar[1]));							
							value = 0;
							resultList.add(am);
						}
				}
			}
			reader.close();

		} catch (IOException e) {
			// log the exception
		}
		return resultList;
	}

	public ArrayList<LinkModel> getAllLinkResults(Context con,
			DbAdapter dbhelper) {
		BufferedReader reader = null;

		ArrayList<LinkModel> resultList = new ArrayList<LinkModel>();
		spm = new SharedPreferencesManager(con);

		System.out.println("Inside Link method:..");

		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		File file = new File(smp.getStringValues("basefolder")
				+ "/Menu/Link.txt");

		System.out.println("read path:.." + file.getPath());

		try {
			FileInputStream fIn = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fIn));
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.contains("\t")) {
					stored_cat_id = new GetDataFrmDB().getCatIDByName(line,
							dbhelper);

				} else {
					String newchar[] = line.split("\t");
					if (newchar.length == 2 && value == 0) {
						lm = new LinkModel();
						lm.setKEY_CAT_ID(stored_cat_id);
						lm.setKEY_LINK_BODY(newchar[1]);
						value = value + 1;
					} else if (value == 1) {
						lm.setKEY_LINK_DATA(newchar[1]);
						value = value + 1;
					} else if (value == 2) {
						lm.setKEY_LINK_PIC(newchar[1]);
						value = value + 1;

					} else if (value == 3) {
						lm.setKEY_LINK_LINK(newchar[1]);
						value = value + 1;
						/*
						 * String path = new GetDataFrmDB().getCatPathByCatID(
						 * Integer.parseInt(stored_cat_id), dbhelper);
						 * lm.setKey_LINK_DEVICE_PATH(path); value = 0;
						 * resultList.add(lm);
						 */
					} else if (value == 4) {
						lm.setkEY_LINK_STATUS(newchar[1]);
						value = value + 1;
						String path = new GetDataFrmDB().getCatPathByCatID(
								Integer.parseInt(stored_cat_id), dbhelper);
						lm.setKey_LINK_DEVICE_PATH(path);
						
					}
					else if (value == 5) {
						lm.setKEY_LINK_POS(newchar[1]);
						
						value = 0;
						resultList.add(lm);
					}

				}

			}
			reader.close();

		} catch (IOException e) {
			// log the exception
		}
		return resultList;
	}

	public ArrayList<SwitchModel> getAllSwitchResults(Context con,
			DbAdapter dbhelper) {
		BufferedReader reader = null;

		ArrayList<SwitchModel> resultList = new ArrayList<SwitchModel>();
		spm = new SharedPreferencesManager(con);

		System.out.println("Inside Link method:..");

		SharedPreferencesManager smp = new SharedPreferencesManager(con);
		File file = new File(smp.getStringValues("basefolder")
				+ "/Menu/Switch.txt");

		System.out.println("read path:.." + file.getPath());

		try {
			FileInputStream fIn = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fIn));
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.contains("\t")) {
					stored_cat_id = new GetDataFrmDB().getCatIDByName(line,
							dbhelper);

				} else {
					String newchar[] = line.split("\t");
					if (newchar.length == 2 && value == 0) {
						sm = new SwitchModel();
						sm.setKEY_CAT_ID(stored_cat_id);
						sm.setKEY_SWITCH_BODY(newchar[1]);
						value = value + 1;
					} else if (value == 1) {
						sm.setKEY_SWITCH_DATA(newchar[1]);
						value = value + 1;
					} else if (value == 2) {
						sm.setKEY_SWITCH_PIC(newchar[1]);
						value = value + 1;

					} else if (value == 3) {
						sm.setKEY_SWITCH_LINK(newchar[1]);
						value = value + 1;
						/*
						 * String path = new GetDataFrmDB().getCatPathByCatID(
						 * Integer.parseInt(stored_cat_id), dbhelper);
						 * lm.setKey_LINK_DEVICE_PATH(path); value = 0;
						 * resultList.add(lm);
						 */
					} else if (value == 4) {
						sm.setkEY_SWITCH_STATUS(newchar[1]);						
						String path = new GetDataFrmDB().getCatPathByCatID(
								Integer.parseInt(stored_cat_id), dbhelper);
						sm.setKey_SWITCH_DEVICE_PATH(path);
						value = value + 1;
						
					}else if (value == 5) {
						sm.setKEY_SWITCH_POS(newchar[1]);						
						value = 0;
						resultList.add(sm);
					}

				}

			}
			reader.close();

		} catch (IOException e) {
			// log the exception
		}
		return resultList;
	}
	 String stored_version="";
	 public String getAllVersionResult(Context con) {
	  BufferedReader reader = null;
	  System.out.println("Inside txt Version method:..");
	       
	  SharedPreferencesManager smp = new SharedPreferencesManager(con);
	  File file = new File(smp.getStringValues("basefolder")
	    + "/Menu/Version.txt");
	  try {
	   FileInputStream fIn = new FileInputStream(file);
	   reader = new BufferedReader(new InputStreamReader(fIn));
	   String line;
	   
	   while ((line = reader.readLine()) != null) {   
	     stored_version = line;
	   }
	   reader.close();

	  } catch (IOException e) { // log the exception }
	  }
	  return stored_version;
	 }

}
