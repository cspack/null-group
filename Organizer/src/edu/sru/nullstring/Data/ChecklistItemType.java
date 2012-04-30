/*
 * Copyright (c) 2012, SRU Cygnus Nullstring.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

*     Redistributions of source code must retain the above copyright notice, 
		this list of conditions and the following disclaimer.
	
*     Redistributions in binary form must reproduce the above copyright notice, 
     	this list of conditions and the following disclaimer in the documentation 
     	and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

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
