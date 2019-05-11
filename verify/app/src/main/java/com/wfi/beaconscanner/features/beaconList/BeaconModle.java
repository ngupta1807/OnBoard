package com.wfi.beaconscanner.features.beaconList;
/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To get/set data from database.
 */

public class BeaconModle {
	
	private String _id;
	private String batteryMilliVolts;
	private String distance;
	private String status;
	private String mode;
	private String lastSeen;
	private String lastminSeen;
	private String instanceId;
	private String server_status;
	private String saved_time;
	private String max_distance;

	public String getMax_distance() {
		return max_distance;
	}

	public void setMax_distance(String max_distance) {
		this.max_distance = max_distance;
	}

	public String getSaved_time() {
		return saved_time;
	}

	public void setSaved_time(String saved_time) {
		this.saved_time = saved_time;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getBatteryMilliVolts() {
		return batteryMilliVolts;
	}

	public void setBatteryMilliVolts(String batteryMilliVolts) {
		this.batteryMilliVolts = batteryMilliVolts;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	public String getLastminSeen() {
		return lastminSeen;
	}

	public void setLastminSeen(String lastminSeen) {
		this.lastminSeen = lastminSeen;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

}
