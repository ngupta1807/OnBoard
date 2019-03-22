package menuapp.activity.util.model;

import java.io.Serializable;


public class SubCatItemModel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private String KEY_CAT_ID;
	private String KEY_CAT_NAME;
	private String Key_DEVICE_PATH;
	private String KEY_SUB_CAT_ITEM_ID;	  
	private String KEY_SUB_CAT_ITEM_BODY;
	private String KEY_PIC;
	private String KEY_LINK;
	private String title;
	private int pos;
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	private String KEY_SUB_CAT_ITEM_Pos;
	
	
	
	public String getKEY_SUB_CAT_ITEM_Pos() {
		return KEY_SUB_CAT_ITEM_Pos;
	}
	public void setKEY_SUB_CAT_ITEM_Pos(String kEY_SUB_CAT_ITEM_Pos) {
		KEY_SUB_CAT_ITEM_Pos = kEY_SUB_CAT_ITEM_Pos;
	}
	public String getKEY_LINK() {
		return KEY_LINK;
	}
	public void setKEY_LINK(String kEY_LINK) {
		KEY_LINK = kEY_LINK;
	}
	public String getKEY_CAT_NAME() 
	{
		return KEY_CAT_NAME;
	}
	public void setKEY_CAT_NAME(String kEY_CAT_NAME)
	{
		KEY_CAT_NAME = kEY_CAT_NAME;
	}
	
	public String getTitle() 
	{
		return title;
	}
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public String getKEY_CAT_ID() {
		return KEY_CAT_ID;
	}
	public void setKEY_CAT_ID(String kEY_CAT_ID) {
		KEY_CAT_ID = kEY_CAT_ID;
	}
	public String getKEY_SUB_CAT_ITEM_ID() {
		return KEY_SUB_CAT_ITEM_ID;
	}
	public void setKEY_SUB_CAT_ITEM_ID(String kEY_SUB_CAT_ITEM_ID) {
		KEY_SUB_CAT_ITEM_ID = kEY_SUB_CAT_ITEM_ID;
	}
	public String getKey_DEVICE_PATH() {
		return Key_DEVICE_PATH;
	}
	public void setKey_DEVICE_PATH(String Key_DEVICE_PATH) {
		this.Key_DEVICE_PATH = Key_DEVICE_PATH;
	}
	public String getKEY_SUB_CAT_ITEM_BODY() {
		return KEY_SUB_CAT_ITEM_BODY;
	}
	public void setKEY_SUB_CAT_ITEM_BODY(String kEY_SUB_CAT_ITEM_BODY) {
		KEY_SUB_CAT_ITEM_BODY = kEY_SUB_CAT_ITEM_BODY;
	}
	
	public String getKEY_PIC() {
		return KEY_PIC;
	}
	public void setKEY_PIC(String kEY_PIC) {
		KEY_PIC = kEY_PIC;
	}
	  
}