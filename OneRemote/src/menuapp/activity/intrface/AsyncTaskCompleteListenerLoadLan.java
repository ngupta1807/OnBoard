package menuapp.activity.intrface;

import java.util.ArrayList;

import menuapp.activity.util.model.LanModel;

public interface AsyncTaskCompleteListenerLoadLan 
{
	public void onTaskComplete(String result,ArrayList<LanModel> code);
	
}
