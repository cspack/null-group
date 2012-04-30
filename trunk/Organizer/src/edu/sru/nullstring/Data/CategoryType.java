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
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "categories")
public class CategoryType extends BaseDaoEnabled<CategoryType,Integer> {

	public static enum FixedTypes
	{
		None,
		All,
		Unsorted
	}

	CategoryType()
	{
		// constructor for ormlite
	}
	
	// Primarily for creating the 'All Categories' category, and the 'Unknown' category
	public CategoryType(FixedTypes fixed)
	{
		this.fixedType = fixed;
	}

	public CategoryType(Dao<CategoryType, Integer> dao)
	{
		this.setDao(dao);
	}
	

	public static final String CAT_ID_FIELD = "cat_id";
	public static final String FIXED_TYPE_FIELD = "fixed_type";
	public static final String TITLE_FIELD = "title";
	public static final String IS_CURRENT_FIELD = "is_current";
	
	
	
	@DatabaseField(generatedId = true, columnName = CAT_ID_FIELD)
	private int id;
	public int getID()
	{
		return id;
	}

	@DatabaseField(index=true, columnName = FIXED_TYPE_FIELD)
	private FixedTypes fixedType = FixedTypes.None; // default not fixed [user created]

	public FixedTypes getFixedType()
	{
		return fixedType;
	}
	
	@DatabaseField(index = true, columnName = TITLE_FIELD)
	private String title = "Unknown";

	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	@DatabaseField(index = true, columnName = IS_CURRENT_FIELD)
	private Boolean current = false;

	public void setCurrent(Boolean current)
	{
		
		Boolean isSet = (current == this.current);
		try {
			// new current.. refresh and clean old currents.
			if(current == true)
			{
				// clear all other currents
				List<CategoryType> res = this.dao.queryBuilder().where().eq(IS_CURRENT_FIELD, true).and().ne(CAT_ID_FIELD, id).query();
				for(CategoryType c : res)
				{
						Log.i("Locadex", "Fixing a wrong category!!!");
						c.setCurrent(false);
						c.update();
				}
			}

			if (!isSet)
			{
				Log.i("Locadex", "Category "+this.title+" wasn't set, updating.");
				this.current = current;
				this.update();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}

	public Boolean isCurrent()
	{
		if(this.current == null)
		{
			this.current = false;
			try {
				this.update();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.current;
	}
	
	public List<ChecklistType> getChecklists(DatabaseHelper helper)
	{
		try {
			if(id == 0)
			{
				// all categories.
				return helper.getChecklistDao().queryForAll();
			}
			else
			{
				return helper.getChecklistDao().queryForEq(ChecklistType.CAT_ID_FIELD, id);
			}
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for ChecklistType in cur category, sending empty list.");
			return new ArrayList<ChecklistType>();
		}
	}
	public List<MarkerType> getMarkers(DatabaseHelper helper)
	{
		try {
			if(id == 0)
			{
				// all categories.
				return helper.getMarkerDao().queryForAll();
			}
			else
			{
				return helper.getMarkerDao().queryForEq(MarkerType.CAT_ID_FIELD, id);
			}
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for MarkerType in cur category, sending empty list.");
			return new ArrayList<MarkerType>();
		}
	}
	public List<NoteType> getNotes(DatabaseHelper helper)
	{
		try {
			if(id == 0)
			{
				// all categories.
				return helper.getNoteDao().queryForAll();
			}
			else
			{
				return helper.getNoteDao().queryForEq(NoteType.CAT_ID_FIELD, id);
			}
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for NoteType in cur category, sending empty list.");
			return new ArrayList<NoteType>();
		}
	}
	public List<ReminderType> getReminders(DatabaseHelper helper)
	{
		try {
			if(id == 0)
			{
				// all categories.
				return helper.getReminderDao().queryForAll();
			}
			else
			{
				return helper.getReminderDao().queryForEq(ReminderType.CAT_ID_FIELD, id);
			}
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for ReminderType in cur category, sending empty list.");
			return new ArrayList<ReminderType>();
		}
	}
	
}
