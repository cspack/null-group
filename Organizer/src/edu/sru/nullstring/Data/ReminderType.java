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

	public String getCategoryID()
	{
		return Integer.toString(this.catid);
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
