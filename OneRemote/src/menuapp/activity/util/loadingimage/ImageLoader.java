package menuapp.activity.util.loadingimage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import menuapp.activity.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Context con;

	public ImageLoader(Context context) {
		con = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	final int stub_id = R.color.no_image;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null){
			System.out.println("width :.."+bitmap.getWidth());
			Matrix m = imageView.getImageMatrix();
			RectF drawableRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
			RectF viewRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
			m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
			imageView.setImageMatrix(m);
			imageView.setImageBitmap(bitmap);
		}
		else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		// System.out.println("Url:.."+url);
		File f = fileCache.getFile(url);
		Bitmap b=null;
		// Bitmap b = null;
		// System.out.println("f:.."+f);

		// from SD cache
		try {
			
		Bitmap bitmap = decodeFile(f);
		
		Matrix matrix = new Matrix();
		
			ExifInterface exif = new ExifInterface(f.getAbsolutePath());

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);

			Log.v("TAG", "" + orientation);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.preRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.preRotate(180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.preRotate(270);
				break;
			default:
				break;
			}

			 b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			/*
			 * System.out.println("Width::" + newb.getWidth());
			 * System.out.println("Height::" + newb.getHeight()); if
			 * (newb.getWidth() > 1080) { b = newb.createScaledBitmap(newb,
			 * 1080, 100, false); } else { b = newb.createScaledBitmap(newb,
			 * newb.getWidth(), 100, false); }
			 */
			System.out.println("new Width::" + b.getWidth());
			System.out.println("new Height::" + b.getHeight());
		} catch (Exception ex) {
			return b;
		}
		return b;

		// from web
		/*
		 * try { Bitmap bitmap=null; URL imageUrl = new URL(url);
		 * HttpURLConnection conn =
		 * (HttpURLConnection)imageUrl.openConnection();
		 * conn.setConnectTimeout(30000); conn.setReadTimeout(30000);
		 * conn.setInstanceFollowRedirects(true); InputStream
		 * is=conn.getInputStream(); OutputStream os = new FileOutputStream(f);
		 * Utils.CopyStream(is, os); os.close(); bitmap = decodeFile(f); return
		 * bitmap; } catch (Throwable ex){ ex.printStackTrace(); if(ex
		 * instanceof OutOfMemoryError) memoryCache.clear(); return null; }
		 */
	}

	// decodes image and scales it to reduce memory consumption
	@SuppressWarnings("deprecation")
	private Bitmap decodeFile(File f) {
		System.gc();
		try {			
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			/*
			 * Bitmap bitmap = BitmapFactory.decodeFile( f.getAbsolutePath(),
			 * o);
			 */

			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			
			// Find the correct scale value. It should be the power of 2.
			WindowManager wm = (WindowManager) con
					.getSystemService(Context.WINDOW_SERVICE);

			Display display = wm.getDefaultDisplay();

			
			final int REQUIRED_SIZE = display.getWidth();
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 6;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();

			/*if (o.outWidth < display.getWidth()) {
				System.out.println("not scale:..");
				o2.inSampleSize = 0;
			} else {*/
				System.out.println("scale:..");
				o2.inSampleSize = scale;
			/*}*/
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			// return bitmap;
		} catch (Exception e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}