package menuapp.activity.util;

import java.io.File;
import android.content.Context;

public class GetPathForImage {
	public String getBasePath(Context con) {
		SharedPreferencesManager spm=new SharedPreferencesManager(con);
		
		String iconsStoragePath = spm.getStringValues("basefolder") + "/Photos/";
		return iconsStoragePath;
	}

	public String getBasePathForCopy(Context con) {
		SharedPreferencesManager spm=new SharedPreferencesManager(con);
		
		String iconsStoragePath =spm.getStringValues("basefolder") + "/Photos";
		return iconsStoragePath;
	}

	public String getAllimages(String name,Context con) {
		String filename = "";
		SharedPreferencesManager spm=new SharedPreferencesManager(con);
		File direct = new File(spm.getStringValues("basefolder") + "/Photos");
		try{
		for (File f : direct.listFiles()) {
			if (f.isFile()) {
				if (f.getName().equals(name)) {
					filename = f.getName();
				}
			}
		}
		
		}
		catch(Exception ex){
			System.out.println("Error:"+ex.getMessage());
			filename="";
		}
		return filename;
	}
}
