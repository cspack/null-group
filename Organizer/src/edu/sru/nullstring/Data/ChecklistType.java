package edu.sru.nullstring.Data;

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

}
