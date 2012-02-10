package edu.sru.nullstring.Data;

import java.util.GregorianCalendar;


import android.location.Location;

public class ReminderType {
	
	public static enum ReminderState 
	{
		Loading,
		Completed,
		Active,
		Disabled
	}
	
	public ReminderState State = ReminderState.Loading;
	public CategoryType Category;
	public String ReminderTitle;
	public boolean UseLocation;
	public MarkerType Location;
	public GregorianCalendar EventTime;
	public Object Note;
	
}
