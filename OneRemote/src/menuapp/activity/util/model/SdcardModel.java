package menuapp.activity.util.model;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;

public class SdcardModel {
	private String Name = "";
	private String path = "";
	private Context ctx;
	
	private ArrayList<File> data;

	public SdcardModel(Context applicationContext, ArrayList<File> fileList) {
		ctx = applicationContext;
		data = fileList;
	}
	
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
