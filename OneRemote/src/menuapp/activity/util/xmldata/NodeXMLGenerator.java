package menuapp.activity.util.xmldata;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;

import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;

public class NodeXMLGenerator {
	public void generateCatXMLFile(ArrayList<AppModel> data,Context con) {
		XMLHandler handler = new XMLHandler();
		String data2;
		try {
			data2 = handler.writeCategoryFile(data);

			File file = handler.getFileNameForSave("Categories",con);

			handler.writeToFile(data2, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateSubCatItemXMLFile(ArrayList<SubCatItemModel> data,Context con) {
		XMLHandler handler = new XMLHandler();
		String data2 = "";
		try {
			System.out.println("size Sub_Category_Items" + data.size());
			if (data.size() > 0) {
				System.out.println("size Sub_Category_Items" + data.size());
				data2 = handler.writeCategoryItemsFile(data);
			}
			File file = handler.getFileNameForSave("Devices",con);

			handler.writeToFile(data2, file);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void generateActionXMLFile(ArrayList<ActionModel> data,Context con) {
		XMLHandler handler = new XMLHandler();
		String data2 = "";
		try {
			System.out.println("size Action" + data.size());
			if (data.size() > 0) {
				System.out.println("size Action" + data.size());
				data2 = handler.writeActionFile(data);
			}
			File file = handler.getFileNameForSave("App",con);

			handler.writeToFile(data2, file);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void generateLinkXMLFile(ArrayList<LinkModel> data,Context con) {
		XMLHandler handler = new XMLHandler();
		String data2 = "";
		try {
			System.out.println("size link_Items" + data.size());
			if (data.size() > 0) {
				data2 = handler.writeLinkItemsFile(data);
			}
			File file = handler.getFileNameForSave("Link",con);

			handler.writeToFile(data2, file);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generateSwitchXMLFile(ArrayList<SwitchModel> data,Context con) {
		XMLHandler handler = new XMLHandler();
		String data2 = "";
		try {
			System.out.println("size Switch" + data.size());
			if (data.size() > 0) {
				data2 = handler.writeSwitchItemsFile(data);
			}
			File file = handler.getFileNameForSave("Switch",con);

			handler.writeToFile(data2, file);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
