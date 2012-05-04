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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Window;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

public class NoteMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
	NoteType data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.note_main);
        mListView = (ListView)findViewById(R.id.noteView);
		try {

			
			// possible option for future refactoring / recreation
			// http://stackoverflow.com/questions/7159816/android-cursor-with-ormlite
			// this will make the lists of lists WAY faster, but its super complicated android code
			// you'd have to recreate all of the adaptors to use it
			
	        // connect to DAO helper, ugly but it works flawlessly.
	        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	        data = new NoteType(helper);
	        // Query for all into a list
	        List<NoteType> results;

	        // create a new note, pass DAO into it.
	        // NoteType data = new NoteType(helper);
			// data.setTitle("Google loves Play Stores!");

	        // spack says NO --> category fixes are handled in NoteType's constructor. DROP THIS SHIT
			/*
			if(data.getCategory(helper).getFixedType()==CategoryType.FixedTypes.All){
				// if category is 'all', set to unknown
				// "This condition works, but I don't know how to set data's category" -Eric
				// Debugging - remove this line //data.setTitle(data.getCategory(helper).getFixedType().toString());
			}*/
			
			//data.create(); // add to database
			
			
			//the following removes resulting notes from the database
			//helper.getNoteDao().delete(results);
			//results.removeAll(results);
			//results = helper.getNoteDao().queryForAll();

			// apply to list adapter.
			mListView.setAdapter(new NoteAdapter(this,
	                android.R.layout.simple_list_item_1, helper));
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mListView.setTextFilterEnabled(true);
		
		// attach list item click
		mListView.setOnItemClickListener(mListClickListener); 
		mListView.setOnItemLongClickListener(mListLongClickListener); 
		

        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        head.setOnCategoryChange(new OnCategoryChangeListener(){
			public void onCategoryChanged() {
				((NoteAdapter)mListView.getAdapter()).refreshData();
			}
        });
        
		Button addItem = (Button)findViewById(R.id.addItem);
		addItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				addItem(v);
			}
		});
    }
    
    private static final int CREATE_MENU_ID = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, CREATE_MENU_ID, 0, "Create New Note").setShortcut('3', 'c');
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CREATE_MENU_ID:
                //create note
            	addItem(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    


	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		h.setActivity(this);
		if(h != null) h.refreshData();

//        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
//		try {
//			List<NoteType> results = helper.getNoteDao().queryForAll();
//			mListView.setAdapter(new NoteAdapter(this,
//	                android.R.layout.simple_list_item_1, results));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private View lastView = null;
	private NoteType currentNote;
    // Handle list long press clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			Log.i("NoteMainActivity:OnItemClickListener", String.valueOf(position));
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
        			NoteAdapter nada = (NoteAdapter) parent.getAdapter();
        			currentNote = nada.getItem(position);

        			openEditorActivity(currentNote);
    			}
    		}
    };
    // Handle list long press clicks
    OnItemLongClickListener mListLongClickListener = new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id)
    		{
    			Log.i("NoteMainActivity:OnItemClickListener", String.valueOf(position));
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
        			NoteAdapter nada = (NoteAdapter) parent.getAdapter();
        			currentNote = nada.getItem(position);
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
    							remove(currentNote);
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
							((NoteAdapter)mListView.getAdapter()).refreshData();
						}
					});
					edit.setVisibility(View.VISIBLE);
					
    				lastView = v;
    			}
        		
        		return true;
    		}
    };
    
    public boolean remove(NoteType note)
    {
    	try {
    		// delete item
			note.delete();
			// Refresh list
			((NoteAdapter)mListView.getAdapter()).refreshData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }

	
	public void openEditorActivity(NoteType item)
	{
        Intent myIntent = new Intent(this, NoteEditActivity.class);
        myIntent.putExtra("edu.sru.nullstring.noteId", item.getID());
        startActivityForResult(myIntent, 0);
	}
    /**
     * Edit the note's title
     * @param position
     * @return
     */
    public boolean editTitle(int position)
    {
    	final NoteType item = (NoteType)mListView.getItemAtPosition(position);

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final Spinner catSpin = new Spinner(this);
		populateCatSpinner(catSpin);
		
		// Set the max length of a note's title to 40 characters
		int maxLength = 20;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		input.setSingleLine(true);

		
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
				item.setCategoryID(itm.getID());
				try
				{
					item.update();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				((NoteAdapter)mListView.getAdapter()).refreshData();
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
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Create a new Note");
		alert.setMessage("Note title:");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final Spinner catSpin = new Spinner(this);
		populateCatSpinner(catSpin);
		
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
					// set the title of the note
					String value = input.getText().toString();
					data.setTitle(value);
					
					CategoryAdapter catAdapt = (CategoryAdapter)catSpin.getAdapter();
					CategoryType itm = catAdapt.getItem(catSpin.getSelectedItemPosition());
					data.setCategoryID(itm.getID());
					try
					{
						data.create(); // add to database
						((NoteAdapter)mListView.getAdapter()).refreshData();
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
    	
//    public boolean addItem(View v){
//        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
//        NoteType data = new NoteType(helper);
//		data.setTitle("New Note!");
//		
//		try {
//			data.create(); // add to database
//			// Refresh list
//			((NoteAdapter)mListView.getAdapter()).refreshData();
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//	    	return false;
//		}
//
//        openEditorActivity(data);
//    	return true;
//    }
	
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
		 			h, CategoryAdapter.SubCategoryType.Note, data);

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
