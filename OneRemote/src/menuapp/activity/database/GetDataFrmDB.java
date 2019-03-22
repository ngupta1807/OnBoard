package menuapp.activity.database;

import java.util.ArrayList;

import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GetDataFrmDB {
	public ArrayList<ActionModel> getAllAction(DbAdapter mhelper) {
		Cursor catcussor = null;
		ActionModel am;
		ArrayList<ActionModel> resultList = new ArrayList<ActionModel>();
		try {
			catcussor = mhelper.fetchAllaction();
			System.out.println("Start get action data....");
			System.out.println("action size:.." + catcussor.getCount());

			if (catcussor.moveToFirst()) {
				do {
					try {
						am = new ActionModel();

						am.setKEY_ACTION_BODY(catcussor.getString(catcussor
								.getColumnIndex("action_body")));
						am.setKEY_ACTION_ID(catcussor.getString(catcussor
								.getColumnIndex("action_id")));
						am.setKey_ACTION_PATH(catcussor.getString(catcussor
								.getColumnIndex("action_path")));
						am.setKEY_ACTION_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKEY_ACTION_POS(Integer.parseInt(catcussor.getString(catcussor
								.getColumnIndex("pos"))));

						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<ActionModel> getAllActionCatIdResults(DbAdapter mhelper,
			int rowid) {
		Cursor catcussor = null;
		ActionModel am;
		ArrayList<ActionModel> resultList = new ArrayList<ActionModel>();
		long id = (long) rowid;
		try {
			catcussor = mhelper.fetchActionByCatId(id);

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					am = new ActionModel();
					am.setKEY_ACTION_BODY(catcussor.getString(catcussor
							.getColumnIndex("action_body")));

					am.setKEY_ACTION_ID(catcussor.getString(catcussor
							.getColumnIndex("action_id")));
					am.setKey_ACTION_PATH(catcussor.getString(catcussor
							.getColumnIndex("action_path")));
					am.setKEY_ACTION_PIC(catcussor.getString(catcussor
							.getColumnIndex("pic")));
					am.setKEY_CAT_ID(catcussor.getString(catcussor
							.getColumnIndex("cat_id")));

					resultList.add(am);

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<ActionModel> getActionByActionId(DbAdapter mhelper,
			int rowid) {
		Cursor catcussor = null;
		ActionModel am;
		ArrayList<ActionModel> resultList = new ArrayList<ActionModel>();
		long id = (long) rowid;
		try {
			catcussor = mhelper.fetchActionByActionId(rowid);

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					am = new ActionModel();
					am.setKEY_ACTION_BODY(catcussor.getString(catcussor
							.getColumnIndex("action_body")));

					am.setKEY_ACTION_ID(catcussor.getString(catcussor
							.getColumnIndex("action_id")));
					am.setKey_ACTION_PATH(catcussor.getString(catcussor
							.getColumnIndex("action_path")));
					am.setKEY_ACTION_PIC(catcussor.getString(catcussor
							.getColumnIndex("pic")));
					am.setKEY_CAT_ID(catcussor.getString(catcussor
							.getColumnIndex("cat_id")));

					resultList.add(am);

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<ActionModel> getAllActionOrderByCatIdResults(
			DbAdapter mhelper) {
		Cursor catcussor = null;
		ActionModel am;
		ArrayList<ActionModel> resultList = new ArrayList<ActionModel>();

		try {
			catcussor = mhelper.fetchactionOrderByCatid();

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					am = new ActionModel();
					am.setKEY_ACTION_BODY(catcussor.getString(catcussor
							.getColumnIndex("action_body")));

					am.setKEY_ACTION_ID(catcussor.getString(catcussor
							.getColumnIndex("action_id")));
					am.setKey_ACTION_PATH(catcussor.getString(catcussor
							.getColumnIndex("action_path")));
					am.setKEY_ACTION_PIC(catcussor.getString(catcussor
							.getColumnIndex("pic")));
					am.setKEY_CAT_ID(catcussor.getString(catcussor
							.getColumnIndex("cat_id")));
					resultList.add(am);

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<AppModel> fetchAllCategoryOrderByNestId(DbAdapter mDbHelper) {
		Cursor catcussor = null;
		AppModel am;
		ArrayList<AppModel> resultList = new ArrayList<AppModel>();
		try {
			catcussor = mDbHelper.fetchAllCategoryOrderByNestId();

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					String cat_name = catcussor.getString(catcussor
							.getColumnIndex("cat_name"));
					String cat_id = catcussor.getString(catcussor
							.getColumnIndex("cat_id"));
					int nest_id = catcussor.getInt(catcussor
							.getColumnIndex("nest_id"));
					try {
						am = new AppModel();
						am.setName(cat_name);
						am.setDesc(catcussor.getString(catcussor
								.getColumnIndex("cat_desc")));
						am.setPic(catcussor.getString(catcussor
								.getColumnIndex("image_path")));
						am.setMax_len(catcussor.getInt(catcussor
								.getColumnIndex("max_len")));
						am.setId(cat_id);
						am.setNest_id(nest_id);
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<AppModel> getAllCategoryResult(DbAdapter mDbHelper) {
		Cursor catcussor = null;
		AppModel am;
		ArrayList<AppModel> resultList = new ArrayList<AppModel>();
		try {
			catcussor = mDbHelper.fetchAllCategory();

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					String cat_name = catcussor.getString(catcussor
							.getColumnIndex("cat_name"));
					String cat_id = catcussor.getString(catcussor
							.getColumnIndex("cat_id"));
					int nest_id = catcussor.getInt(catcussor
							.getColumnIndex("nest_id"));
					try {
						am = new AppModel();
						am.setName(cat_name);
						am.setDesc(catcussor.getString(catcussor
								.getColumnIndex("cat_desc")));
						am.setPic(catcussor.getString(catcussor
								.getColumnIndex("image_path")));
						am.setId(cat_id);
						am.setNest_id(nest_id);
						am.setMax_len(catcussor.getInt(catcussor
								.getColumnIndex("max_len")));
						am.setPath(catcussor.getString(catcussor
								.getColumnIndex("path")));
						am.setPos(Integer.parseInt(catcussor.getString(catcussor
								.getColumnIndex("pos"))));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<AppModel> getAllCategoryResultByNestID(
			DbAdapter mDbHelper, int id) {
		Cursor catcussor = null;
		AppModel am;
		Long nestid = (long) id;
		ArrayList<AppModel> resultList = new ArrayList<AppModel>();
		try {
			catcussor = mDbHelper.fetchCategoryIdByNestingId(nestid);

			if (catcussor.moveToFirst()) {
				do {
					String cat_name = catcussor.getString(catcussor
							.getColumnIndex("cat_name"));
					String cat_id = catcussor.getString(catcussor
							.getColumnIndex("cat_id"));
					int nest_id = catcussor.getInt(catcussor
							.getColumnIndex("nest_id"));

					System.out.println("nest id on db method:.." + nest_id);
					try {
						am = new AppModel();
						am.setName(cat_name);
						am.setDesc(catcussor.getString(catcussor
								.getColumnIndex("cat_desc")));
						am.setPic(catcussor.getString(catcussor
								.getColumnIndex("image_path")));
						am.setId(cat_id);
						am.setNest_id(nest_id);
						am.setMax_len(catcussor.getInt(catcussor
								.getColumnIndex("max_len")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<AppModel> getAllCategoryResultById(DbAdapter mDbHelper,
			int rowid) {
		Cursor catcussor = null;
		AppModel am;
		long id = (int) rowid;
		ArrayList<AppModel> resultList = new ArrayList<AppModel>();
		try {
			catcussor = mDbHelper.fetchCategory(id);

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					String cat_name = catcussor.getString(catcussor
							.getColumnIndex("cat_name"));
					String cat_id = catcussor.getString(catcussor
							.getColumnIndex("cat_id"));
					String cat_desc = catcussor.getString(catcussor
							.getColumnIndex("cat_desc"));
					String pic_path = catcussor.getString(catcussor
							.getColumnIndex("image_path"));

					try {
						am = new AppModel();
						am.setName(cat_name);
						am.setDesc(cat_desc);
						am.setPic(pic_path);
						am.setId(cat_id);
						am.setMax_len(catcussor.getInt(catcussor
								.getColumnIndex("max_len")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<AppModel> getCatIDMatchesWithPath(DbAdapter mhelper, int id) {
		Cursor catcussor = null;
		String path = "";
		String cat_ids = "";
		AppModel am;
		ArrayList<AppModel> resultList = new ArrayList<AppModel>();
		try {
			catcussor = mhelper.fetchAllCategory();

			if (catcussor.moveToFirst()) {
				do {
					path = catcussor
							.getString(catcussor.getColumnIndex("path"));

					int catid = catcussor.getInt(catcussor
							.getColumnIndex("cat_id"));
					String ar[] = path.split(",");
					for (int i = 0; i < ar.length; i++) {
						System.out.println("ar[i].." + ar[i]);
						System.out.println("id.." + id);
						if (ar[i].equals("" + id)) {
							/*if (catid == id) {
								System.out.println("root id:...");
							} else {*/
								System.out.println("Match");

								String cat_name = catcussor.getString(catcussor
										.getColumnIndex("cat_name"));
								String nest_id = catcussor.getString(catcussor
										.getColumnIndex("nest_id"));
								String cat_id = catcussor.getString(catcussor
										.getColumnIndex("cat_id"));
								String cat_desc = catcussor.getString(catcussor
										.getColumnIndex("cat_desc"));
								String pic_path = catcussor.getString(catcussor
										.getColumnIndex("image_path"));
								String dev_path = catcussor.getString(catcussor
										.getColumnIndex("path"));
								try {
									am = new AppModel();
									am.setName(cat_name);
									am.setDesc(cat_desc);
									am.setPic(pic_path);
									am.setId(cat_id);
									am.setNest_id(Integer.parseInt(nest_id));
									am.setPath(dev_path);
									am.setMax_len(catcussor.getInt(catcussor
											.getColumnIndex("max_len")));
									resultList.add(am);
								} catch (Exception e) {
									Log.e("List Category",
											"Error " + e.toString());
								}
							/*}*/
							break;
						} else {
							System.out.println("Not Match");
						}

					}

					System.out.println("cat_ids:..." + cat_ids);
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public void updateRootFolderMaxLen(DbAdapter mDbHelper, String id,
			int lenght) {
		Cursor catcussor = null;
		// int check_root_id=Integer.parseInt(id);

		try {
			catcussor = mDbHelper.fetchAllCategory();

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					String cat_id = catcussor.getString(catcussor
							.getColumnIndex("cat_id"));
					System.out.println("cat_id cheking:" + cat_id);
					System.out.println("id if exists:" + id);
					if (cat_id.equals(id)) {
						int max_len = catcussor.getInt(catcussor
								.getColumnIndex("max_len"));
						System.out.println("max_len:" + max_len);
						System.out.println("path.length:" + (lenght - 1));
						if (max_len < (lenght - 1)) {
							System.out.println("max_len:.." + (max_len + 1));
							mDbHelper.updateCategory(Integer.parseInt(cat_id),
									(max_len + 1));
						}
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}

	}

	public ArrayList<SubCatItemModel> getAllSubCategoryItemOrderByResult2(
			DbAdapter mDbHelper) {
		Cursor catcussor = null;
		SubCatItemModel am;
		// long id = (int) rowid;
		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();
		try {
			catcussor = mDbHelper.fetchSubCategoryItemOrderBy();
			if (catcussor.moveToFirst()) {
				do {

					try {
						am = new SubCatItemModel();

						am.setKEY_SUB_CAT_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("sub_cat_item_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_DEVICE_PATH(catcussor.getString(catcussor
								.getColumnIndex("device_path")));
						am.setKEY_SUB_CAT_ITEM_BODY(catcussor
								.getString(catcussor
										.getColumnIndex("sub_cat_item_name")));
						am.setKEY_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));

						am.setKEY_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_SUB_CAT_ITEM_Pos(catcussor.getString(catcussor
								.getColumnIndex("pos")));

						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<AppModel> getAllSubCategoryItemByCatSubCatIdResult(
			DbAdapter mDbHelper, int cat_id, int sub_cat_id) {
		Cursor catcussor = null;
		AppModel am;
		long catid = (long) cat_id;
		long subcatid = (long) sub_cat_id;
		ArrayList<AppModel> resultList = new ArrayList<AppModel>();
		try {
			catcussor = mDbHelper.fetchSubCategoryItemByCatAndSubCatId(catid,
					subcatid);

			System.out.println("count:" + catcussor.getCount());
			if (catcussor.moveToFirst()) {
				do {
					String cat_name = catcussor.getString(catcussor
							.getColumnIndex("sub_cat_item_name"));
					String sub_cat_item_id = catcussor.getString(catcussor
							.getColumnIndex("sub_cat_item_id"));
					try {
						am = new AppModel();
						am.setName(cat_name);
						am.setId(sub_cat_item_id);
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	// -----------------------------GetRowByID---------------------------------

	public ArrayList<SubCatItemModel> getAllSubCategoryItemResult(
			DbAdapter mDbHelper, int rowid) {
		Cursor catcussor = null;
		SubCatItemModel am;
		long id = (int) rowid;
		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();
		try {
			catcussor = mDbHelper.fetchSubCategoryItem(id);
			if (catcussor.moveToFirst()) {
				do {

					try {
						am = new SubCatItemModel();

						am.setKEY_SUB_CAT_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("sub_cat_item_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_DEVICE_PATH(catcussor.getString(catcussor
								.getColumnIndex("device_path")));
						am.setKEY_SUB_CAT_ITEM_BODY(catcussor
								.getString(catcussor
										.getColumnIndex("sub_cat_item_name")));

						am.setKEY_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<SubCatItemModel> getAllSubCategoryItemByCatId(
			DbAdapter mDbHelper, int rowid) {
		Cursor catcussor = null;
		SubCatItemModel am;
		long id = (int) rowid;
		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();
		try {
			catcussor = mDbHelper.fetchSubCategoryItemByCatId(id);
			if (catcussor.moveToFirst()) {
				do {

					try {
						am = new SubCatItemModel();

						am.setKEY_SUB_CAT_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("sub_cat_item_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_DEVICE_PATH(catcussor.getString(catcussor
								.getColumnIndex("device_path")));
						am.setKEY_SUB_CAT_ITEM_BODY(catcussor
								.getString(catcussor
										.getColumnIndex("sub_cat_item_name")));

						am.setKEY_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<SubCatItemModel> getAllSubCategoryItemResult2(
			DbAdapter mDbHelper) {
		Cursor catcussor = null;
		SubCatItemModel am;
		// long id = (int) rowid;
		ArrayList<SubCatItemModel> resultList = new ArrayList<SubCatItemModel>();
		try {
			catcussor = mDbHelper.fetchSubCategoryItem();
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("device path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new SubCatItemModel();

						am.setKEY_SUB_CAT_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("sub_cat_item_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_DEVICE_PATH(catcussor.getString(catcussor
								.getColumnIndex("device_path")));
						am.setKEY_SUB_CAT_ITEM_BODY(catcussor
								.getString(catcussor
										.getColumnIndex("sub_cat_item_name")));

						am.setKEY_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_SUB_CAT_ITEM_Pos(catcussor.getString(catcussor
								.getColumnIndex("pos")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public SubCatItemModel getOneSubCategoryItemResult(DbAdapter mDbHelper,
			int rowid) {
		Cursor catcussor = null;
		SubCatItemModel am = null;
		long id = (int) rowid;
		try {
			catcussor = mDbHelper.fetchSubCategoryItem(id);
			if (catcussor.moveToFirst()) {
				do {

					try {
						am = new SubCatItemModel();
						am.setKey_DEVICE_PATH(catcussor.getString(catcussor
								.getColumnIndex("device_path")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKEY_SUB_CAT_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("sub_cat_item_id")));
						am.setKEY_SUB_CAT_ITEM_BODY(catcussor
								.getString(catcussor
										.getColumnIndex("sub_cat_item_name")));
						am.setKEY_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
					} catch (Exception e) {
						Log.e("List Category", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return am;
	}

	public ArrayList<LinkModel> getAllLinkResult(DbAdapter mDbHelper) {
		Cursor catcussor = null;
		LinkModel am;
		ArrayList<LinkModel> resultList = new ArrayList<LinkModel>();
		try {
			catcussor = mDbHelper.fetchLink();
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("device path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new LinkModel();

						am.setKEY_LINK_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("link_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_LINK_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_LINK_BODY(catcussor.getString(catcussor
								.getColumnIndex("link_name")));

						am.setKEY_LINK_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_LINK_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						am.setkEY_LINK_STATUS(catcussor.getString(catcussor
								.getColumnIndex("status")));
						am.setKEY_LINK_POS(catcussor.getString(catcussor
								.getColumnIndex("pos")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List link", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<LinkModel> getAllLinkItemResult(DbAdapter mDbHelper,
			int rowid) {
		Cursor catcussor = null;
		LinkModel am;
		long id = (int) rowid;
		ArrayList<LinkModel> resultList = new ArrayList<LinkModel>();
		try {
			catcussor = mDbHelper.fetchLinkItem(id);
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("link path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new LinkModel();

						am.setKEY_LINK_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("link_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_LINK_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_LINK_BODY(catcussor.getString(catcussor
								.getColumnIndex("link_name")));

						am.setKEY_LINK_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_LINK_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						am.setkEY_LINK_STATUS(catcussor.getString(catcussor
								.getColumnIndex("status")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List link", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<LinkModel> getAllLinkItemByCatid(DbAdapter mDbHelper,
			int rowid) {
		Cursor catcussor = null;
		LinkModel am;
		long id = (int) rowid;
		ArrayList<LinkModel> resultList = new ArrayList<LinkModel>();
		try {
			catcussor = mDbHelper.fetchLinkItemByCatid(id);
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("link path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new LinkModel();

						am.setKEY_LINK_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("link_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_LINK_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_LINK_BODY(catcussor.getString(catcussor
								.getColumnIndex("link_name")));

						am.setKEY_LINK_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_LINK_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_LINK_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List link", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}
	
	/*
	public ArrayList<SwitchModel> getAllSwitchItemResult(DbAdapter mDbHelper,
			int rowid) {
		Cursor catcussor = null;
		SwitchModel am;
		long id = (int) rowid;
		ArrayList<SwitchModel> resultList = new ArrayList<SwitchModel>();
		try {
			catcussor = mDbHelper.fetchSwitchItem(id);
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("link path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new SwitchModel();
						
						am.setKEY_SWITCH_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("switch_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_SWITCH_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_SWITCH_BODY(catcussor.getString(catcussor
								.getColumnIndex("switch_name")));

						am.setKEY_SWITCH_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_SWITCH_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_SWITCH_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						am.setkEY_SWITCH_STATUS(catcussor.getString(catcussor
								.getColumnIndex("status")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List SWITCH", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}*/
	
	public ArrayList<SwitchModel> getAllSwitchResult(DbAdapter mDbHelper) {
		Cursor catcussor = null;
		SwitchModel am;
		ArrayList<SwitchModel> resultList = new ArrayList<SwitchModel>();
		try {
			catcussor = mDbHelper.fetchSwitch();
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("device path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new SwitchModel();
						
						am.setKEY_SWITCH_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("switch_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_SWITCH_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_SWITCH_BODY(catcussor.getString(catcussor
								.getColumnIndex("switch_name")));

						am.setKEY_SWITCH_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_SWITCH_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_SWITCH_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						
						System.out.println("setting status:..."
								+ catcussor.getString(catcussor
										.getColumnIndex("status")));
						am.setkEY_SWITCH_STATUS(catcussor.getString(catcussor
								.getColumnIndex("status")));
						am.setKEY_SWITCH_POS(catcussor.getString(catcussor
								.getColumnIndex("pos")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List SWITCH", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<SwitchModel> getAllSwitchItemResult(DbAdapter mDbHelper,int rowid) {
		Cursor catcussor = null;
		SwitchModel am;
		long id = (int) rowid;
		ArrayList<SwitchModel> resultList = new ArrayList<SwitchModel>();
		try {
			catcussor = mDbHelper.fetchSwitchItem(id);
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("Switch path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new SwitchModel();

						am.setKEY_SWITCH_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("switch_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_SWITCH_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_SWITCH_BODY(catcussor.getString(catcussor
								.getColumnIndex("switch_name")));

						am.setKEY_SWITCH_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_SWITCH_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_SWITCH_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						
						System.out.println("setting status:..."
								+ catcussor.getString(catcussor
										.getColumnIndex("status")));
						am.setkEY_SWITCH_STATUS(catcussor.getString(catcussor
								.getColumnIndex("status")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List SWITCH", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}

	public ArrayList<SwitchModel> getAllSwitchItemByCatid(DbAdapter mDbHelper,
			int rowid) {
		Cursor catcussor = null;
		SwitchModel am;
		long id = (int) rowid;
		ArrayList<SwitchModel> resultList = new ArrayList<SwitchModel>();
		try {
			catcussor = mDbHelper.fetchSwitchItemByCatid(id);
			if (catcussor.moveToFirst()) {
				do {

					System.out.println("Switch path:..."
							+ catcussor.getString(catcussor
									.getColumnIndex("device_path")));
					try {
						am = new SwitchModel();

						am.setKEY_SWITCH_ITEM_ID(catcussor.getString(catcussor
								.getColumnIndex("switch_id")));
						am.setKEY_CAT_ID(catcussor.getString(catcussor
								.getColumnIndex("cat_id")));
						am.setKey_SWITCH_DEVICE_PATH(catcussor
								.getString(catcussor
										.getColumnIndex("device_path")));
						am.setKEY_SWITCH_BODY(catcussor.getString(catcussor
								.getColumnIndex("switch_name")));

						am.setKEY_SWITCH_PIC(catcussor.getString(catcussor
								.getColumnIndex("pic")));
						am.setKEY_SWITCH_LINK(catcussor.getString(catcussor
								.getColumnIndex("link")));
						am.setKEY_SWITCH_DATA(catcussor.getString(catcussor
								.getColumnIndex("data")));
						
						System.out.println("setting status:..."
								+ catcussor.getString(catcussor
										.getColumnIndex("status")));
						am.setkEY_SWITCH_STATUS(catcussor.getString(catcussor
								.getColumnIndex("status")));
						resultList.add(am);
					} catch (Exception e) {
						Log.e("List SWITCH", "Error " + e.toString());
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return resultList;
	}
	

	// /////////////Common Queryes/////////////////////////

	public String getCatNameByID(int id, DbAdapter mhelper) {
		Cursor catcussor = null;
		String cat_name = "";
		long cat_id = (long) id;
		try {
			catcussor = mhelper.fetchCategory(cat_id);

			if (catcussor.moveToFirst()) {
				do {
					cat_name = catcussor.getString(catcussor
							.getColumnIndex("cat_name"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return cat_name;
	}

	public String getCatPathByCatID(int id, DbAdapter mhelper) {
		Cursor catcussor = null;
		String path = "";
		long cat_id = (long) id;
		try {
			catcussor = mhelper.fetchCategory(cat_id);

			if (catcussor.moveToFirst()) {
				do {
					path = catcussor
							.getString(catcussor.getColumnIndex("path"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return path;
	}

	public String getCatIDMatchesWithPath(int id, DbAdapter mhelper) {
		Cursor catcussor = null;
		String path = "";
		String cat_ids = "";
		try {
			catcussor = mhelper.fetchAllCategory();

			if (catcussor.moveToFirst()) {
				do {
					path = catcussor
							.getString(catcussor.getColumnIndex("path"));
					String ar[] = path.split(",");
					for (int i = 0; i < ar.length; i++) {
						System.out.println("ar[i].." + ar[i]);
						System.out.println("id.." + id);
						if (ar[i].equals("" + id)) {
							System.out.println("Match");
							if (cat_ids.equals("")) {
								cat_ids = catcussor.getString(catcussor
										.getColumnIndex("cat_id"));
							} else {
								cat_ids = cat_ids
										+ ","
										+ catcussor.getString(catcussor
												.getColumnIndex("cat_id"));
							}
							break;
						} else {
							System.out.println("Not Match");
						}

					}

					System.out.println("cat_ids:..." + cat_ids);
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return cat_ids;
	}

	
	
	
	public String getCatIDByName(String name, DbAdapter mhelper) {
		Cursor catcussor = null;
		String catid = "0";
		try {
			catcussor = mhelper.fetchCategoryIdByName(name);

			if (catcussor.moveToFirst()) {
				do {
					catid = catcussor.getString(catcussor
							.getColumnIndex("cat_id"));
				} while (catcussor.moveToNext());

				System.out.println("catid converting:...."+catid);
			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return catid;
	}

	public String getPathByCat_id(int id, DbAdapter mhelper) {
		Cursor catcussor = null;
		String path = "";
		long cat_id = (long) id;
		try {
			catcussor = mhelper.fetchPathByCat_id(cat_id);

			if (catcussor.moveToFirst()) {
				do {
					path = catcussor
							.getString(catcussor.getColumnIndex("path"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return path;
	}

	public String getActionCatNameByID(DbAdapter mhelper, int sub_cat_id) {
		Cursor catcussor = null;
		String sub_cat_name = "";
		long subcat_id = (long) sub_cat_id;
		try {
			catcussor = mhelper.fetchaction(subcat_id);

			if (catcussor.moveToFirst()) {
				do {
					sub_cat_name = catcussor.getString(catcussor
							.getColumnIndex("action_body"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return sub_cat_name;
	}

	public String getUrlfromDeviceID(DbAdapter mhelper, int sub_cat_id) {
		Cursor catcussor = null;
		String url = "";
		long subcat_id = (long) sub_cat_id;
		try {
			catcussor = mhelper.fetchSubCategoryItem(subcat_id);

			if (catcussor.moveToFirst()) {
				do {
					url = catcussor.getString(catcussor.getColumnIndex("link"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return url;
	}

	public String getLinkUrlfromDeviceID(DbAdapter mhelper, int sub_cat_id) {
		Cursor catcussor = null;
		String url = "";
		long subcat_id = (long) sub_cat_id;
		try {
			catcussor = mhelper.fetchLinkItem(subcat_id);

			if (catcussor.moveToFirst()) {
				do {
					url = catcussor.getString(catcussor.getColumnIndex("link"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return url;
	}
	
	public String getLinkUrlWithdatafromDeviceID(DbAdapter mhelper, int sub_cat_id) {
		Cursor catcussor = null;
		String url = "";
		long subcat_id = (long) sub_cat_id;
		try {
			catcussor = mhelper.fetchLinkItem(subcat_id);

			if (catcussor.moveToFirst()) {
				do {
					url = catcussor.getString(catcussor
									.getColumnIndex("data"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return url;
	}
	
	
	public String getSwitchUrlfromDeviceID(DbAdapter mhelper, int sub_cat_id) {
		Cursor catcussor = null;
		String url = "";
		long subcat_id = (long) sub_cat_id;
		try {
			catcussor = mhelper.fetchSwitchItem(subcat_id);

			if (catcussor.moveToFirst()) {
				do {
					url = catcussor.getString(catcussor.getColumnIndex("link"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return url;
	}
	

	public String getSwitchUrlWithdatafromDeviceID(DbAdapter mhelper, int sub_cat_id) {
		Cursor catcussor = null;
		String url = "";
		long subcat_id = (long) sub_cat_id;
		try {
			catcussor = mhelper.fetchSwitchItem(subcat_id);

			if (catcussor.moveToFirst()) {
				do {
					url = catcussor.getString(catcussor
									.getColumnIndex("data"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return url;
	}
	
	/*
	 * public String getSubIdBySubCatName(DbAdapter mhelper, String sub_cat) {
	 * Cursor catcussor = null; String sub_cat_id = "0";
	 * 
	 * try { catcussor = mhelper.fetchactionid(sub_cat); if
	 * (catcussor.moveToFirst()) { do { sub_cat_id =
	 * catcussor.getString(catcussor .getColumnIndex("action_id")); } while
	 * (catcussor.moveToNext());
	 * 
	 * } } catch (Exception ex) { System.out.println("Error:" +
	 * ex.getMessage()); } return sub_cat_id; }
	 */

	public String getnestpath(DbAdapter mhelper, int nest_id) {
		Cursor catcussor = null;
		long id = (long) nest_id;
		String return_id = "";
		try {

			catcussor = mhelper.getnestpath(id);
			if (catcussor.moveToFirst()) {
				do {
					return_id = catcussor.getString(catcussor
							.getColumnIndex("path")) + ",";
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return return_id;
	}

}
