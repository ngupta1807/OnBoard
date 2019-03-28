package menuapp.activity.util.xmldata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.os.Environment;

public class XMLHandler {
	public String writeCategoryFile(ArrayList<AppModel> data) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		// create root:
		Element root = doc.createElement("Categories");
		doc.appendChild(root);

		for (int i = 0; i < data.size(); i++) {
			AppModel am = data.get(i);

			Element tagStudy = doc.createElement("category");

			root.appendChild(tagStudy);

			tagStudy.setAttribute("cat_id", am.getId());

			Element name = doc.createElement("cat_name");

			tagStudy.appendChild(name);

			name.setTextContent(am.getName());

			Element path = doc.createElement("path");

			tagStudy.appendChild(path);

			path.setTextContent("" + am.getPath());

			Element nest_id = doc.createElement("nest_id");

			tagStudy.appendChild(nest_id);

			nest_id.setTextContent("" + am.getNest_id());

			Element max_len = doc.createElement("max_len");

			tagStudy.appendChild(max_len);

			max_len.setTextContent("" + am.getMax_len());

			Element pic = doc.createElement("image_path");

			tagStudy.appendChild(pic);

			pic.setTextContent(am.getPic());
			
			Element pos = doc.createElement("pos");

			tagStudy.appendChild(pos);

			pos.setTextContent(""+am.getPos());

		}

		// create Transformer object
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		return writer.toString();
	}

	public String writeActionFile(ArrayList<ActionModel> data) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		System.out.println("Writing action file");

		Element root = doc.createElement("App");
		doc.appendChild(root);

		for (int i = 0; i < data.size(); i++) {
			System.out.println("data size:.." + data.size());

			ActionModel am = data.get(i);

			Element tagStudy = doc.createElement("action");

			root.appendChild(tagStudy);

			System.out.println("Cat_id:.." + am.getKEY_CAT_ID());
			
			tagStudy.setAttribute("action_id", am.getKEY_ACTION_ID());

			Element cat_id = doc.createElement("cat_id");

			tagStudy.appendChild(cat_id);

			cat_id.setTextContent(am.getKEY_CAT_ID());

			Element sub_cat_id = doc.createElement("action_path");

			tagStudy.appendChild(sub_cat_id);

			sub_cat_id.setTextContent(am.getKey_ACTION_PATH());

			Element sub_cat_item_name = doc.createElement("action_name");

			tagStudy.appendChild(sub_cat_item_name);

			sub_cat_item_name.setTextContent(am.getKEY_ACTION_BODY());

			Element pic = doc.createElement("pic");

			tagStudy.appendChild(pic);

			pic.setTextContent(am.getKEY_ACTION_PIC());
			
			Element pos = doc.createElement("pos");

			tagStudy.appendChild(pos);

			pos.setTextContent(""+am.getKEY_ACTION_POS());

		}
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(doc), result);
		} catch (Exception ex) {
			System.out.println("Error;..." + ex.getMessage());
		}

		return writer.toString();
	}

	public String writeLinkItemsFile(ArrayList<LinkModel> data)
			throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		// create root:
		Element root = doc.createElement("Link");
		doc.appendChild(root);

		for (int i = 0; i < data.size(); i++) {
			LinkModel am = data.get(i);

			Element tagStudy = doc.createElement("link_item");

			root.appendChild(tagStudy);

			System.out.println("sub cat id:.." + am.getKEY_LINK_ITEM_ID());

			tagStudy.setAttribute("link_id", am.getKEY_LINK_ITEM_ID());

			Element cat_id = doc.createElement("cat_id");

			tagStudy.appendChild(cat_id);

			cat_id.setTextContent(am.getKEY_CAT_ID());

			Element sub_cat_id = doc.createElement("device_path");

			tagStudy.appendChild(sub_cat_id);

			sub_cat_id.setTextContent(am.getKey_LINK_DEVICE_PATH());

			Element sub_cat_item_name = doc.createElement("link_name");

			tagStudy.appendChild(sub_cat_item_name);

			sub_cat_item_name.setTextContent(am.getKEY_LINK_BODY());

			Element pic = doc.createElement("pic");

			tagStudy.appendChild(pic);

			pic.setTextContent(am.getKEY_LINK_PIC());

			Element dt = doc.createElement("data");

			tagStudy.appendChild(dt);

			dt.setTextContent(am.getKEY_LINK_DATA());

			Element link = doc.createElement("link");

			tagStudy.appendChild(link);

			link.setTextContent(am.getKEY_LINK_LINK());
			
			Element status = doc.createElement("status");

			tagStudy.appendChild(status);

			status.setTextContent(am.getkEY_LINK_STATUS());
			
			Element pos = doc.createElement("pos");

			tagStudy.appendChild(pos);

			pos.setTextContent(am.getKEY_LINK_POS());
			
			

		}
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(doc), result);
		} catch (Exception ex) {
			System.out.println("Error;..." + ex.getMessage());
		}

		return writer.toString();
	}
	
	
	public String writeSwitchItemsFile(ArrayList<SwitchModel> data)
			throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		// create root:
		Element root = doc.createElement("Switch");
		doc.appendChild(root);

		for (int i = 0; i < data.size(); i++) {
			SwitchModel am = data.get(i);

			Element tagStudy = doc.createElement("switch_item");

			root.appendChild(tagStudy);

			System.out.println("sub cat id:.." + am.getKEY_SWITCH_ITEM_ID());

			tagStudy.setAttribute("switch_id", am.getKEY_SWITCH_ITEM_ID());

			Element cat_id = doc.createElement("cat_id");

			tagStudy.appendChild(cat_id);

			cat_id.setTextContent(am.getKEY_CAT_ID());

			Element sub_cat_id = doc.createElement("device_path");

			tagStudy.appendChild(sub_cat_id);

			sub_cat_id.setTextContent(am.getKey_SWITCH_DEVICE_PATH());

			Element sub_cat_item_name = doc.createElement("switch_name");

			tagStudy.appendChild(sub_cat_item_name);

			sub_cat_item_name.setTextContent(am.getKEY_SWITCH_BODY());

			Element pic = doc.createElement("pic");

			tagStudy.appendChild(pic);

			pic.setTextContent(am.getKEY_SWITCH_PIC());

			Element dt = doc.createElement("data");

			tagStudy.appendChild(dt);

			dt.setTextContent(am.getKEY_SWITCH_DATA());

			Element link = doc.createElement("link");

			tagStudy.appendChild(link);

			link.setTextContent(am.getKEY_SWITCH_LINK());
			
			Element status = doc.createElement("status");

			tagStudy.appendChild(status);

			status.setTextContent(am.getkEY_SWITCH_STATUS());
			
			Element pos = doc.createElement("pos");

			tagStudy.appendChild(pos);

			pos.setTextContent(am.getKEY_SWITCH_POS());

		}
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(doc), result);
		} catch (Exception ex) {
			System.out.println("Error;..." + ex.getMessage());
		}

		return writer.toString();
	}
	

	public String writeCategoryItemsFile(ArrayList<SubCatItemModel> data)
			throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		// create root:
		Element root = doc.createElement("Devices");
		doc.appendChild(root);

		for (int i = 0; i < data.size(); i++) {
			System.out.println("data size:.." + data.size());

			SubCatItemModel am = data.get(i);

			Element tagStudy = doc.createElement("sub_category_item");

			root.appendChild(tagStudy);

			System.out.println("sub cat id:.." + am.getKEY_SUB_CAT_ITEM_ID());

			tagStudy.setAttribute("sub_cat_item_id",
					am.getKEY_SUB_CAT_ITEM_ID());

			Element cat_id = doc.createElement("cat_id");

			tagStudy.appendChild(cat_id);

			cat_id.setTextContent(am.getKEY_CAT_ID());

			Element sub_cat_id = doc.createElement("device_path");

			tagStudy.appendChild(sub_cat_id);

			sub_cat_id.setTextContent(am.getKey_DEVICE_PATH());

			Element sub_cat_item_name = doc.createElement("sub_cat_item_name");

			tagStudy.appendChild(sub_cat_item_name);

			sub_cat_item_name.setTextContent(am.getKEY_SUB_CAT_ITEM_BODY());

			Element pic = doc.createElement("pic");

			tagStudy.appendChild(pic);

			pic.setTextContent(am.getKEY_PIC());

			Element link = doc.createElement("link");

			tagStudy.appendChild(link);

			link.setTextContent(am.getKEY_LINK());
			
			Element pos = doc.createElement("pos");

			tagStudy.appendChild(pos);

			pos.setTextContent(am.getKEY_SUB_CAT_ITEM_Pos());


		}
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(doc), result);
		} catch (Exception ex) {
			System.out.println("Error;..." + ex.getMessage());
		}

		return writer.toString();
	}

	public File getFileName(String fileNmae, Context con) {
		SharedPreferencesManager spm = new SharedPreferencesManager(con);

		final File dir = new File(spm.getStringValues("basefolder") + "/Menu/");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		final File myFile = new File(dir, "." + fileNmae + ".xml");

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

		final File dir = new File(Environment.getExternalStorageDirectory()
				+ "/ORsave" + "/Menu/");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		final File myFile = new File(dir, "." + fileNmae + ".xml");

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

	public void writeToFile(String data, File a) {
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(a);

			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

			myOutWriter.append(data);

			myOutWriter.close();

			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * public String writeSubCategoryFile(ArrayList<SubCatAppModel> data) throws
	 * Exception { Document doc = DocumentBuilderFactory.newInstance()
	 * .newDocumentBuilder().newDocument();
	 * 
	 * // create root: Element root = doc.createElement("sub_categories");
	 * doc.appendChild(root);
	 * 
	 * for (int i = 0; i < data.size(); i++) { SubCatAppModel am = data.get(i);
	 * 
	 * Element tagStudy = doc.createElement("sub_category");
	 * 
	 * root.appendChild(tagStudy);
	 * 
	 * tagStudy.setAttribute("sub_cat_id", am.getSUB_CAT_Id());
	 * 
	 * Element cat_id = doc.createElement("cat_id");
	 * 
	 * tagStudy.appendChild(cat_id);
	 * 
	 * cat_id.setTextContent(am.getCAT_Id());
	 * 
	 * Element name = doc.createElement("sub_cat_name");
	 * 
	 * tagStudy.appendChild(name);
	 * 
	 * name.setTextContent(am.getName());
	 * 
	 * }
	 * 
	 * Transformer transformer = TransformerFactory.newInstance()
	 * .newTransformer(); StringWriter writer = new StringWriter(); StreamResult
	 * result = new StreamResult(writer); transformer.transform(new
	 * DOMSource(doc), result);
	 * 
	 * return writer.toString(); }
	 */

}
