package menuapp.activity.validation;

import java.util.ArrayList;

import menuapp.activity.util.model.GetDownloadModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoadList {
	public ArrayList<GetDownloadModel> load(String response) {
		ArrayList<GetDownloadModel> arraylist = new ArrayList<GetDownloadModel>();
		try {
			JSONArray jarray = new JSONArray(response);
			if (jarray.length() > 0) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject json_data = jarray.getJSONObject(i);
					GetDownloadModel gdm = new GetDownloadModel();
					gdm.setId(json_data.getString("id"));
					gdm.setName(json_data.getString("name"));
					gdm.setChecksum(json_data.getString("checksum"));
					gdm.setDescription(json_data.getString("description"));
					gdm.setOriginal_filename(json_data
							.getString("original_filename"));
					gdm.setVersion(json_data.getString("version"));
					arraylist.add(gdm);
				}
			}

		} catch (Exception ex) {

		}
		return arraylist;
	}
}
