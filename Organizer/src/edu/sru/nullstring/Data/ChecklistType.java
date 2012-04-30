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
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

import edu.sru.nullstring.Data.CategoryType.FixedTypes;

@DatabaseTable(tableName = "checklists")
public class ChecklistType extends BaseDaoEnabled<ChecklistType,Integer> {

	public static final String LIST_ID_FIELD = "list_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";
	
	@DatabaseField(generatedId = true, columnName = LIST_ID_FIELD )
	private int id;
	
	@DatabaseField(index = true, columnName = CAT_ID_FIELD)
	private int catid = 1;
	
	@DatabaseField(columnName = TITLE_FIELD)
	private String title = "Unlabeled";
	
	public ChecklistType(DatabaseHelper helper)
	{
		CategoryType curCat = helper.getCurrentCategory();
		try {
			this.setDao(helper.getChecklistDao());
			if(curCat.getFixedType() == FixedTypes.All)
			{
				// query unsorted category
				PreparedQuery<CategoryType> unsortedCat = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, FixedTypes.Unsorted).prepare();
				curCat = helper.getCategoryDao().queryForFirst(unsortedCat);
			}
		} catch (SQLException e) {
			Log.e("Locadex", "Database helper failed when creating Checklist type.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.catid = curCat.getID();
		
	}
	
	ChecklistType()
	{
		// ormlite
	}
	
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
		try {
			this.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getTitle()
	{
		return this.title;
	}

	public int getCategoryID()
	{
		return this.catid;
	}
	

	public void setCategory(int newId)
	{
		this.catid = newId;
	}
	
	public void setCategoryID(int newCatID)
	{
		this.catid = newCatID;
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

	public List<ChecklistItemType> getItems(DatabaseHelper h)
	{
		try {
			return h.getChecklistItemDao().queryForEq(ChecklistItemType.LIST_ID_FIELD, id);
		} catch (SQLException e) {
			Log.i("","SQL Exception querying for Attachments for Note, sending empty list.");
			return new ArrayList<ChecklistItemType>();
		}
	}

	
	public CategoryType getCategory(DatabaseHelper h)
	{
		CategoryType result = null;
		try {
			List<CategoryType> allres;
			allres = h.getCategoryDao().queryForEq(CategoryType.CAT_ID_FIELD, this.catid);

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
