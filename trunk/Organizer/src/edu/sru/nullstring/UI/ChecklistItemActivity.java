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
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.R;
//import edu.sru.nullstring.Data.CategoryAdapter;
//import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.ChecklistAdapter;
import edu.sru.nullstring.Data.ChecklistItemAdapter;
import edu.sru.nullstring.Data.ChecklistItemType;
import edu.sru.nullstring.Data.ChecklistType;
//import edu.sru.nullstring.Data.ChecklistType;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;

public class ChecklistItemActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
	private int checklistID;
	private String checklistTitle;
	private DatabaseHelper helper;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.checklist_item);
    	
    	mListView = (ListView)findViewById(R.id.checklistView);
    	
    	Bundle checklist = getIntent().getExtras();
    	if(checklist != null)
    	{
    		checklistID = checklist.getInt("CHECKLIST_ID");
    		checklistTitle = checklist.getString("CHECKLIST_TITLE");
    	}
    	
    	// change the title to the current checklist
    	TextView title = (TextView)this.findViewById(R.id.text1);
    	title.setText(checklistTitle);
    	
		try {

	        // connect to DAO helper, ugly but it works flawlessly.
	        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	        
			// apply to listItem adapter.			
			mListView.setAdapter(new ChecklistItemAdapter(this,
					android.R.layout.simple_list_item_1, helper, checklistID));

				        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mListView.setTextFilterEnabled(true);
		// attach onclick
		mListView.setOnItemClickListener(mListClickListener);
		
		// attach onlongclick
		mListView.setOnItemLongClickListener(mListLongClickListener);
		
        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        head.setOnCategoryChange(new OnCategoryChangeListener(){
			public void onCategoryChanged() {
				((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
			}
        });
		
		ImageButton addItem = (ImageButton)findViewById(R.id.addItem);
		addItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				addItem(v);
				((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
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
		((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
	}

	OnItemClickListener mListClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				Log.i("CMainActivity:OnItemClickListener", String.valueOf(position));
				
				// last view, hide remove button and show checkbox again
				if(lastView != null)
				{
					lastView.setBackgroundColor(Color.WHITE);
    				// show checkbox
    				CheckBox chkBox = (CheckBox)lastView.findViewById(R.id.chkbox);
    				chkBox.setVisibility(View.VISIBLE);
    				// hide delete button
					ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
					hider.setVisibility(View.GONE);
					// hide edit button
					ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
					edit.setVisibility(View.GONE);
				}
				
				CheckBox chkBox = (CheckBox)v.findViewById(R.id.chkbox);
				ChecklistItemType item = (ChecklistItemType)mListView.getItemAtPosition(position);
				
				if (chkBox.isChecked())
				{
					chkBox.setChecked(false);
					item.setChecked(false);
				}
				else
				{
					chkBox.setChecked(true);
					item.setChecked(true);
				}
			}
	};
	
	private View lastView = null;
	private ChecklistItemType currentChecklistItem;
	
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
        			ChecklistItemAdapter clAdapt = (ChecklistItemAdapter) parent.getAdapter();
        			currentChecklistItem = clAdapt.getItem(position);
    				v.setBackgroundColor(Color.LTGRAY);
    				
    				ImageButton hider = (ImageButton)v.findViewById(R.id.listRightButtons);
    				hider.setOnClickListener(new OnClickListener() {
    						public void onClick(View v)
    						{
    			    			if(lastView != null)
    			    			{
    			    				lastView.setBackgroundColor(Color.WHITE);
    			    				// show checkbox
    			    				CheckBox chkBox = (CheckBox)lastView.findViewById(R.id.chkbox);
    			    				chkBox.setVisibility(View.VISIBLE);
    			    				// hide delete button
    			    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    			    				hider.setVisibility(View.GONE);
    			    				
    			    				ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
    			    				edit.setVisibility(View.GONE);
    			    			};
    							remove(currentChecklistItem);
    						}
    					});
    				// hide checkbox
    				CheckBox chkBox = (CheckBox)v.findViewById(R.id.chkbox);
    				chkBox.setVisibility(View.GONE);
    				// show delete button
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
			    				
			    				CheckBox chkBox = (CheckBox)lastView.findViewById(R.id.chkbox);
			    				chkBox.setVisibility(View.VISIBLE);
			    			};
							editTitle(position);
							((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
						}
					});
					edit.setVisibility(View.VISIBLE);
					
    				lastView = v;
    			}
        		
        		return true;
    		}
    };

    public boolean remove(ChecklistItemType checklist)
    {
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
    	try {
			helper.getChecklistItemDao().delete(checklist);
			((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }
    
    /**
     * Edit the checklist item's title
     * @param position
     * @return
     */
    public boolean editTitle(int position)
    {
    	final ChecklistItemType item = (ChecklistItemType)mListView.getItemAtPosition(position);
    	
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		
		// Set the max length of a checklist to 19 characters
		int maxLength = 20;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		
		input.setText(item.getText());
		input.setSelectAllOnFocus(true);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// edit the title of the checklist
				String value = input.getText().toString();
				item.setText(value);
				((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
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
    
    public boolean addItem(View v){
		DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
		final ChecklistItemType item = new ChecklistItemType(helper);

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Checklist");
		alert.setMessage("Add item:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		
		// Set the max length of a checklist to 19 characters
		int maxLength = 20;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// set the id to the checklist
				item.setListID(checklistID);
				String value = input.getText().toString();
				item.setText(value);
				item.setChecked(false);
				try
				{
					item.create(); // add to database
					((ChecklistItemAdapter)mListView.getAdapter()).refreshData();
				} catch (SQLException e)
				{
					e.printStackTrace();	
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
}
