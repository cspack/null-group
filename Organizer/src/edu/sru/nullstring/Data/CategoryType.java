package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "categories")
public class CategoryType {

	@DatabaseField(generatedId = true)
	private int id;
	
	
	@DatabaseField(index = true)
	private String categoryName;
	
	CategoryType()
	{
		// required by ormlite
	}

	public List<ChecklistType> getChecklists(DatabaseHelper helper)
	{
		try {
			return helper.getChecklistDao().queryForEq("catid", id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for ChecklistType in cur category, sending empty list.");
			return new ArrayList<ChecklistType>();
		}
	}

	public List<MarkerType> getMarkers(DatabaseHelper helper)
	{
		try {
			return helper.getMarkerDao().queryForEq("catid", id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for MarkerType in cur category, sending empty list.");
			return new ArrayList<MarkerType>();
		}
	}

	public List<NoteType> getNotes(DatabaseHelper helper)
	{
		try {
			return helper.getNoteDao().queryForEq("catid", id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for NoteType in cur category, sending empty list.");
			return new ArrayList<NoteType>();
		}
	}
	

	public List<ReminderType> getReminders(DatabaseHelper helper)
	{
		try {
			return helper.getReminderDao().queryForEq("catid", id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for ReminderType in cur category, sending empty list.");
			return new ArrayList<ReminderType>();
		}
	}
	
	
	// possibly link to int IDs and make accessor methods for this?
	//List<MarkerType> markerCollection;
	//List<NoteType> reminderCollection;
	//List<NoteType> notes;

}