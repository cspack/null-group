package edu.sru.nullstring.Data;

import com.j256.ormlite.field.DatabaseField;

import android.location.Location;

public class MarkerType
{
	
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
	
	
	MarkerType()
	{
		// constructor for ormlite
	}
}
