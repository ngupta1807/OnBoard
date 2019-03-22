package menuapp.activity.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import menuapp.activity.AddCategory;
import menuapp.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCompressing {

	public Bitmap decodeFile(File f, Context con) {
		SharedPreferencesManager spm = new SharedPreferencesManager(con);

		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			if (spm.getStringValues(con.getString(R.string.Scalling)).equals(
					"size")) {
				System.out.println("max size:..");
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
						f), null, o2);
				bitmap = Bitmap.createScaledBitmap(bitmap, Integer.parseInt(spm
						.getStringValues(con.getString(R.string.Width))),
						Integer.parseInt(spm.getStringValues(con
								.getString(R.string.Height))), false);
				return bitmap;
			} else {
				System.out.println("Enlarge:..");
				final int REQUIRED_SIZE = 1000;

				// Find the correct scale value. It should be the power of 2.
				int scale = 2;

				while (o.outWidth / scale > REQUIRED_SIZE
						&& o.outHeight / scale > REQUIRED_SIZE) {
					scale *= 2;
				}
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				if (o.outWidth <= 500) {
					o2.inSampleSize = 1;
				} else {
					o2.inSampleSize = scale;
				}
				return BitmapFactory.decodeStream(new FileInputStream(f), null,
						o2);
			}
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	File sdIconStorageDir;
	String file_name;

	public void saveImage(String path, Bitmap bitmap, Context con,
			String file_name) {

		path = path.substring(path.lastIndexOf("/") + 1, path.length());

		String iconsStoragePath = new GetPathForImage().getBasePath(con);

		File imgFile = new File(iconsStoragePath, path);

		System.out.println("Image path.." + imgFile);
		try {

			sdIconStorageDir = new File(iconsStoragePath);

			file_name = "" + file_name;

			File file = new File(sdIconStorageDir, file_name);

			if (file.exists())
				file.delete();

			FileOutputStream out = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
