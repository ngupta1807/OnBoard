package com.bookmyride.models;

import java.util.ArrayList;

public class CalendarCollection {
	public String day = "";
	public String time = "";
	public String event_message = "";
	
	public static ArrayList<CalendarCollection> date_collection_arr;
	public CalendarCollection(String date, String time, String event_message){
		this.day = date;
		this.time = time;
		this.event_message = event_message;
	}

	public String getDate() {
		return day;
	}
}
