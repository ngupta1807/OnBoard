package menuapp.activity;

import java.io.File;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.intrface.AlertInterface;
import menuapp.activity.intrface.Constants;
import menuapp.activity.service.DeleteLogout;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.InternetReachability;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.DragDrop.LinearLayoutListView;
import menuapp.activity.util.DragDrop.CategoryPassObject;
import menuapp.activity.util.DragDrop.SubCategoryPassObject;
import menuapp.activity.util.adapter.AllDataAppAdapter;
import menuapp.activity.util.adapter.AppAdapter;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.AppendAllData;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.xmldata.XMLGenerator;
import menuapp.activity.util.xmldata.XMLParser;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListCategory extends Activity implements OnClickListener,
		AlertInterface {
	private void clearcache() {

		try {
			File dir = getCacheDir();
			if (dir != null && dir.isDirectory()) {
				Boolean val = deleteDir(dir);
			}
		} catch (Exception e) {
			System.out.println("Error:.." + e.getMessage());
		}
		finish();

	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
			return dir.delete();
		} else if (dir != null && dir.isFile())
			return dir.delete();
		else {
			return false;
		}
	}

	ListView cat_list;
	// Button addcat;
	DbAdapter mDbHelper;
	public ArrayList<AppModel> catList;
	SharedPreferencesManager spm;
	Context mcon;
	// ImageView back;
	TextView txt, title;
	Button restore;
	Button add_cat, ssdp;
	//public static List<String> onfoffar = new ArrayList<String>();
	public static List<String> pos = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.categoryview);

		mcon = ListCategory.this;

		spm = new SharedPreferencesManager(mcon);

		String id = Secure.getString(mcon.getContentResolver(),
                Secure.ANDROID_ID); 
		
		
		System.out.println("id:.."+id);
		dbSetup();

		CreateDump();

		System.out.println("List Categry:..");

		spm = new SharedPreferencesManager(mcon);

		

		cat_list = (ListView) findViewById(R.id.cat_list);
		// addcat = (Button) findViewById(R.id.addcat);
		txt = (TextView) findViewById(R.id.txt);
		add_cat = (Button) findViewById(R.id.add_cat);

		title = (TextView) findViewById(R.id.title);

		ssdp = (Button) findViewById(R.id.ssdp);
		
		XMLParser parser = new XMLParser();
		catList = parser.getAllCategoryResult(ListCategory.this, 0);
		spm.saveStringValues("level", "level1");

		spm.saveStringValues("menu", "click");
		viewsetUp();

		add_cat.setOnClickListener(this);
		//cat_list.setOnItemLongClickListener(myOnItemLongClickListener);
		ssdp.setOnClickListener(this);

	}

	private void CreateDump() {

		ArrayList<AppModel> cat_data = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);

		new XMLGenerator().generateCatXMLFile(cat_data, ListCategory.this);

		ArrayList<SubCatItemModel> sub_cat_item_data = new GetDataFrmDB()
				.getAllSubCategoryItemResult2(mDbHelper);

		new XMLGenerator().generateSubCatItemXMLFile(sub_cat_item_data,
				ListCategory.this);

		new XMLGenerator().generateActionXMLFile(
				new GetDataFrmDB().getAllAction(mDbHelper), ListCategory.this);
		new XMLGenerator().generateLinkXMLFile(
				new GetDataFrmDB().getAllLinkResult(mDbHelper),
				ListCategory.this);
		new XMLGenerator().generateSwitchXMLFile(
				new GetDataFrmDB().getAllSwitchResult(mDbHelper),
				ListCategory.this);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.add_cat) {
			spm.saveStringValues("tv_id", "");
			spm.saveStringValues("tv_url", "");
			Intent intent = new Intent(ListCategory.this, Setting.class);
			startActivity(intent);
			finish();
		}
		if (v.getId() == R.id.ssdp) {

			InternetReachability ir=new InternetReachability(mcon);
			CustomAlertDialog am=new CustomAlertDialog(this);
			if (ir.isConnected()) {
				CustomAlertDialog cd = new CustomAlertDialog(ListCategory.this,
						this);
				cd.CustomzippedTestDialog();
			} else {
				am.showValidate(getString(R.string.internet_error), true);
			}
			
		}
		

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		if (spm.getStringValues("session").equals("add")
				&& spm.getStringValues("sub_session").equals("add")) {
			finish();
			clearcache();
		} else {
			Intent intent = new Intent(ListCategory.this, Setting.class);
			intent.putExtra("itemid", "0");
			startActivity(intent);
			finish();
		}

	}
	String longcat_id="";
	String longcat_name="";
	private void listViewClick() {
		cat_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String cat_id = ((TextView) view.findViewById(R.id.tv_id))
						.getText().toString();
				String cat_name = ((TextView) view.findViewById(R.id.tv_name))
						.getText().toString();

				System.out.println("cat_id:...." + cat_id);

				spm.saveStringValues(getString(R.string.Cat_id), "" + cat_id);
				spm.saveStringValues(getString(R.string.Cat_name), ""
						+ cat_name);
				spm.saveStringValues(getString(R.string.Parent_id), "" + cat_id);

				spm.saveStringValues("level3", "main");
				spm.saveStringValues("sub_session", "add");
				spm.saveStringValues("session", "add");
				spm.saveStringValues("tv_id", "");
				spm.saveStringValues("tv_url", "");
				startActivity(new Intent(getApplicationContext(),
						ListSubCategory.class));
				finish();
			}
		});

		cat_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, final View view,
					final int position, long id) {
				System.out.println("Long click:...");

				
				AppModel selectedItem = (AppModel) (parent
						.getItemAtPosition(position));
				System.out.println("selectedItem:.."+selectedItem);
				   
				AppAdapter associatedAdapter = (AppAdapter) (parent.getAdapter());
				ArrayList<AppModel> associatedList = associatedAdapter.getList();

				CategoryPassObject passObj = new CategoryPassObject(view, selectedItem,
						associatedList);

				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				view.startDrag(data, shadowBuilder, passObj, 0);
				
				return true;
			}
		});

	}
	
	/*cat_list.setOnItemLongClickListener(new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			System.out.println("Long click:..."+longcat_id);
			
			
			return true;
		}
	});
	OnItemLongClickListener myOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			AppModel selectedItem = (AppModel) (parent
					.getItemAtPosition(position));
			System.out.println("selectedItem:.."+selectedItem);
			   
			AppAdapter associatedAdapter = (AppAdapter) (parent.getAdapter());
			ArrayList<AppModel> associatedList = associatedAdapter.getList();

			PassObject passObj = new PassObject(view, selectedItem,
					associatedList);

			ClipData data = ClipData.newPlainText("", "");
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
			view.startDrag(data, shadowBuilder, passObj, 0);

			return true;
		}

	};*/
	


	private void dbSetup() {
		// TODO Auto-generated method stub
		spm.saveStringValues(getString(R.string.P_Cat_id), "");
		spm.saveStringValues(getString(R.string.array_set), "");
		spm.saveStringValues(getString(R.string.Cat_id), "0");
		spm.saveStringValues(getString(R.string.Cat_name), "");

		mDbHelper = new DbAdapter(this);

		mDbHelper.open();

	}
	LinearLayoutListView area1;
	public AppAdapter adapter ;
	private void viewsetUp() {
		System.out.println("Size" + catList.size());
		if (catList.size() > 0) {
			txt.setVisibility(View.GONE);
			cat_list.setVisibility(View.VISIBLE);
			
			area1 = (LinearLayoutListView) findViewById(R.id.pane1);

			area1.setOnDragListener(myOnDragListener); 
			
			area1.setListView(cat_list);
			
			Collections.sort(catList, new Comparator<AppModel>() {
			   	@Override
				public int compare(AppModel lhs, AppModel rhs) {
					return String.valueOf(lhs.getPos()).compareTo(String.valueOf(rhs.getPos()));
				}
			});
			
		    adapter = new AppAdapter(this, catList,
					getApplicationContext().getResources(),mDbHelper);
	       
			cat_list.setAdapter(adapter);
			
			
			listViewClick();
		} else {
			txt.setVisibility(View.VISIBLE);
			cat_list.setVisibility(View.GONE);
		}
		System.out.println("session:." + spm.getStringValues("session"));
		System.out
				.println("sub_session:." + spm.getStringValues("sub_session"));
		if (spm.getStringValues("session").equals("add")
				&& spm.getStringValues("sub_session").equals("add")) {
			// title.setText("OneRemote");
			add_cat.setVisibility(View.VISIBLE);
		} else {
			title.setText("Select Category");
			add_cat.setVisibility(View.GONE);
		}

	}
	

	/*@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	
	Log.v("requestCode:..",""+requestCode);
	XMLParser parser = new XMLParser();
	catList = parser.getAllCategoryResult(ListCategory.this, 0);
	adapter = new AppAdapter(this, catList,
			getApplicationContext().getResources(),mDbHelper);
	cat_list.setAdapter(adapter);
	cat_list.setSelection(requestCode);

	System.out.println("requestCode:.."+requestCode);
	XMLParser parser = new XMLParser();
	catList = parser.getAllCategoryResult(ListCategory.this, 0);
	spm.saveStringValues("level", "level1");
	spm.saveStringValues("menu", "click");
	System.out.println("Size" + catList.size());
	if (catList.size() > 0) {
		txt.setVisibility(View.GONE);
		cat_list.setVisibility(View.VISIBLE);
		
		area1 = (LinearLayoutListView) findViewById(R.id.pane1);

		area1.setOnDragListener(myOnDragListener); 
		
		area1.setListView(cat_list);
		
		Collections.sort(catList, new Comparator<AppModel>() {
		   	@Override
			public int compare(AppModel lhs, AppModel rhs) {
				return String.valueOf(lhs.getPos()).compareTo(String.valueOf(rhs.getPos()));
			}
		});
		
	    adapter = new AppAdapter(this, catList,
				getApplicationContext().getResources(),mDbHelper);
	    
	    //adapter.notifyDataSetChanged();
	    
		cat_list.setAdapter(adapter);
		cat_list.setSelection(requestCode);
		
		
		listViewClick();
	} else {
		txt.setVisibility(View.VISIBLE);
		cat_list.setVisibility(View.GONE);
	}
	System.out.println("session:." + spm.getStringValues("session"));
	System.out
			.println("sub_session:." + spm.getStringValues("sub_session"));
	if (spm.getStringValues("session").equals("add")
			&& spm.getStringValues("sub_session").equals("add")) {
		// title.setText("OneRemote");
		add_cat.setVisibility(View.VISIBLE);
	} else {
		title.setText("Select Category");
		add_cat.setVisibility(View.GONE);
	}
}*/

	@Override
	public void selected(String action) {
		// TODO Auto-generated method stub
		if(action.equals("finish")){
			Intent intent = new Intent(ListCategory.this, SelectCategory.class);
			intent.putExtra("selectedvalue", "Link");
			startActivity(intent);
			finish();
		}
	}
	public boolean removeItemToList(ArrayList<AppModel> l, AppModel it) {
		boolean result = l.remove(it);
		return result;
	}

	private boolean addItemToList(ArrayList<AppModel> l, AppModel it) {
		boolean result = l.add(it);
		return result;
	}
	OnDragListener myOnDragListener = new OnDragListener() {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			String area;
			if (v == area1) {
				area = "area1";
			} else {
				area = "unknown";
			}

			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				break;
			case DragEvent.ACTION_DROP:

				CategoryPassObject passObj = (CategoryPassObject) event.getLocalState();
				View view = passObj.view;
				AppModel passedItem = passObj.item;
				ArrayList<AppModel> srcList = passObj.srcList;
				ListView oldParent = (ListView) view.getParent();
				AppAdapter srcAdapter = (AppAdapter) (oldParent.getAdapter());

				LinearLayoutListView newParent = (LinearLayoutListView) v;
				AppAdapter destAdapter = (AppAdapter) (newParent.listView
						.getAdapter());
				ArrayList<AppModel> destList = destAdapter.getList();

				if (removeItemToList(srcList, passedItem)) {
					addItemToList(destList, passedItem);
				}

				srcAdapter.notifyDataSetChanged();
				destAdapter.notifyDataSetChanged();

				// smooth scroll to bottom
				newParent.listView.smoothScrollToPosition(destAdapter
						.getCount() - 1);

				break;
			case DragEvent.ACTION_DRAG_ENDED:

			default:
				break;
			}

			return true;
		}

	};
	
	

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * 
	 * getMenuInflater().inflate(R.menu.main, menu); return
	 * super.onCreateOptionsMenu(menu); };
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.item1: Intent intent = new
	 * Intent(ListCategory.this, AddCategory.class); intent.putExtra("itemid",
	 * "0"); startActivity(intent); finish(); break;
	 * 
	 * default: break; } return super.onOptionsItemSelected(item); }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.item1:
	 * Toast.makeText(getApplicationContext
	 * (),"Item 1 Selected",Toast.LENGTH_LONG).show(); return true; case
	 * R.id.item2:
	 * Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast
	 * .LENGTH_LONG).show(); return true; case R.id.item3:
	 * Toast.makeText(getApplicationContext
	 * (),"Item 3 Selected",Toast.LENGTH_LONG).show(); return true; default:
	 * return super.onOptionsItemSelected(item); }
	 */

}
