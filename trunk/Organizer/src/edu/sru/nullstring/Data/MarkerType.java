package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;

import android.location.Location;

public class MarkerType extends BaseDaoEnabled<MarkerType, Integer>
{
	MarkerType()
	{
		// constructor for ormlite
	}

	public MarkerType(Dao<MarkerType, Integer> dao)
	{
		this.setDao(dao);
	}
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(index = true)
	private int catid;
	
	@DatabaseField
	private String title;
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}

	
	@DatabaseField
	private double latitude;
	
	@DatabaseField
	private double longitude;

	@DatabaseField
	private String locationStr;
	
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
