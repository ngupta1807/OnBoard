package com.sample.app.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Status {
	@SerializedName("statuses")
	@Expose
	private ArrayList<Tweets> statuses = new ArrayList<Tweets>();

	public void setUser(List<Tweets> user) {
		this.statuses = statuses;
	}

	public  ArrayList<Tweets> getUser() {
		return statuses;
	}

}
