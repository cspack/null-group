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
	
	public boolean getChecked()
	{
		return (this.checked);
	}
	
	public void setChecked(boolean newChecked)
	{
		this.checked = newChecked;
		try {
			this.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getListID()
	{
		return (this.listid);
	}
	
	public void setListID(int newListID)
	{
		this.listid = newListID;
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
	
	public void setID(int newID)
	{
		this.id = newID;
		try {
			this.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String newText)
	{
		this.text = newText;
		try {
			this.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
