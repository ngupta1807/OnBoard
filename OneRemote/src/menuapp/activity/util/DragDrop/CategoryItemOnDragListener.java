package menuapp.activity.util.DragDrop;

import java.util.ArrayList;

import java.util.List;

import menuapp.activity.ListCategory;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.adapter.AppAdapter;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.AppendAllData;
import menuapp.activity.util.txtdata.TXTGenerator;
import menuapp.activity.util.xmldata.XMLGenerator;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ListView;


public class CategoryItemOnDragListener implements OnDragListener {

	AppModel me;
	Context mcon;
	int resumeColor;
	ArrayList<AppModel> data;
	DbAdapter mdbhelper;
	AppModel remtempValues= null;
	AppModel instempValues= null;
	AppModel tempValues = null;
	public CategoryItemOnDragListener(AppModel appModel,Context mcon,ArrayList<AppModel> data,DbAdapter mdbhelper) {
		me = appModel;
		this.mcon=mcon;
		this.data=data;
		this.mdbhelper=mdbhelper;
		resumeColor  = mcon.getResources().getColor(android.R.color.background_light);

	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			v.setBackgroundColor(0x30000000);
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			v.setBackgroundColor(resumeColor);
			break;
		case DragEvent.ACTION_DROP:

			CategoryPassObject passObj = (CategoryPassObject) event.getLocalState();
			View view = passObj.view;
			AppModel passedItem = passObj.item;
			ArrayList<AppModel> srcList = passObj.srcList;
			ListView oldParent = (ListView) view.getParent();
			AppAdapter srcAdapter = (AppAdapter) (oldParent
					.getAdapter());

			ListView newParent = (ListView) v.getParent();
			AppAdapter destAdapter = (AppAdapter) (newParent
					.getAdapter());
			ArrayList<AppModel> destList = destAdapter.getList();

			int removeLocation = srcList.indexOf(passedItem);
			int insertLocation = destList.indexOf(me);
			/*
			 * If drag and drop on the same list, same position, ignore
			 */
			
			remtempValues = (AppModel) data.get(removeLocation);
			String remid = remtempValues.getId();
			instempValues = (AppModel) data.get(insertLocation);
			String insid = instempValues.getId();
			
			System.out.println("insid loc :" + insid);
			System.out.println("remid loc :" + remid);
			
			
			if (srcList != destList || removeLocation != insertLocation) {
				if (new ListCategory().removeItemToList(srcList, passedItem)) {
					destList.add(insertLocation, passedItem);
				}

				mdbhelper.updatecategoryPos(Long.valueOf(remid),
						String.valueOf(insertLocation));
				updatedata(insertLocation, removeLocation);
				
				
				srcAdapter.notifyDataSetChanged();
				destAdapter.notifyDataSetChanged();
			}

			v.setBackgroundColor(resumeColor);

			break;
		case DragEvent.ACTION_DRAG_ENDED:
			v.setBackgroundColor(resumeColor);
		default:
			break;
		}

		return true;
	}
	public void updatedata(int insertLocation,int removeLocation){
		if (insertLocation > removeLocation) {
			
			for (int i = removeLocation; i <= insertLocation - 1; i++) { // 3

				tempValues = (AppModel) data.get(i);

				Log.v("Value of id :", "" + tempValues.getId());
				Log.v("Set Pos :",""+ (tempValues.getPos() - 1));
				mdbhelper.updatecategoryPos(
						Long.valueOf(tempValues.getId()),
						""+ (tempValues.getPos() - 1));		
				
			}
		} else {
			Log.v("", "in For loop  graeter cond loc");					
			for (int i = insertLocation+1; i <= removeLocation; i++) { // 3

				tempValues = (AppModel) data.get(i);

				Log.v("Value of id :", "" + tempValues.getId());
				Log.v("Set Pos :",""+ (tempValues.getPos() +1));

				mdbhelper.updatecategoryPos(
						Long.valueOf(tempValues.getId()),
						""+ (tempValues.getPos() + 1));	
				
			}

		}
		new XMLGenerator().generateCatXMLFile(
				new GetDataFrmDB().getAllCategoryResult(mdbhelper),
				mcon);

		new TXTGenerator().generateCatTXTFile(
				new GetDataFrmDB().getAllCategoryResult(mdbhelper),
				mdbhelper,mcon);
	}

}
