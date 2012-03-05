package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="notes")
public class NoteType {

	@DatabaseField(generatedId = true)
	private int id;
	
	//public CategoryType Category;
	
	@DatabaseField(index = true)
	public String title;
	
	//public Object NoteColor;
	//public Object Content;
	

	// Attachments are many to one note, generate with a query in real time
	public List<AttachmentType> getAttachments(DatabaseHelper h)
	{
		try {
			return h.getAttachmentDao().queryForEq("noteid", id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for Attachments for Note, sending empty list.");
			return new ArrayList<AttachmentType>();
		}
	}
	
	
	//public GregorianCalendar CreationDate;
	//public GregorianCalendar ModifiedDate;
	
}
