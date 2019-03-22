package menuapp.activity.util.DragDrop;

import java.util.ArrayList;

import java.util.List;

import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.AppendAllData;

import android.view.View;

public class SubCategoryPassObject {

	public View view;
	public AppendAllData item;
	public ArrayList<AppendAllData> srcList;

	public SubCategoryPassObject(View v, AppendAllData i, ArrayList<AppendAllData> s) {
		view = v;
		item = i;
		srcList = s;
	}

}
