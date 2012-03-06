package edu.sru.nullstring.Data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "checklist_items")
public class ChecklistItemType extends BaseDaoEnabled<ChecklistItemType,Integer> {

	ChecklistItemType()
	{
		// constructor for ormlite
	}

	public ChecklistItemType(Dao<ChecklistItemType, Integer> dao)
	{
		this.setDao(dao);
	}
	
	
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(index = true)
	private int listid;

	@DatabaseField
	private String text;

	@DatabaseField
	private Boolean checked;
	
}
