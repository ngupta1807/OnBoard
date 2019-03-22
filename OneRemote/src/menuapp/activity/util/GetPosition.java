package menuapp.activity.util;

import java.util.ArrayList;

import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.PackageItem;
import android.content.Context;


public class GetPosition  {
Context mcon;
String action="";
int index=-1;
	public int getCategoryPos(String category, DbAdapter mDbHelper) {
		int index = -1;
		AppModel am;
		ArrayList<AppModel> CatData = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);
		for (int i = 0; i < CatData.size(); i++) {
			am = (AppModel) CatData.get(i);
			System.out.println("name:.." + am.getName());
			System.out.println("category:.." + category);
			if (am.getName().equals(category)) {
				index = i;
			}
		}
		return index;
	}
	
	public int getActionPos(String action, Context mcon) {
		PackageItem am;
		
		this.mcon=mcon;
		this.action=action;
		
	
	/*	GetListOfInstalledApplication	gla=new GetListOfInstalledApplication(mcon,GetPosition.this);
		gla.execute();*/
	
		
		ArrayList<PackageItem> pk = new GetListOfInstalledApplication(mcon).getdata();
		for (int i = 0; i < pk.size(); i++) {
			am = (PackageItem) pk.get(i);
			System.out.println("name:.." + am.getName());
			System.out.println("action:.." + action);
			if (am.getName().equals(action)) {
				index = i;
			}
		}
		
		System.out.println("index in get position:..."+index);
		
		return index;
	}

	/*@Override
	public int onTaskComplete(ArrayList<PackageItem> result) {
		
		ArrayList<PackageItem> pk = new GetListOfInstalledApplication()
		.getInstalledApplication(mcon);
		for (int i = 0; i < result.size(); i++) {
			am = (PackageItem) result.get(i);		
			if (am.getName().equals(action)) {
				index = i;
				break;
			}
		}
		System.out.println("index out get position:..."+index);
		return index;
	}*/
}
