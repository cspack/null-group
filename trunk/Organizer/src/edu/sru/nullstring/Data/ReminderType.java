package edu.sru.nullstring.Data;

import java.util.GregorianCalendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;


import android.location.Location;

@DatabaseTable (tableName="reminders")
public class ReminderType extends BaseDaoEnabled<ReminderType, Integer> {
	
	public ReminderType()
	{
		
	}
	
	public static enum ReminderState 
	{
		Loading,
		Completed,
		Active,
		Disabled
	}
	
	public static enum ReminderTypes
	{
		Location,
		Quick,
		Advanced
	}
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(index = true)
	private int catid;
	
	@DatabaseField
	private String title;
	
	@DatabaseField
	private ReminderState state = ReminderState.Loading;

	

	
}
