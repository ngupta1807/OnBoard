package menuapp.activity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesManager 
{
	Context ctx;

	SharedPreferences prefs;

	public SharedPreferencesManager(Context ctx) 
	{
		this.ctx = ctx;
		prefs = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
	}
	
	public void saveStringValues(String key, String value)
	{
		Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void saveBoolValues(String key, boolean value)
	{
		Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	public String getStringValues(String key) 
	{
		return prefs.getString(key, "0");
	}

	public boolean getBoolValues(String key)
	{
		return prefs.getBoolean(key, false);
	}
}
