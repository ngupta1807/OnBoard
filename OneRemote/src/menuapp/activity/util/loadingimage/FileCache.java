package menuapp.activity.util.loadingimage;

import java.io.File;
import menuapp.activity.util.SharedPreferencesManager;
import android.content.Context;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			SharedPreferencesManager smp = new SharedPreferencesManager(context);
			cacheDir = new File(smp.getStringValues("basefolder")+"/Photos");
		} else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String path) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.

		path = path.substring(path.lastIndexOf("/") + 1, path.length());

		File imgFile = new File(cacheDir, path);
		return imgFile;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}