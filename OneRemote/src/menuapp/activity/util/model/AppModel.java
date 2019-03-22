package menuapp.activity.util.model;

public class AppModel {

	private String Name = "";
	private String Desc = "";
	private String Pic = "";
	private String Id = "";
	private String Path = "";
	private int nest_id = 0;
	private int pos = 0;
	

	private int max_len = 0;
	
	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	/*********** Set Methods ******************/

	public String getName() {
		return Name;
	}

	public int getMax_len() {
		return max_len;
	}

	public void setMax_len(int max_len) {
		this.max_len = max_len;
	}

	public String getPath() {
		return Path;
	}

	public void setPath(String path) {
		Path = path;
	}

	public int getNest_id() {
		return nest_id;
	}

	public void setNest_id(int nest_id) {
		this.nest_id = nest_id;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getPic() {
		return Pic;
	}

	public void setPic(String pic) {
		Pic = pic;
	}

}