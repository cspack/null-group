package edu.sru.nullstring.Data;

import java.sql.SQLException;

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

	public ChecklistItemType(DatabaseHelper helper)
	{
		try {
			this.setDao(helper.getChecklistItemDao());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static final String ITEM_ID_FIELD = "item_id";
	public static final String LIST_ID_FIELD = "list_id";
	public static final String TEXT_FIELD = "text";
	public static final String IS_CHECKED_FIELD = "is_checked";
	
	
	@DatabaseField(generatedId = true, columnName = ITEM_ID_FIELD)
	private int id;

	@DatabaseField(index = true, columnName = LIST_ID_FIELD)
	private int listid;

	@DatabaseField(columnName = TEXT_FIELD)
	private String text;

	@DatabaseField(columnName = IS_CHECKED_FIELD)
	private Boolean checked;
	
}
