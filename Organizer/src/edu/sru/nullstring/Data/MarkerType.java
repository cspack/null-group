package edu.sru.nullstring.Data;

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

	@DatabaseField
	private double latitude;
	
	@DatabaseField
	private double longitude;

	Location location; // Generated in real time
	
	@DatabaseField
	private String locationStr;
	
	// public CategoryType Category;
	
	
}
