package menuapp.activity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.GetListOfInstalledApplication;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.DragDrop.LinearLayoutListView;
import menuapp.activity.util.DragDrop.SubCategoryPassObject;
import menuapp.activity.util.adapter.AllDataAppAdapter;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.AppendAllData;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;
import menuapp.activity.util.xmldata.XMLParser;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListSubCategory extends Activity implements OnClickListener {
	ListView cat_list;
	DbAdapter mDbHelper;
	private ArrayList<AppModel> CatData;
	private ArrayList<SubCatItemModel> devicedata;
	private ArrayList<ActionModel> actiondata;
	private ArrayList<LinkModel> linkdata;
	private ArrayList<SwitchModel> switchdata;

	private ArrayList<AppendAllData> allData = new ArrayList<AppendAllData>();
	
	
	Context con;
	SharedPreferencesManager spm;
	TextView title;
	ImageView back;
	// TextView txt, action, cat, dev;
	TextView txt;
	Button add_sub_cat;
	AppendAllData aad;
	//public static List<String> pos = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subcategoryview);
		con = ListSubCategory.this;
		spm = new SharedPreferencesManager(con);

		cat_list = (ListView) findViewById(R.id.cat_list);
		title = (TextView) findViewById(R.id.title);
		back = (ImageView) findViewById(R.id.back);
		txt = (TextView) findViewById(R.id.txt);
		
		add_sub_cat = (Button) findViewById(R.id.add_sub_cat);

		dbSetup();
		
		System.out.println("cat_id:.."
				+ spm.getStringValues(getString(R.string.Cat_id)));
		adddata();

		viewsetUp();

		add_sub_cat.setOnClickListener(this);

		back.setOnClickListener(this);

	}
	public void adddata(){
		spm.saveStringValues("menu", "click");
		spm.saveStringValues("level", "level2");
		allData.clear();
		if (Integer.parseInt(spm.getStringValues(getString(R.string.Cat_id))) > 0) {
			XMLParser parser = new XMLParser();

			CatData = parser.getAllCategoryResult(ListSubCategory.this, Integer
					.parseInt(spm.getStringValues(getString(R.string.Cat_id))));
			devicedata = parser.getAllSubCategoryItemCatIdResults(
					ListSubCategory.this, Integer.parseInt(spm
							.getStringValues(getString(R.string.Cat_id))));

			actiondata = parser.getAllActionCatIdResults(ListSubCategory.this,
					Integer.parseInt(spm
							.getStringValues(getString(R.string.Cat_id))));

			linkdata = parser.getAllLinkItemResults(ListSubCategory.this,
					Integer.parseInt(spm
							.getStringValues(getString(R.string.Cat_id))));
			
			switchdata = parser.getAllSwitchItemResults(ListSubCategory.this,
					Integer.parseInt(spm
							.getStringValues(getString(R.string.Cat_id))));

		}

		try{
		for (int i = 0; i < CatData.size(); i++) {
			AppModel am = (AppModel) CatData.get(i);
			System.out.println("getName:.." + am.getName());
			System.out.println("getId:.." + am.getId());
			System.out.println("getPic:.." + am.getPic());
			aad = new AppendAllData();

			aad.setName("" + am.getName());
			aad.setId("" + am.getId());
			aad.setPic("" + am.getPic());
			aad.setStatus("");
			aad.setPos(""+am.getPos());
			aad.setType("cat");
			allData.add(aad);
		}
		for (int i = 0; i < devicedata.size(); i++) {
			SubCatItemModel scim = (SubCatItemModel) devicedata.get(i);
			aad = new AppendAllData();
			aad.setName(scim.getKEY_SUB_CAT_ITEM_BODY());
			aad.setId(scim.getKEY_SUB_CAT_ITEM_ID());
			aad.setPic(scim.getKEY_PIC());
			aad.setStatus("");
			aad.setType("device");
			aad.setPos(""+scim.getPos());
			allData.add(aad);
		}

		for (int i = 0; i < actiondata.size(); i++) {
			ActionModel am = (ActionModel) actiondata.get(i);
			aad = new AppendAllData();
			aad.setName(am.getKEY_ACTION_BODY());
			aad.setId(am.getKEY_ACTION_ID());
			aad.setPic(am.getKEY_ACTION_PIC());
			aad.setStatus("");
			aad.setType("action");
			aad.setPos(""+am.getKEY_ACTION_POS());
			allData.add(aad);
		}
		for (int i = 0; i < linkdata.size(); i++) {
			LinkModel lm = (LinkModel) linkdata.get(i);
			aad = new AppendAllData();
			aad.setName(lm.getKEY_LINK_BODY());
			aad.setId(lm.getKEY_LINK_ITEM_ID());
			aad.setPic(lm.getKEY_LINK_PIC());
			aad.setStatus(lm.getkEY_LINK_STATUS());
			aad.setType("link");
			aad.setPos(""+lm.getKEY_LINK_POS());
			allData.add(aad);
		}
		for (int i = 0; i < switchdata.size(); i++) {
			SwitchModel lm = (SwitchModel) switchdata.get(i);
			aad = new AppendAllData();
			aad.setName(lm.getKEY_SWITCH_BODY());
			aad.setId(lm.getKEY_SWITCH_ITEM_ID());
			aad.setPic(lm.getKEY_SWITCH_PIC());
			aad.setStatus(lm.getkEY_SWITCH_STATUS());
			aad.setType("switch");
			aad.setPos(""+lm.getKEY_SWITCH_POS());
			allData.add(aad);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.add_sub_cat) {
			Intent intent = new Intent(ListSubCategory.this, Setting.class);
			startActivity(intent);
			finish();
		}

		if (v.getId() == R.id.back) {

			if (spm.getStringValues(getString(R.string.array_set)).equals("")) {
				System.out.println("one session..");
				startActivity(new Intent(getApplicationContext(),
						ListCategory.class));
				finish();
			} else {
				System.out.println("two session..");

				String array_set = spm
						.getStringValues(getString(R.string.array_set));

				System.out.println("array_set..." + array_set);

				if (array_set.contains(",")) {
					int pos = array_set.lastIndexOf(",");

					String x = array_set.substring(0, pos);

					System.out.println("X:.." + x);

					String output = array_set.substring(
							array_set.lastIndexOf(",") + 1).trim();

					System.out.println("output:.." + output);

					System.out.println("final_array:.." + x);

					spm.saveStringValues(getString(R.string.array_set), x);
					spm.saveStringValues(getString(R.string.Cat_id), output);

				} else {
					spm.saveStringValues(getString(R.string.array_set), "");
					spm.saveStringValues(getString(R.string.Cat_id), array_set);
				}

				startActivity(new Intent(getApplicationContext(),
						ListSubCategory.class));
				finish();
			}
		}
	}

	String url = "";

	private void listViewClick() {
		

		cat_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					 int position, long id) {
				String tv_type = ((TextView) view.findViewById(R.id.tv_type))
						.getText().toString();

				if (tv_type.equals("cat")) {
					if (spm.getStringValues("sub_session").contains("cat_edit")) {

					} else {
						String catsdas_id = ((TextView) view
								.findViewById(R.id.tv_id)).getText().toString();
						String sub_cat_name = ((TextView) view
								.findViewById(R.id.tv_name)).getText()
								.toString();

						String p_id = "";
						if (spm.getStringValues(getString(R.string.array_set))
								.equals("")) {
							p_id = spm
									.getStringValues(getString(R.string.Cat_id));
						} else {
							p_id = spm
									.getStringValues(getString(R.string.array_set))
									+ ","
									+ spm.getStringValues(getString(R.string.Cat_id));
						}
						spm.saveStringValues(getString(R.string.array_set), ""
								+ p_id);
						spm.saveStringValues(
								getString(R.string.P_Cat_id),
								""
										+ spm.getStringValues(getString(R.string.Cat_id)));
						spm.saveStringValues(getString(R.string.Cat_id), ""
								+ catsdas_id);
						spm.saveStringValues(getString(R.string.Cat_name), ""
								+ sub_cat_name);
						startActivity(new Intent(getApplicationContext(),
								ListSubCategory.class));
						finish();
					}

				}
				if (tv_type.equals("device")) {
					spm.saveStringValues(
							getString(R.string.Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));
					spm.saveStringValues(
							getString(R.string.Cat_name),
							""
									+ new GetDataFrmDB().getCatNameByID(
											Integer.parseInt(spm
													.getStringValues(getString(R.string.Cat_id))),
											mDbHelper));
					spm.saveStringValues(
							getString(R.string.P_Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));

					String ac_id = ((TextView) view.findViewById(R.id.tv_id))
							.getText().toString();

					String url = new GetDataFrmDB().getUrlfromDeviceID(
							mDbHelper, Integer.parseInt(ac_id));

					// String url = "google.com";

					if (url.equals("")) {
						Toast.makeText(
								con,
								"Please update device to add link , by long pressing on the device.",
								2000).show();
					} else {
						url = url.replace(" ", "");
						if (url.startsWith("http://")) {
							url = url;
						} else
							url = "http://" + url;
						try {
							Intent myIntent = new Intent(Intent.ACTION_VIEW,
									Uri.parse(url));
							startActivity(myIntent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(
									con,
									"No application can handle this request."
											+ " Please install a webbrowser",
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}

				}

				if (tv_type.equals("link")) {
					spm.saveStringValues(
							getString(R.string.Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));
					spm.saveStringValues(
							getString(R.string.Cat_name),
							""
									+ new GetDataFrmDB().getCatNameByID(
											Integer.parseInt(spm
													.getStringValues(getString(R.string.Cat_id))),
											mDbHelper));
					spm.saveStringValues(
							getString(R.string.P_Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));

					String ac_id = ((TextView) view.findViewById(R.id.tv_id))
							.getText().toString();

					String tv_status = ((TextView) view
							.findViewById(R.id.tv_status)).getText().toString();
					ImageView onoff = (ImageView) view.findViewById(R.id.onoff);

					System.out.println("tv_status:.."+tv_status);
					
					url = new GetDataFrmDB().getLinkUrlfromDeviceID(mDbHelper,
							Integer.parseInt(ac_id));

					System.out
							.println("onoff:.." + new ListCategory().pos);
					System.out.println("ac_id:.." + ac_id);
					if (url.contains("?:")) {
						
						/*if (new ListCategory().onfoffar.contains(ac_id)) {
							onoff.setBackgroundResource(android.R.drawable.presence_invisible);
							new ListCategory().onfoffar.remove(ac_id);							
							//pos.remove(""+position);
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[1];
							System.out.println("onoff remove:.."
									+ new ListCategory().pos);
					
						} else {
							new ListCategory().onfoffar.add(ac_id);							
							//pos.add(""+position);
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[0];
							System.out.println("onoff add:.."
									+ new ListCategory().pos);
						}*/
						if(new ListCategory().pos.contains(String.valueOf(position))){
							System.out.println("link removing in pos array");
							new ListCategory().pos.remove(""+position);
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[1];
							System.out.println("onoff remove:.."
									+ new ListCategory().pos);
						}
						else{
							System.out.println("link adding in pos array");							
							new ListCategory().pos.add(String.valueOf(position));
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[0];
							System.out.println("onoff add:.."
									+ new ListCategory().pos);
						}
						
						
					} else {
						url = url;
					}
					String data = new GetDataFrmDB()
							.getLinkUrlWithdatafromDeviceID(mDbHelper,
									Integer.parseInt(ac_id));

					String URL = url + "" + data;

					if (URL.equals("")) {
						Toast.makeText(
								con,
								"Please update device to add link , by long pressing on the device.",
								2000).show();
					} else {
						URL = URL.replace(" ", "");
						if (URL.startsWith("http://")) {
							URL = URL;
						} else
							URL = "http://" + URL;
						if (tv_status.equals("false")) {
							try {
								Intent myIntent = new Intent(
										Intent.ACTION_VIEW, Uri.parse(URL));
								startActivity(myIntent);
							} catch (Exception e) {
								Toast.makeText(ListSubCategory.this,
										"Not able to open.", 2000).show();
								e.printStackTrace();
							} finally {
								// urlConnection.disconnect();
							}

						} else {
							
							StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
									.permitAll().build();
							StrictMode.setThreadPolicy(policy);

							
							
							URL url;
						    HttpURLConnection urlConnection = null;
						    try {
						    	//new URL("http://blooddonor.me/").openStream();
						        url = new URL(URL);

						        urlConnection = (HttpURLConnection) url
						                .openConnection();

						        InputStream in = urlConnection.getInputStream();

						        InputStreamReader isw = new InputStreamReader(in);

						        int dt = isw.read();
						        while (dt != -1) {
						            char current = (char) dt;
						            dt = isw.read();
						            System.out.print(current);
						        }
						    } catch (Exception e) {
						    	Toast.makeText(ListSubCategory.this,
										"Not able to open.", 2000).show();
								e.printStackTrace();
						    } finally {
						        if (urlConnection != null) {
						            urlConnection.disconnect();
						        }    
						    }
						}
						
						/*AllDataAppAdapter adapter = new AllDataAppAdapter(ListSubCategory.this, allData,
								getApplicationContext().getResources(),mDbHelper,new ListCategory().onfoffar,cat_list);		*/				
						/*System.out.println("Array data in it.."+new ListCategory().pos);
						System.out.println("Array data in it.."+position);
						String onof="false";
						for(int araysplit=0;araysplit<new ListCategory().pos.size();araysplit++){
							System.out.println("check 1 : "+new ListCategory().pos.get(araysplit));
							System.out.println("check 2 : "+position);
							if(Integer.parseInt(new ListCategory().pos.get(araysplit))==position){
								System.out.println("equal cond ");
								onof="true";
							}							
						}
						System.out.println("Array data in value.."+onof);
						if (onof.equals("true")) {							
							onoff.setImageResource(android.R.drawable.presence_online);
							Log.v("Status Adpter :", "On");
						} else {
							onoff.setImageResource(android.R.drawable.presence_invisible);
							Log.v("Status Adpter :", "Off");
						}*/
						System.out.println("Link :."+new ListCategory().pos);
						adapter.notifyDataSetChanged();
						/*cat_list.setAdapter(adapter);*/

					}
				}
				if (tv_type.equals("switch")) {
					spm.saveStringValues(
							getString(R.string.Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));
					spm.saveStringValues(
							getString(R.string.Cat_name),
							""
									+ new GetDataFrmDB().getCatNameByID(
											Integer.parseInt(spm
													.getStringValues(getString(R.string.Cat_id))),
											mDbHelper));
					spm.saveStringValues(
							getString(R.string.P_Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));

					String ac_id = ((TextView) view.findViewById(R.id.tv_id))
							.getText().toString();

					String tv_status = ((TextView) view
							.findViewById(R.id.tv_status)).getText().toString();
					ImageView onoff = (ImageView) view.findViewById(R.id.onoff);

					System.out.println("tv_status:.."+tv_status);
					
					url = new GetDataFrmDB().getSwitchUrlfromDeviceID(mDbHelper,
							Integer.parseInt(ac_id));

					System.out
							.println("onoff:.." + new ListCategory().pos);
					System.out
					.println("onoff:.." + url);
					System.out.println("ac_id:.." + ac_id);
					if (url.contains("?:")) {
						
						/*if (new ListCategory().onfoffar.contains(ac_id)) {
							onoff.setBackgroundResource(android.R.drawable.presence_invisible);
							new ListCategory().onfoffar.remove(ac_id);
							
							//pos.remove(""+position);
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[1];
							System.out.println("onoff remove:.."
									+ new ListCategory().pos);
						} else {
							new ListCategory().onfoffar.add(ac_id);
							
							//pos.add(""+position);
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[0];
							System.out.println("onoff add:.."
									+ new ListCategory().pos);
						}*/
						
						if(new ListCategory().pos.contains(String.valueOf(position))){
							System.out.println("Switch removing in pos array");
							new ListCategory().pos.remove(""+position);
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[1];
							System.out.println("onoff remove:.."
									+ new ListCategory().pos);
						}
						else{
							System.out.println("Switch adding in pos array");							
							new ListCategory().pos.add(String.valueOf(position));
							String lk2[] = url.split(Pattern.quote("?:"));
							url = lk2[0];
							System.out.println("onoff add:.."
									+ new ListCategory().pos);
						}
						
					} else {
						url = url;
					}
					String data = new GetDataFrmDB()
							.getSwitchUrlWithdatafromDeviceID(mDbHelper,
									Integer.parseInt(ac_id));

					String URL = url + "" + data;

					System.out.println("Opening url:.."+URL);
					if (URL.equals("")) {
						Toast.makeText(
								con,
								"Please update device to add link , by long pressing on the device.",
								2000).show();
					} else {
						URL = URL.replace(" ", "");
						if (URL.startsWith("http://")) {
							URL = URL;
						} else
							URL = "http://" + URL;
						if (tv_status.equals("false")) {
							try {
								Intent myIntent = new Intent(
										Intent.ACTION_VIEW, Uri.parse(URL));
								startActivity(myIntent);
							} catch (Exception e) {
								Toast.makeText(ListSubCategory.this,
										"Not able to open.", 2000).show();
								e.printStackTrace();
							} finally {
								// urlConnection.disconnect();
							}

						} else {
							
							StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
									.permitAll().build();
							StrictMode.setThreadPolicy(policy);

							
							
							URL url;
						    HttpURLConnection urlConnection = null;
						    try {
						    	//new URL("http://blooddonor.me/").openStream();
						        url = new URL(URL);

						        urlConnection = (HttpURLConnection) url
						                .openConnection();

						        InputStream in = urlConnection.getInputStream();

						        InputStreamReader isw = new InputStreamReader(in);

						        int dt = isw.read();
						        while (dt != -1) {
						            char current = (char) dt;
						            dt = isw.read();
						            System.out.print(current);
						        }
						    } catch (Exception e) {
						    	Toast.makeText(ListSubCategory.this,
										"Not able to open.", 2000).show();
								e.printStackTrace();
						    } finally {
						        if (urlConnection != null) {
						            urlConnection.disconnect();
						        }    
						    }
						}
						
						/*AllDataAppAdapter adapter = new AllDataAppAdapter(ListSubCategory.this, allData,
								getApplicationContext().getResources(),mDbHelper,new ListCategory().onfoffar,cat_list);		*/				
						/*System.out.println("Array data in it.."+new ListCategory().pos);
						System.out.println("Array data in it.."+position);
						String onof="false";
						for(int araysplit=0;araysplit<new ListCategory().pos.size();araysplit++){
							if(new ListCategory().pos.get(araysplit).equals(position)){
								onof="true";
							}							
						}
						System.out.println("Array data in value.."+onof);
						if (onof.equals("true")) {
							onoff.setBackgroundResource(android.R.drawable.presence_online);
							Log.v("Status Adpter :", "On");
						} else {
							onoff.setBackgroundResource(android.R.drawable.presence_invisible);
							Log.v("Status Adpter :", "Off");
						}*/
						System.out.println("Switch :."+new ListCategory().pos);
						adapter.notifyDataSetChanged();
						/*cat_list.setAdapter(adapter);*/

					}
				}
				if (tv_type.equals("action")) {
					spm.saveStringValues(
							getString(R.string.Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));
					spm.saveStringValues(
							getString(R.string.Cat_name),
							""
									+ new GetDataFrmDB().getCatNameByID(
											Integer.parseInt(spm
													.getStringValues(getString(R.string.Cat_id))),
											mDbHelper));
					spm.saveStringValues(
							getString(R.string.P_Cat_id),
							""
									+ spm.getStringValues(getString(R.string.Cat_id)));

					String tv_name = ((TextView) view
							.findViewById(R.id.tv_name)).getText().toString();

					String pacakege_name = new GetListOfInstalledApplication(
							ListSubCategory.this).getPackagename(tv_name);
					new GetListOfInstalledApplication(ListSubCategory.this)
							.openApp(pacakege_name);

				}
			}

		});

	

	}
	String drag_id="";
	String drag_type="";
	public void longClickListener() {
		System.out.println("Long Click...");
		spm.saveStringValues("menu", "click");

		cat_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				drag_id = ((TextView) view.findViewById(R.id.tv_id))
						.getText().toString();
				drag_type = ((TextView) view.findViewById(R.id.tv_type))
						.getText().toString();


				AppendAllData selectedItem = (AppendAllData) (parent
						.getItemAtPosition(position));
				System.out.println("selectedItem:.."+selectedItem);
				   
				AllDataAppAdapter associatedAdapter = (AllDataAppAdapter) (parent.getAdapter());
				ArrayList<AppendAllData> associatedList = associatedAdapter.getList();

				SubCategoryPassObject passObj = new SubCategoryPassObject(view, selectedItem,
						associatedList);

				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				view.startDrag(data, shadowBuilder, passObj, 0);
				return true;

			}
		});

	}
	
	
	private void dbSetup() {
		mDbHelper = new DbAdapter(this);

		mDbHelper.open();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.out.println("session:..." + spm.getStringValues("sub_session"));
		if (spm.getStringValues("sub_session").equals("add")) {
			Intent intent = new Intent(ListSubCategory.this, ListCategory.class);
			startActivity(intent);
			finish();

		} else {
			Intent intent = new Intent(ListSubCategory.this, Setting.class);
			startActivity(intent);
			finish();

		}

	}
	AllDataAppAdapter adapter ;
	LinearLayoutListView area1;
	private void viewsetUp() {
		// action_list.setVisibility(View.GONE);
		cat_list.setVisibility(View.VISIBLE);
		// device_list.setVisibility(View.GONE);
		cat_list.setOnTouchListener(new OnTouchListener() {
			// Setting on Touch Listener for handling the touch inside
			// ScrollView
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		System.out.println("id in view:..."
				+ spm.getStringValues(getString(R.string.Cat_id)));
		String name = new GetDataFrmDB().getCatNameByID(Integer.parseInt(spm
				.getStringValues(getString(R.string.Cat_id))), mDbHelper);

		if (name.length() > 13) {
			String newname = name.substring(0, 13);
			title.setText(newname + "..");
		} else {
			title.setText(name);
		}

		if (allData.size() > 0) {
			listViewClick();
			longClickListener();
			txt.setVisibility(View.GONE);
			cat_list.setVisibility(View.VISIBLE);
			area1 = (LinearLayoutListView) findViewById(R.id.pane1);

			area1.setOnDragListener(myOnDragListener); 
			
			area1.setListView(cat_list);
			
			Collections.sort(allData, new Comparator<AppendAllData>() {
			    @Override
			    public int compare(AppendAllData lhs, AppendAllData rhs) {
			        return lhs.getPos().compareTo(rhs.getPos());
			    }
			});
			
			adapter = new AllDataAppAdapter(this, allData,
					getApplicationContext().getResources(),mDbHelper,cat_list,new ListCategory().pos);

			cat_list.setAdapter(adapter);

			
		} else {
			txt.setVisibility(View.VISIBLE);
			cat_list.setVisibility(View.GONE);
		}
		System.out.println("sub_session:.."
				+ spm.getStringValues("sub_session"));

		if (spm.getStringValues("sub_session").equals("add")) {
			spm.saveStringValues("level", "level2");
		} else {
			title.setText("Select Sub-Category");
		}

	}
	
/*	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.v("requestCode:..",""+requestCode);
		//XMLParser parser = new XMLParser();
		adddata();
		adapter = new AllDataAppAdapter(this, allData,
				getApplicationContext().getResources(),mDbHelper,cat_list,new ListCategory().pos);
		cat_list.setAdapter(adapter);
		
		cat_list.setSelection(requestCode);
		//allData = parser.getAllCategoryResult(ListCategory.this, 0);
		adapter = new AllDataAppAdapter(this, allData,
				getApplicationContext().getResources(),mDbHelper);
		
	}
*/
	
	
	public boolean removeItemToList(ArrayList<AppendAllData> l, AppendAllData it) {
		boolean result = l.remove(it);
		return result;
	}

	private boolean addItemToList(ArrayList<AppendAllData> l, AppendAllData it) {
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

				SubCategoryPassObject passObj = (SubCategoryPassObject) event.getLocalState();
				View view = passObj.view;
				AppendAllData passedItem = passObj.item;
				ArrayList<AppendAllData> srcList = passObj.srcList;
				ListView oldParent = (ListView) view.getParent();
				AllDataAppAdapter srcAdapter = (AllDataAppAdapter) (oldParent.getAdapter());

				LinearLayoutListView newParent = (LinearLayoutListView) v;
				AllDataAppAdapter destAdapter = (AllDataAppAdapter) (newParent.listView
						.getAdapter());
				ArrayList<AppendAllData> destList = destAdapter.getList();

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
	
	

}
