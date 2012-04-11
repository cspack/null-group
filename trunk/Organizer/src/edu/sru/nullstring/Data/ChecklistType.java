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

@DatabaseTable(tableName = "checklists")
public class ChecklistType extends BaseDaoEnabled<ChecklistType,Integer> {

	public static final String LIST_ID_FIELD = "list_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";
	
	@DatabaseField(generatedId = true, columnName = LIST_ID_FIELD )
	private int id;
	
	@DatabaseField(index = true, columnName = CAT_ID_FIELD)
	private int catid = 1;
	
	@DatabaseField(columnName = TITLE_FIELD)
	private String title = "Unlabeled";
	
	public ChecklistType(DatabaseHelper helper)
	{
		CategoryType curCat = helper.getCurrentCategory();
		try {
			this.setDao(helper.getChecklistDao());
			if(curCat.getFixedType() == FixedTypes.All)
			{
				// query unsorted category
				PreparedQuery<CategoryType> unsortedCat = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, FixedTypes.Unsorted).prepare();
				curCat = helper.getCategoryDao().queryForFirst(unsortedCat);
			}
		} catch (SQLException e) {
			Log.e("Locadex", "Database helper failed when creating Checklist type.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.catid = curCat.getID();
		
	}
	
	ChecklistType()
	{
		// ormlite
	}
	
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
		try {
			this.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getTitle()
	{
		return this.title;
	}

	public int getCategoryID()
	{
		return this.catid;
	}
	

	public void setCategory(int newId)
	{
		this.catid = newId;
	}
	
	public void setCategoryID(int newCatID)
	{
		this.catid = newCatID;
		try {
			this.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getID()
	{
		return (this.id);
	}

	public List<ChecklistItemType> getItems(DatabaseHelper h)
	{
		try {
			return h.getChecklistItemDao().queryForEq(ChecklistItemType.LIST_ID_FIELD, id);
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
