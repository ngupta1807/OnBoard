package menuapp.activity.util.xmldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;

public class XMLParser {
	static final String KEY_ID = "id";
	static final String KEY_NAME = "name";

	// constructor
	public XMLParser() {

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
	public ArrayList<AppModel> getAllCategoryResult(Context con, int id) {

		ArrayList<AppModel> resultList = new ArrayList<AppModel>();

		AppModel am;

		System.out.println("Inside xml categories method:..");

		try {

			// Get the text file
			SharedPreferencesManager smp = new SharedPreferencesManager(con);

			File file = new File(smp.getStringValues("basefolder")
					+ "/Menu/.Categories.xml");

			System.out.println("read path:.." + file.getPath());
			InputStream is = new FileInputStream(file.getPath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("category");

			for (int i = 0; i < nl.getLength(); i++) {
				Element e = (Element) nl.item(i);

				Node node = nl.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					am = new AppModel();
					Element eElement = (Element) node;
					System.out.println("id:.." + id);
					System.out.println("nestid:.." + getValue(e, "nest_id"));
					if (getValue(e, "nest_id").equals("" + id)) {
						am.setName(getValue(e, "cat_name"));
						am.setNest_id(Integer.parseInt(getValue(e, "nest_id")));
						am.setDesc(getValue(e, "cat_desc"));
						am.setMax_len(Integer.parseInt(getValue(e, "max_len")));
						am.setPic(getValue(e, "image_path"));
						am.setPos(Integer.parseInt(getValue(e, "pos")));
						am.setId(eElement.getAttribute("cat_id"));
						resultList.add(am);
					}
				}

			}

		} catch (Exception ex) {
			System.out.println("Error in reading:.." + ex.getMessage());
		}

		return resultList;
	}

	public ArrayList<ActionModel> getAllActionCatIdResults(Context con,
			int rowID) {

		ActionModel am;

		ArrayList<ActionModel> resultList = new ArrayList<ActionModel>();

		System.out.println("Inside action method:..");

		try {

			SharedPreferencesManager smp = new SharedPreferencesManager(con);

			File file = new File(smp.getStringValues("basefolder")
					+ "/Menu/.App.xml");

			InputStream is = new FileInputStream(file.getPath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("action");

			for (int i = 0; i < nl.getLength(); i++) {
				Element e = (Element) nl.item(i);
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) node;
					System.out.println("xml pic path:..."
							+ eElement.getAttribute("pic"));
					
					System.out.println("get cat id:.."+getValue(e, "cat_id"));
					
					System.out.println("rowID:.."+rowID);
					if (getValue(e, "cat_id").equals("" + rowID)) {
						System.out.println("in side if:..");
						am = new ActionModel();
						am.setKEY_ACTION_BODY(getValue(e, "action_name"));
						am.setKEY_ACTION_ID(eElement.getAttribute("action_id"));
						am.setKey_ACTION_PATH(getValue(e, "action_path"));
						am.setKEY_ACTION_PIC(getValue(e, "pic"));
						am.setKEY_ACTION_POS(Integer.parseInt(getValue(e, "pos")));
						am.setKEY_CAT_ID(getValue(e, "cat_id"));

						resultList.add(am);
					}

				}

			}

		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	/*
	 * Sub category item
	 */
	public ArrayList<SubCatItemModel> getAllSubCategoryItemCatIdResults(
			Context con, int cat_id) {

		SubCatItemModel am;

		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();

		System.out.println("Inside sub-categories-item method:..");

		try {

			// Get the text file
			SharedPreferencesManager smp = new SharedPreferencesManager(con);

			File file = new File(smp.getStringValues("basefolder")
					+ "/Menu/.Devices.xml");

			InputStream is = new FileInputStream(file.getPath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("sub_category_item");

			System.out.println("cat_id" + cat_id);

			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					if (getValue(e, "cat_id").equals("" + cat_id)) {

						am = new SubCatItemModel();
						am.setKEY_SUB_CAT_ITEM_BODY(getValue(e,
								"sub_cat_item_name"));
						am.setKEY_CAT_ID(getValue(e, "cat_id"));
						am.setKey_DEVICE_PATH(getValue(e, "device_path"));
						am.setKEY_SUB_CAT_ITEM_ID(eElement
								.getAttribute("sub_cat_item_id"));
						am.setKEY_PIC(getValue(e, "pic"));
						am.setPos(Integer.parseInt(getValue(e, "pos")));
						am.setKEY_LINK(getValue(e, "link"));
						resultList.add(am);
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<LinkModel> getAllLinkItemResults(Context con, int cat_id) {

		LinkModel am;

		ArrayList<LinkModel> resultList = new ArrayList<LinkModel>();

		System.out.println("Inside Link-item method:..");

		try {

			// Get the text file
			SharedPreferencesManager smp = new SharedPreferencesManager(con);

			File file = new File(smp.getStringValues("basefolder")
					+ "/Menu/.Link.xml");

			InputStream is = new FileInputStream(file.getPath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("link_item");

			System.out.println("cat_id" + cat_id);

			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					
					System.out.println("get cat_id:.." + getValue(e, "cat_id"));
					if (getValue(e, "cat_id").equals("" + cat_id)) {

						am = new LinkModel();
						am.setKEY_LINK_BODY(getValue(e, "link_name"));
						am.setKEY_CAT_ID(getValue(e, "cat_id"));
						am.setKey_LINK_DEVICE_PATH(getValue(e, "device_path"));
						am.setKEY_LINK_ITEM_ID(eElement
								.getAttribute("link_id"));
						am.setKEY_LINK_PIC(getValue(e, "pic"));
						am.setKEY_LINK_DATA(getValue(e, "data"));
						am.setKEY_LINK_LINK(getValue(e, "link"));
						am.setKEY_LINK_POS(getValue(e, "pos"));
						am.setkEY_LINK_STATUS(getValue(e, "status"));
						resultList.add(am);
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<SwitchModel> getAllSwitchItemResults(Context con, int cat_id) {

		SwitchModel am;

		ArrayList<SwitchModel> resultList = new ArrayList<SwitchModel>();

		System.out.println("Inside Switch-item method:..");

		try {

			// Get the text file
			SharedPreferencesManager smp = new SharedPreferencesManager(con);

			File file = new File(smp.getStringValues("basefolder")
					+ "/Menu/.Switch.xml");

			InputStream is = new FileInputStream(file.getPath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("switch_item");

			System.out.println("cat_id" + cat_id);

			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					
					System.out.println("get cat_id:.." + getValue(e, "cat_id"));
					if (getValue(e, "cat_id").equals("" + cat_id)) {

						am = new SwitchModel();
						am.setKEY_SWITCH_BODY(getValue(e, "switch_name"));
						am.setKEY_CAT_ID(getValue(e, "cat_id"));
						am.setKey_SWITCH_DEVICE_PATH(getValue(e, "device_path"));
						am.setKEY_SWITCH_ITEM_ID(eElement
								.getAttribute("switch_id"));
						am.setKEY_SWITCH_PIC(getValue(e, "pic"));
						am.setKEY_SWITCH_DATA(getValue(e, "data"));
						am.setKEY_SWITCH_LINK(getValue(e, "link"));
						am.setKEY_SWITCH_POS(getValue(e, "pos"));
						am.setkEY_SWITCH_STATUS(getValue(e, "status"));
						resultList.add(am);
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}
	
	
	public ArrayList<SubCatItemModel> getAllSubCategoryItemCatIdResults(
			Context con) {

		SubCatItemModel am;

		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();

		System.out.println("Inside sub-categories-item method:..");

		try {

			// Get the text file
			SharedPreferencesManager smp = new SharedPreferencesManager(con);

			File file = new File(smp.getStringValues("basefolder")
					+ "/Menu/.Devices.xml");

			InputStream is = new FileInputStream(file.getPath());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("sub_category_item");

			for (int i = 0; i < nl.getLength(); i++) {
				System.out.println("in for:..");
				Element e = (Element) nl.item(i);
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;

					am = new SubCatItemModel();
					am.setKEY_SUB_CAT_ITEM_BODY(getValue(e, "sub_cat_item_name"));
					am.setKEY_CAT_ID(getValue(e, "cat_id"));
					am.setKey_DEVICE_PATH(getValue(e, "device_path"));
					am.setKEY_SUB_CAT_ITEM_ID(eElement
							.getAttribute("sub_cat_item_id"));
					am.setKEY_PIC(getValue(e, "pic"));
					am.setKEY_LINK(getValue(e, "link"));
					resultList.add(am);

				}

			}

		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}
}
