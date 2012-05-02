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
import java.util.Calendar;
import java.util.Date;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.Data.ReminderAdapter;
import edu.sru.nullstring.Data.ReminderType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ReminderMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mReminderView;
	private DatabaseHelper helper;
	private ReminderType item;

	
	private Handler refresher = new Handler();
	private Runnable refresherRunnable;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	
        setContentView(R.layout.reminder_main);
        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        head.setOnCategoryChange(new OnCategoryChangeListener(){
			public void onCategoryChanged() {
				((ReminderAdapter)mReminderView.getAdapter()).refreshData();
			}
        });
        
        mReminderView = (ListView)findViewById(R.id.reminderView);

        // DB code
        try {
        	DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
			mReminderView.setAdapter(new ReminderAdapter(this,
	                R.layout.reminder_list_row, helper));
        } catch(Exception ex)
        {
        	Log.e("ReminderMainActivity", "Some database stuff failed =/");
        }
        
		mReminderView.setTextFilterEnabled(true);
		
		// attach list item click
		mReminderView.setOnItemClickListener(mListClickListener); 
		mReminderView.setOnItemLongClickListener(mListLongClickListener); 

        
        
    	Button addItem = (Button)findViewById(R.id.addItem);
    	addItem.setOnClickListener(new OnClickListener() {
    		public void onClick(View v)
    		{
    	        Intent myIntent = new Intent(v.getContext(), ReminderEditActivity.class);
    	        startActivityForResult(myIntent, 0); 
    		}
    	});
    	
    	
    	// Setup UI Refresher

		refresherRunnable = new Runnable()
		{
			private Calendar cal = Calendar.getInstance();
			public void run()
			{
				// refresh
				onResume();
				
				final long startTime = System.currentTimeMillis();
				cal.setTime(new Date(startTime));
				cal.add(Calendar.MINUTE, 1);
				cal.set(Calendar.SECOND, 0);
				refresher.postDelayed(this, cal.getTime().getTime() - startTime);
			}
		};
		
		
		refresher.post(refresherRunnable);
    }
    


	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.refreshData();
		
		// refresh reminder list
		ReminderAdapter r = (ReminderAdapter)mReminderView.getAdapter();
		r.refreshData();
		
	}
	
	private View lastView = null;
	private ReminderType currentReminder;
    // Handle list long press clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			Log.i("ReminderMainActivity:OnItemClickListener", String.valueOf(position));
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
        			ReminderAdapter nada = (ReminderAdapter) parent.getAdapter();
        			currentReminder = nada.getItem(position);

        			openEditorActivity(currentReminder);
    			}
    		}
    };
    // Handle list long press clicks
    OnItemLongClickListener mListLongClickListener = new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id)
    		{
    			Log.i("ReminderMainActivity:OnItemClickListener", String.valueOf(position));
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
        			ReminderAdapter rada = (ReminderAdapter) parent.getAdapter();
        			currentReminder = rada.getItem(position);
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
    							remove(currentReminder);
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
							((ReminderAdapter)mReminderView.getAdapter()).refreshData();
						}
					});
					edit.setVisibility(View.VISIBLE);
					
    				lastView = v;
    			}
        		
        		return true;
    		}
    };

	public void openEditorActivity(ReminderType item)
	{
        Intent myIntent = new Intent(this, ReminderEditActivity.class);
        myIntent.putExtra("edu.sru.nullstring.reminderId", item.getID());
        startActivityForResult(myIntent, 0);
	}

    /**
     * Edit the note's title
     * @param position
     * @return
     */
    public boolean editTitle(int position)
    {
    	item = (ReminderType)mReminderView.getItemAtPosition(position);

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final Spinner catSpin = new Spinner(this);
		populateCatSpinner(catSpin);
		
		// Set the max length of a Reminder's title to 19 characters
		int maxLength = 20;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		
		// Set the text to the current title and highlight it
		input.setText(item.getTitle());
		input.setSelectAllOnFocus(true);		
		input.setSingleLine(true);

		
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
				((ReminderAdapter)mReminderView.getAdapter()).refreshData();
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
	
    public boolean remove(ReminderType item)
    {
    	try {
    		// delete item
			item.delete();
			// Refresh list
			((ReminderAdapter)mReminderView.getAdapter()).refreshData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
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

			//mSpinner = (Spinner)findViewById(R.id.categorySpinner);
			
			// this is requiring adapter
		    //CategoryAdapter adapter = new CategoryAdapter(this,
		    //		android.R.layout.simple_spinner_item, h);
		    
		 	CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, 
		 			h, CategoryAdapter.SubCategoryType.Reminder, item);

			// apply to list adapter.
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    mSpinner.setAdapter(adapter);
			mSpinner.setSelection(adapter.getSelectedIndex(), false);
		    // select to current category id
			 
		} catch (SQLException e) {
			Log.e("Locadex", "Failed loading data into GlobalHeaderView. ");
			// TODO Auto-generated catch block
			// e.printStackTrace();
			
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
