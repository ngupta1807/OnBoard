package menuapp.activity.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class Compress {

	public boolean zipFileAtPath(String sourcePath, String toLocation,
			String name) {
		// ArrayList<String> contentList = new ArrayList<String>();
		final int BUFFER = 2048;

		File sourceFile = new File(sourcePath);
		try {
			BufferedInputStream origin = null;

			FileOutputStream dest = new FileOutputStream(toLocation);

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			if (sourceFile.isDirectory()) {
				System.out.println("sourcePath:..." + sourcePath);
				zipSubFolder(out, sourceFile, sourceFile.getParent().length(),
						name);
			} else {
				byte data[] = new byte[BUFFER];
				FileInputStream fi = new FileInputStream(sourcePath);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void zipSubFolder(ZipOutputStream out, File folder,
			int basePathLength, String name) throws IOException {

		final int BUFFER = 2048;

		File[] fileList = folder.listFiles();
		BufferedInputStream origin = null;
		for (File file : fileList) {
			System.out.println("data:.." + file.getAbsoluteFile());
			if (file.isDirectory()) {
				System.out.println("data inside:.." + file.isDirectory());
				try {
					byte data[] = new byte[BUFFER];
					String unmodifiedFilePath = file.getPath();

					String Path = unmodifiedFilePath.substring(basePathLength);

					String ar[] = Path.split(name + "/");

					String newpath = ar[1];

					System.out.println("newpath:..." + newpath);

					String relativePath = newpath;

					Log.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
					File r = new File(relativePath);
					if (!r.exists()) {
						Log.i("inside dir ", "creation");
						r.mkdirs();
					}

				} catch (Exception ex) {
					System.out.println("dir yes:.." + ex.getMessage());
				}
				zipSubFolder(out, file, basePathLength, name);
			} else {
				byte data[] = new byte[BUFFER];
				String unmodifiedFilePath = file.getPath();

				String Path = unmodifiedFilePath.substring(basePathLength);

				System.out.println("Path:.." + Path);
				
				String relativePath = "";
				
				if (Path.contains(name)) {

					System.out.println("name:.." + name);

					String ar[] = Path.split(name + "/");

					System.out.println("length" + ar.length);
					
				
					String newpath = ar[1];

					System.out.println("newpath:..." + newpath);

					relativePath = newpath;
				}
				 else {
					String newpath =Path.replaceFirst("/", "");
					
					System.out.println("newpath:..." + newpath);

					relativePath = newpath;
				}
				
				Log.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
				FileInputStream fi = new FileInputStream(unmodifiedFilePath);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(relativePath);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();

			}
		}
	}

	public String getLastPathComponent(String filePath) {
		String[] segments = filePath.split("/");
		String lastPathComponent = segments[segments.length - 1];
		return lastPathComponent;
	}

}