package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;


import android.location.Location;

@DatabaseTable (tableName="reminders")
public class ReminderType extends BaseDaoEnabled<ReminderType, Integer> {
	
	ReminderType()
	{
		
	}

	public ReminderType(Dao<ReminderType, Integer> dao)
	{
		this.setDao(dao);
	}
	

	public static final String REMINDER_ID_FIELD = "reminder_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";
	public static final String REMINDER_STATE_FIELD = "state";

	
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
	
	@DatabaseField(generatedId = true, columnName = REMINDER_ID_FIELD)
	private int id;
	
	@DatabaseField(index = true, columnName = CAT_ID_FIELD)
	private int catid;
	
	@DatabaseField(columnName = TITLE_FIELD)
	private String title;
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	@DatabaseField(columnName = REMINDER_STATE_FIELD)
	private ReminderState state = ReminderState.Loading;


	public int getCategoryID()
	{
		return this.catid;
	}
	public void setCategory(int cat)
	{
		this.catid = cat;
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
