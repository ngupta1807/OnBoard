package menuapp.activity.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

public class Decompress {
	List<String> fileList;

	public void unZipIt(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];
		try {
			File folder = new File(outputFolder);

			if (!folder.exists()) {
				folder.mkdir();
			}

			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(zipFile));

			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();

				File newFile = new File(outputFolder + File.separator
						+ fileName);
				System.out.println("file:.." + newFile);
				String newfl = "" + newFile;

				if (newfl.contains("jpg") || newfl.contains("png")) {
					System.out.println("in file:..");
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;

					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				} else if (newfl.contains("txt")) {
					System.out.println("in file:..");
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;

					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				} else {
					System.out.println("in dir:..");
					// new File(newFile.getParent()).mkdirs();
				}

				ze = zis.getNextEntry();
				/* } */
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void unzip(Context ctx, String zipFile, String outputFolder) {
		try {
			InputStream inputStream;

			if (zipFile.equals(""))
				inputStream = ctx.getAssets().open("OneRemote.zip");
			// InputStream inputStream = new FileInputStream(zipFile);
			else
				inputStream = new FileInputStream(zipFile);

			ZipInputStream zipStream = new ZipInputStream(inputStream);

			ZipEntry zEntry = null;

			while ((zEntry = zipStream.getNextEntry()) != null) {

				if (zEntry.isDirectory()) {
					hanldeDirectory(zEntry.getName(), outputFolder);
				} else {
					FileOutputStream fout = new FileOutputStream(outputFolder
							+ "/" + zEntry.getName());

					BufferedOutputStream bufout = new BufferedOutputStream(fout);

					byte[] buffer = new byte[1024];

					int read = 0;

					while ((read = zipStream.read(buffer)) != -1) {
						bufout.write(buffer, 0, read);
					}

					zipStream.closeEntry();

					bufout.close();

					fout.close();
				}
			}
			zipStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void hanldeDirectory(String dir, String dest) {
		File f = new File(dest + dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

}