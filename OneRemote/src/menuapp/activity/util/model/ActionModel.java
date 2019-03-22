package menuapp.activity.util.model;

import java.io.Serializable;


public class ActionModel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private String KEY_CAT_ID;
	private String Key_ACTION_PATH;
	private String KEY_ACTION_ID;	  
	private String KEY_ACTION_BODY;
	private String KEY_ACTION_PIC;
	
	private int KEY_ACTION_POS;
	
	
	public int getKEY_ACTION_POS() {
		return KEY_ACTION_POS;
	}
	public void setKEY_ACTION_POS(int kEY_ACTION_POS) {
		KEY_ACTION_POS = kEY_ACTION_POS;
	}
	public String getKEY_CAT_ID() {
		return KEY_CAT_ID;
	}
	public void setKEY_CAT_ID(String kEY_CAT_ID) {
		KEY_CAT_ID = kEY_CAT_ID;
	}

	public String getKey_ACTION_PATH() {
		return Key_ACTION_PATH;
	}
	public void setKey_ACTION_PATH(String key_ACTION_PATH) {
		Key_ACTION_PATH = key_ACTION_PATH;
	}
	public String getKEY_ACTION_ID() {
		return KEY_ACTION_ID;
	}
	public void setKEY_ACTION_ID(String kEY_ACTION_ID) {
		KEY_ACTION_ID = kEY_ACTION_ID;
	}
	public String getKEY_ACTION_BODY() {
		return KEY_ACTION_BODY;
	}
	public void setKEY_ACTION_BODY(String kEY_ACTION_BODY) {
		KEY_ACTION_BODY = kEY_ACTION_BODY;
	}
	public String getKEY_ACTION_PIC() {
		return KEY_ACTION_PIC;
	}
	public void setKEY_ACTION_PIC(String kEY_ACTION_PIC) {
		KEY_ACTION_PIC = kEY_ACTION_PIC;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	  
}