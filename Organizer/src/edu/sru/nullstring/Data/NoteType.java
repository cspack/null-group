package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="notes")
public class NoteType extends BaseDaoEnabled<NoteType, Integer> {

	NoteType()
	{
		// ormlite
	}
	public NoteType(Dao<NoteType, Integer> dao)
	{
		this.setDao(dao);
	}
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(index = true)
	private int catid;
	
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

	public CategoryType getCategory(DatabaseHelper h)
	{
		CategoryType result = null;
		try {
			List<CategoryType> allres;
			allres = h.getCategoryDao().queryForEq("id", this.catid);

			if(allres.size() > 0)
			{
				result = allres.get(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
