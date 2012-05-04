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

package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

public class ChecklistMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
	private DatabaseHelper helper = null;
	private ChecklistType item = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.checklist_main);
    	
    	mListView = (ListView)findViewById(R.id.checklistView);
        
		try {

	        // connect to DAO helper, ugly but it works flawlessly.
	        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	        
			// apply to list adapter.
			mListView.setAdapter(new ChecklistAdapter(this,
	                android.R.layout.simple_list_item_1, helper));
				        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mListView.setTextFilterEnabled(true);
		
		mListView.setOnItemClickListener(mListClickListener);
		// attach list item click
		mListView.setOnItemLongClickListener(mListLongClickListener);

		 helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        head.setOnCategoryChange(new OnCategoryChangeListener(){
			public void onCategoryChanged() {
				((ChecklistAdapter)mListView.getAdapter()).refreshData();
			}
        });
		
        ImageButton addItem = (ImageButton)findViewById(R.id.addItem);
		addItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				addItem(v);
			}
		});
    }
    
	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.refreshData();
		
		// refresh listview		
		((ChecklistAdapter)mListView.getAdapter()).refreshData();
	}
	
	OnItemClickListener mListClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				
				Log.i("CMainActivity:OnItemClickListener", String.valueOf(position));
				// last view, hide remove button again
				if(lastView != null)
				{
				
					lastView.setBackgroundColor(Color.WHITE);
					ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
					hider.setVisibility(View.GONE);
					
					ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
					edit.setVisibility(View.GONE);
				}
	
				// current view, open it
				if(v != null)
				{
        			ChecklistAdapter clAdapt = (ChecklistAdapter) parent.getAdapter();
        			currentChecklist = clAdapt.getItem(position);
        			
					ChecklistItemActivity item = new ChecklistItemActivity();
			        Intent intent = new Intent(v.getContext(), item.getClass());
			        intent.putExtra("CHECKLIST_ID", currentChecklist.getID());
			        intent.putExtra("CHECKLIST_TITLE", currentChecklist.getTitle());
			        startActivityForResult(intent, 0);
				}
			}
	};
	private View lastView = null;
	private ChecklistType currentChecklist;
    // Handle list long press clicks
    OnItemLongClickListener mListLongClickListener = new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id)
    		{
    			Log.i("ChecklistMainActivity:OnItemClickListener", String.valueOf(position));
    			// last view, hide remove button again
    			if(lastView != null)
    			{
    				lastView.setBackgroundColor(Color.WHITE);
    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.GONE);
    				
					ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
					edit.setVisibility(View.GONE);
    			}

    			// current view, make remove button visible
    			if(v != null)
    			{
        			ChecklistAdapter clAdapt = (ChecklistAdapter) parent.getAdapter();
        			currentChecklist = clAdapt.getItem(position);
    				v.setBackgroundColor(Color.LTGRAY);
    				
    				ImageButton hider = (ImageButton)v.findViewById(R.id.listRightButtons);
    				hider.setOnClickListener(new OnClickListener() {
    						public void onClick(View v)
    						{
    			    			if(lastView != null)
    			    			{
    			    				lastView.setBackgroundColor(Color.WHITE);
    			    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    			    				hider.setVisibility(View.GONE);
    			    				
    			    				ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
    			    				edit.setVisibility(View.GONE);
    			    			};
    							remove(currentChecklist);
    						}
    					});
    				hider.setVisibility(View.VISIBLE);
    				
					ImageButton edit = (ImageButton)v.findViewById(R.id.editBtn);
    				edit.setOnClickListener(new OnClickListener() {
						public void onClick(View v)
						{
			    			if(lastView != null)
			    			{
			    				lastView.setBackgroundColor(Color.WHITE);
			    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
			    				hider.setVisibility(View.GONE);
			    				
			    				ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
			    				edit.setVisibility(View.GONE);
			    			};
							editTitle(position);
							((ChecklistAdapter)mListView.getAdapter()).refreshData();
						}
					});
					edit.setVisibility(View.VISIBLE);
					
    				lastView = v;
    			}
        		
        		return true;
    		}
    };
    
    public boolean remove(ChecklistType checklist)
    {
    	List<ChecklistItemType> items = null;
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
    	try
    	{
    		// need to delete all of the checklist's items
    		items = helper.getChecklistItemDao().queryForEq(ChecklistItemType.LIST_ID_FIELD, checklist.getID());
    		
    		Iterator<ChecklistItemType> iterator = items.iterator();
    		while (iterator.hasNext())
    		{
    			iterator.next().delete();
    		}
    		
			helper.getChecklistDao().delete(checklist);
			((ChecklistAdapter)mListView.getAdapter()).refreshData();
		}
    	catch (SQLException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }
    /**
     * Edit the checklist's title and category
     * @param position
     * @return
     */
    public boolean editTitle(int position)
    {
    	item = (ChecklistType)mListView.getItemAtPosition(position);
    	
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final Spinner catSpin = new Spinner(this);
		populateCatSpinner(catSpin);
		
		// Set the max length of a checklist to 19 characters
		int maxLength = 20;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setSingleLine(true);
		input.setFilters(FilterArr);
		
		// Set the text to the current title and highlight it
		input.setText(item.getTitle());
		input.setSelectAllOnFocus(true);
		
		catSpin.setSelection(((CategoryAdapter)catSpin.getAdapter()).getSelectedIndex());
		
		alertLayout.addView(input);
		alertLayout.addView(catSpin);
		
		
		
		alert.setView(alertLayout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// edit the title of the checklist
				String value = input.getText().toString();
				item.setTitle(value);
				
				CategoryAdapter catAdapt = (CategoryAdapter)catSpin.getAdapter();
				CategoryType itm = catAdapt.getItem(catSpin.getSelectedItemPosition());
				item.setCategory(itm.getID());
				try
				{
					item.update();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				((ChecklistAdapter)mListView.getAdapter()).refreshData();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
    	
    	return true;
    }
    
    public boolean addItem(View v)
    {
    	item = new ChecklistType(helper);
    	
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Create a Checklist");
		alert.setMessage("Checklist Title:");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final Spinner catSpin = new Spinner(this);
		populateCatSpinner(catSpin);
		// Set the max length of a checklist to 19 characters
		int maxLength = 20;
		input.setSingleLine(true);
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		
		alertLayout.addView(input);
		alertLayout.addView(catSpin);
		
		alert.setView(alertLayout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			boolean allowCreate = true;
			public void onClick(DialogInterface dialog, int whichButton)
			{
				if (allowCreate == true)
				{
					allowCreate = false;
					// set the id to the checklist
					String value = input.getText().toString();
					item.setTitle(value);
					
					CategoryAdapter catAdapt = (CategoryAdapter)catSpin.getAdapter();
					CategoryType itm = catAdapt.getItem(catSpin.getSelectedItemPosition());
					item.setCategory(itm.getID());
					try
					{
						item.create(); // add to database
						((ChecklistAdapter)mListView.getAdapter()).refreshData();
					} catch (SQLException e)
					{
						e.printStackTrace();	
					}
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
    	
    	return true;
    }
    
	/**
	 * Populates the category spinner with the current categories
	 */
	private void populateCatSpinner(Spinner mSpinner)
	{
		// requirement -- all activities that call globalheader must have the helper
		DatabaseHelper h = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		
		try {
			Dao<CategoryType, Integer> dao = h.getCategoryDao();
		    
		 	CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, 
		 			h, CategoryAdapter.SubCategoryType.Checklist, item);

			// apply to list adapter.
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    mSpinner.setAdapter(adapter);
			mSpinner.setSelection(adapter.getSelectedIndex(), false);
		    // select to current category id
			 
		} catch (SQLException e) {
			Log.e("Locadex", "Failed loading data into GlobalHeaderView. ");
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		catch(Exception e)
		{
			Log.e("Locadex", "Really failed hard querying categories... ");
			Log.e("Locadex", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
