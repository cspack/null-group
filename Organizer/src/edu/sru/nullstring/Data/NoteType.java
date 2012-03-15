package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

import edu.sru.nullstring.Data.CategoryType.FixedTypes;

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
	

	public NoteType(DatabaseHelper helper) {
		CategoryType curCat = helper.getCurrentCategory();
		try {
			this.setDao(helper.getNoteDao());
			if(curCat.getFixedType() == FixedTypes.All)
			{
				// query unsorted category
				PreparedQuery<CategoryType> unsortedCat = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, FixedTypes.All).prepare();
				curCat = helper.getCategoryDao().queryForFirst(unsortedCat);
			}
		} catch (SQLException e) {
			Log.e("Locadex", "Database helper failed when creating Note type.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.catid = curCat.getID();
	}


	public static final String NOTE_ID_FIELD = "note_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";

	
	
	@DatabaseField(generatedId = true, columnName = NOTE_ID_FIELD)
	private int id;
	
	@DatabaseField(index = true, columnName = CAT_ID_FIELD)
	private int catid;
	
	@DatabaseField (columnName = TITLE_FIELD)
	public String title;
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}

	//public Object NoteColor;
	//public Object Content;
	

	// Attachments are many to one note, generate with a query in real time
	public List<AttachmentType> getAttachments(DatabaseHelper h)
	{
		try {
			return h.getAttachmentDao().queryForEq(AttachmentType.NOTE_ID_FIELD, id);
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
			allres = h.getCategoryDao().queryForEq(CategoryType.CAT_ID_FIELD, this.catid);

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
