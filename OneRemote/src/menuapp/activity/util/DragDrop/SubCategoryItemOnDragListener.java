package menuapp.activity.util.DragDrop;

import java.util.ArrayList;

import java.util.List;

import menuapp.activity.AddCategory;
import menuapp.activity.AddDevice;
import menuapp.activity.AddLink;
import menuapp.activity.AddSwitch;
import menuapp.activity.ListCategory;
import menuapp.activity.ListSubCategory;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.adapter.AllDataAppAdapter;
import menuapp.activity.util.model.AppendAllData;
import menuapp.activity.util.txtdata.TXTGenerator;
import menuapp.activity.util.xmldata.XMLGenerator;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ListView;

public class SubCategoryItemOnDragListener implements OnDragListener {

	AppendAllData me;
	Context mcon;
	int resumeColor;
	ArrayList<AppendAllData> data;	
	DbAdapter mDbHelper;
	AppendAllData tempValues = null;
	AppendAllData remtempValues= null;
	AppendAllData instempValues= null;
	public SubCategoryItemOnDragListener(AppendAllData appModel, Context mcon,
			ArrayList<AppendAllData> data,DbAdapter mdbhelper) {
		me = appModel;
		this.mcon = mcon;
		this.data = data;		
		mDbHelper = mdbhelper;
		resumeColor = mcon.getResources().getColor(
				android.R.color.background_light);

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

			SubCategoryPassObject passObj = (SubCategoryPassObject) event
					.getLocalState();
			View view = passObj.view;
			AppendAllData passedItem = passObj.item;
			ArrayList<AppendAllData> srcList = passObj.srcList;
			ListView oldParent = (ListView) view.getParent();
			AllDataAppAdapter srcAdapter = (AllDataAppAdapter) (oldParent
					.getAdapter());

			ListView newParent = (ListView) v.getParent();
			AllDataAppAdapter destAdapter = (AllDataAppAdapter) (newParent
					.getAdapter());
			ArrayList<AppendAllData> destList = destAdapter.getList();

			int removeLocation = srcList.indexOf(passedItem);
			int insertLocation = destList.indexOf(me);
			/*
			 * If drag and drop on the same list, same position, ignore
			 */
			remtempValues = (AppendAllData) data.get(removeLocation);
			String remid = remtempValues.getId();
			instempValues = (AppendAllData) data.get(insertLocation);
			String insid = instempValues.getId();
			System.out.println("insid loc :" + insid);
			System.out.println("remid loc :" + remid);

			
			String instype = instempValues.getType();
			String remtype = remtempValues.getType();
			
			if (srcList != destList || removeLocation != insertLocation) {
				if (new ListSubCategory().removeItemToList(srcList, passedItem)) {
					destList.add(insertLocation, passedItem);
				}
				

				if (remtype.equals("link")) {
					Log.v("ID loc", "" + Long.valueOf(remid));
					Log.v("I loc", "" + insertLocation);
					mDbHelper.updateLinkItemPos(Long.valueOf(remid),
							String.valueOf(insertLocation));
					updatedata(insertLocation, removeLocation);
				}
				if (remtype.equals("switch")) {
					Log.v("ID loc", "" + Long.valueOf(remid));
					Log.v("I loc", "" + insertLocation);
					mDbHelper.updateSwitchItemPos(Long.valueOf(remid),
							String.valueOf(insertLocation));
					updatedata(insertLocation, removeLocation);
				}
				if (remtype.equals("device")) {
					Log.v("ID loc", "" + Long.valueOf(remid));
					Log.v("I loc", "" + insertLocation);
					mDbHelper.updateSubCategoryItemPos(Long.valueOf(remid),
							String.valueOf(insertLocation));
					updatedata(insertLocation, removeLocation);
				}
				if (remtype.equals("cat")) {
					mDbHelper.updatecategoryPos(Long.valueOf(remid),
							String.valueOf(insertLocation));
					updatedata(insertLocation, removeLocation);
				}
				if (remtype.equals("action")) {
					Log.v("ID loc", "" + Long.valueOf(remid));
					Log.v("I loc", "" + insertLocation);
					mDbHelper.updateactionPos(Long.valueOf(remid),
							String.valueOf(insertLocation));
					updatedata(insertLocation, removeLocation);
				}

			}
			srcAdapter.notifyDataSetChanged();
			destAdapter.notifyDataSetChanged();

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

				tempValues = (AppendAllData) data.get(i);

				Log.v("Value of id :", "" + tempValues.getId());
				Log.v("Set Pos :",
						""
								+ (Integer.parseInt(tempValues
										.getPos()) - 1));
				
				if (tempValues.getType().equals("Link")) {
					mDbHelper.updateLinkItemPos(Long.valueOf(tempValues.getId()),
							""+ (Integer.parseInt(tempValues
											.getPos()) - 1));
				} else if (tempValues.getType().equals("switch")) {
					mDbHelper.updateSwitchItemPos(
							Long.valueOf(tempValues.getId()),
							""+ (Integer.parseInt(tempValues
											.getPos()) - 1));	

				} else if (tempValues.getType().equals("device")) {

					mDbHelper.updateSubCategoryItemPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) - 1));
					

				} else if (tempValues.getType().equals("cat")) {
					mDbHelper.updatecategoryPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) - 1));

				}
				if (tempValues.getType().equals("action")) {
					mDbHelper.updateactionPos(
							Long.valueOf(tempValues.getId()),
							""+ (Integer.parseInt(tempValues
											.getPos()) - 1));								

				}
			}
		} else {
			Log.v("", "in For loop  graeter cond loc");					
			for (int i = insertLocation+1; i <= removeLocation; i++) { // 3

				tempValues = (AppendAllData) data.get(i);

				Log.v("Value of id :", "" + tempValues.getId());
				Log.v("Set Pos :",
						""+ (Integer.parseInt(tempValues
										.getPos()) +1));

				if (tempValues.getType().equals("Link")) {
					mDbHelper.updateLinkItemPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) + 1));
				} else if (tempValues.getType().equals("switch")) {
					mDbHelper.updateSwitchItemPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) + 1));
				} else if (tempValues.getType().equals("device")) {								
					mDbHelper.updateSubCategoryItemPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) + 1));
					
				} else if (tempValues.getType().equals("cat")) {
					mDbHelper.updatecategoryPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) + 1));					
				}
				if (tempValues.getType().equals("action")) {
					mDbHelper.updateactionPos(
							Long.valueOf(tempValues.getId()),
							""
									+ (Integer.parseInt(tempValues
											.getPos()) + 1));
				}
			}

		}
		new XMLGenerator().generateCatXMLFile(
				new GetDataFrmDB().getAllCategoryResult(mDbHelper),
				mcon);

		new TXTGenerator().generateCatTXTFile(
				new GetDataFrmDB().getAllCategoryResult(mDbHelper),
				mDbHelper,mcon);

		new XMLGenerator().generateSubCatItemXMLFile(
				new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
				mcon);

		new TXTGenerator().generateSubCatItemTXTFile(
				new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
				mDbHelper, mcon);

		new XMLGenerator().generateActionXMLFile(
				new GetDataFrmDB().getAllAction(mDbHelper),
				mcon);

		new TXTGenerator().generateActionTXTFile(
				new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
				mcon);
		
		new XMLGenerator().generateLinkXMLFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper),
				mcon);

		new TXTGenerator().generateLinkTXTFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper),
				mDbHelper, mcon);
		new XMLGenerator().generateSwitchXMLFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper),
				mcon);

		new TXTGenerator().generateSwitchTXTFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper),
				mDbHelper, mcon);

	}

}
