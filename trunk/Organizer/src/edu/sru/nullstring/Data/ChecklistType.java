package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "checklists")
public class ChecklistType extends BaseDaoEnabled<ChecklistType,Integer> {

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(index = true)
	private int catid = 1;
	
	@DatabaseField
	private String title = "Unlabeled";
	
	public ChecklistType(Dao<ChecklistType, Integer> dao)
	{
		this.setDao(dao);
	}
	
	ChecklistType()
	{
		// ormlite
	}
	
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	public String getCategoryID()
	{
		return Integer.toString(this.catid);
	}

	
	public List<ChecklistItemType> getItems(DatabaseHelper h)
	{
		try {
			return h.getChecklistItemDao().queryForEq("noteid", id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for Attachments for Note, sending empty list.");
			return new ArrayList<ChecklistItemType>();
		}
	}

	
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
