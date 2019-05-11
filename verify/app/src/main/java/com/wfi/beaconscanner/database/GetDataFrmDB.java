package com.wfi.beaconscanner.database;

import android.database.Cursor;

import com.wfi.beaconscanner.features.beaconList.BeaconModle;

import java.util.ArrayList;

/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To get data from database.
 */
public class GetDataFrmDB {
	/*
	 * Used : To get offline logs from database.
	 */
	public ArrayList<BeaconModle> getLimitedOfflineLogs(DbAdapter mhelper) {
		Cursor catcussor = null;
		BeaconModle am;
		ArrayList<BeaconModle> resultList = new ArrayList<BeaconModle>() ;
		try {
			catcussor = mhelper.fetchLimitedOfflineLogs();

			if (catcussor.moveToFirst()) {
				do {
					try {
						am = new BeaconModle();
						am.setBatteryMilliVolts(catcussor.getString(catcussor.getColumnIndex("batteryMilliVolts")));
						am.set_id(catcussor.getString(catcussor.getColumnIndex("_id")));
						am.setDistance(catcussor.getString(catcussor.getColumnIndex("Distance")));
						am.setInstanceId(catcussor.getString(catcussor.getColumnIndex("instanceId")));
						am.setLastminSeen(catcussor.getString(catcussor.getColumnIndex("lastminSeen")));
						am.setLastSeen(catcussor.getString(catcussor.getColumnIndex("lastSeen")));
						am.setMode(catcussor.getString(catcussor.getColumnIndex("mode")));
						am.setSaved_time(catcussor.getString(catcussor.getColumnIndex("savedTime")));
						am.setMax_distance(catcussor.getString(catcussor.getColumnIndex("status")));
						resultList.add(am);
					} catch (Exception e) {
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
		}finally {
			catcussor.close();
		}
		return resultList;
	}
	/*
	 * Used : To get LastSavedTime value from logs table in database.
	 */
	public String getBeaconNameByInstanceId(DbAdapter mhelper, String instanceId) {
		Cursor catcussor = null;
		String name="";
		try {
			catcussor = mhelper.fetchBeaconName(instanceId);
			if (catcussor.moveToFirst()) {
				do {

					name = catcussor.getString(catcussor
							.getColumnIndex("beacon_name"));
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
		}finally {
			catcussor.close();
		}
		return name;
	}
		/*
         * Used : To get LastSavedTime value from logs table in database.
         */
	public ArrayList<BeaconModle> getInstanceIdByBeaconName(DbAdapter mhelper) {
		Cursor catcussor = null;
		BeaconModle am;
		ArrayList<BeaconModle> resultList = new ArrayList<BeaconModle> ();
		try {
			catcussor = mhelper.fetchInstanceId("na");

			if (catcussor.moveToFirst()) {
				do {
					try {
						am = new BeaconModle();
						am.setInstanceId(catcussor.getString(catcussor.getColumnIndex("instance")));
						resultList.add(am);
					} catch (Exception e) {
					}

				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
		}finally {
			catcussor.close();
		}
		return resultList;
	}


	/*
	 * Used : To get LastSavedTime value from logs table in database.
	 */
	public int getCountByInstanceId(DbAdapter mhelper, String instanceId) {
		Cursor catcussor = null;
		int count=0;
		try {
			catcussor = mhelper.fetchBeaconName(instanceId);
			count=catcussor.getCount();
		} catch (Exception ex) {
		}finally {
			catcussor.close();
		}
		return count;
	}

}
