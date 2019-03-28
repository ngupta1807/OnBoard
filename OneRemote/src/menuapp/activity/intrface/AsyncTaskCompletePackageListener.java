package menuapp.activity.intrface;

import java.util.ArrayList;

import menuapp.activity.util.model.PackageItem;

public interface AsyncTaskCompletePackageListener 
{
	
	public int onTaskComplete(ArrayList<PackageItem> result);
	
}
