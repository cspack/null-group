package edu.sru.nullstring.Data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "checklists")
public class ChecklistType {

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(index = true)
	private int catid;
	
	@DatabaseField
	private String title;
	
	ChecklistType()
	{
		// ormlite
	}

}
