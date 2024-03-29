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

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R.layout;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ListView;

public class CategoriesActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
	private DatabaseHelper helper;
	private CategoryType item = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.categories);
    	
    	mListView = (ListView)findViewById(R.id.categoryListView);
        
		try {

			
			// possible option for future refactoring / recreation
			// http://stackoverflow.com/questions/7159816/android-cursor-with-ormlite
			// this will make the lists of lists WAY faster, but its super complicated android code
			// you'd have to recreate all of the adaptors to use it
			
	        // connect to DAO helper, ugly but it works flawlessly.
	        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	        
			// apply to list adapter.
			mListView.setAdapter(new CategoryAdapter(this,
	                R.layout.categories_list_row, helper));
				        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mListView.setTextFilterEnabled(true);
		
		// attach list item click
		//mListView.setOnItemClickListener(mListClickListener);
		mListView.setOnItemLongClickListener(mListLongClickListener);
		
		Button addItem = (Button)findViewById(R.id.addItem);
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
		
		// refresh listview data
		((CategoryAdapter)mListView.getAdapter()).refreshData();
		
        Log.i("Locadex", "Attempting to refresh list onresume.");
	}
	
	
	private View lastView = null;
	private CategoryType currentCat;
    // Handle list clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			Log.i("NoteMainActivity:OnItemClickListener", String.valueOf(position));
    			// last view, hide remove button again
    			if(lastView != null)
    			{
    			
    				lastView.setBackgroundColor(Color.WHITE);
    				Button hider = (Button)lastView.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.GONE);
    			}

    			// current view, make remove button visible
    			if(v != null)
    			{
        			CategoryAdapter clAdapt = (CategoryAdapter) parent.getAdapter();
        			currentCat = clAdapt.getItem(position);
        			if(currentCat.getFixedType() == CategoryType.FixedTypes.None)
        			{
        				v.setBackgroundColor(Color.LTGRAY);
        				Button hider = (Button)v.findViewById(R.id.listRightButtons);
        				hider.setOnClickListener(new OnClickListener() {
    						public void onClick(View v)
    						{

    			    			if(lastView != null)
    			    			{
    			    			
    			    				lastView.setBackgroundColor(Color.WHITE);
    			    				Button hider = (Button)lastView.findViewById(R.id.listRightButtons);
    			    				hider.setVisibility(View.GONE);
    			    			}
    			    			lastView = null;
    							remove(currentCat);
    						}
    					});
    				hider.setVisibility(View.VISIBLE);
    				lastView = v;
        			}
    			}
    		}
    };
    
    // Handle list long press clicks
    OnItemLongClickListener mListLongClickListener = new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id)
    		{
    			Log.i("CategoryActivity:OnItemClickListener", String.valueOf(position));
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
        			CategoryAdapter clAdapt = (CategoryAdapter) parent.getAdapter();
        			currentCat = clAdapt.getItem(position);
        			if(currentCat.getFixedType() == CategoryType.FixedTypes.None)
        			{
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
	    							remove(currentCat);
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
								((CategoryAdapter)mListView.getAdapter()).refreshData();
							}
						});
						edit.setVisibility(View.VISIBLE);
						
	    				lastView = v;
        			}
    			}
        		
        		return true;
    		}
    };
    
    public boolean remove(CategoryType cat)
    {

    	// never delete a fixed category
		if(cat.getFixedType() != CategoryType.FixedTypes.None)
			return false;
		
		
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
    	try {
    		// TODO Step1: Confirm yes/no (notify items will be moved to 'unsorted')
    		
    		// Step2: Go thru all checklists, markers, notes, reminders, move items to Unsorted
    		CategoryType unsorted = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.Unsorted).query().get(0);
    		helper.MoveAllCategoryItems(cat.getID(), unsorted.getID());

    		// Step3: If activecategory == toDelete, Set active category as Unsorted
    		if(helper.getCurrentCategory().getID() == unsorted.getID())
    			unsorted.setCurrent(true);

    		// Step4: Delete
			helper.getCategoryDao().delete(cat);

			// refresh listview data
			((CategoryAdapter)mListView.getAdapter()).refreshData();

    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }
    
    /**
     * Edit the category title
     * @param position
     * @return
     */
    public boolean editTitle(int position)
    {
    	item = (CategoryType)mListView.getItemAtPosition(position);
    	
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		
		int maxLength = 50;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		input.setSingleLine(true);
		// Set the text to the current title and highlight it
		input.setText(item.getTitle());
		input.setSelectAllOnFocus(true);
			
		alertLayout.addView(input);
		
		alert.setView(alertLayout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// edit the title of the category
				String value = input.getText().toString();
				item.setTitle(value);
				
				try
				{
					item.update();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				((CategoryAdapter)mListView.getAdapter()).refreshData();
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
    
    // Create new category
    public boolean addItem(View v){
    	// popup category create window with title
    	// ok or cancel
    	// if okay, change
    	
        LayoutInflater factory = LayoutInflater.from(v.getContext());            
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext()); 

        alert.setTitle("Create Category"); 
        alert.setMessage("Enter your Category title"); 
        // Set an EditText view to get user input  

        final EditText input = new EditText(v.getContext());
		int maxLength = 50;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		input.setSingleLine(true);
        input.setText("");
        alert.setView(input);


        // Create
        alert.create();
        
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int whichButton) { 

        	try {
                CategoryType data = new CategoryType(CategoryType.FixedTypes.None);
                data.setDao(helper.getCategoryDao());
            	data.setTitle(input.getText().toString());
				data.create();
				((CategoryAdapter)mListView.getAdapter()).refreshData();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.e("CategoriesActivity","Update of new category.");
			}
        }
        }); 

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
            // Canceled. 
          } 
        }); 
        
        // Pop it!
        alert.show();


    	return true;
    }
    
    
}
