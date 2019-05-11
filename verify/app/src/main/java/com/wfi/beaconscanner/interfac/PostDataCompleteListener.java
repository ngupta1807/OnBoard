package com.wfi.beaconscanner.interfac;

import com.wfi.beaconscanner.models.BeaconSaved;

import java.util.List;

/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To get response from server api.
 */
public interface PostDataCompleteListener
{
	public void onTaskComplete(String result, List<BeaconSaved> arrayData);
}
