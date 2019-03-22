package menuapp.activity.util.DragDrop;

import java.util.ArrayList;

import java.util.List;

import menuapp.activity.util.model.AppModel;

import android.view.View;

public class CategoryPassObject {

	public View view;
	public AppModel item;
	public ArrayList<AppModel> srcList;

	public CategoryPassObject(View v, AppModel i, ArrayList<AppModel> s) {
		view = v;
		item = i;
		srcList = s;
	}

}
