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
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.ChecklistType;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// XML Based View
// Source: http://www.permadi.com/blog/2010/03/android-sdk-using-custom-view-in-xml-based-layout/
public class GlobalHeaderView extends LinearLayout {

	private View xmlView;
	private Spinner mSpinner;
	private Context viewContext;
	// Constructor from XML files
	public GlobalHeaderView(Context context, AttributeSet attrs) {
	super(context, attrs);
	viewContext=context;

	// Expand global_header xml into content.
		

	LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	xmlView=layoutInflater.inflate(R.layout.global_header,this);

	
    // Generate Categories
    // http://developer.android.com/resources/tutorials/views/hello-spinner.html

	// Dynamic spinner
	// http://stackoverflow.com/questions/5453580/android-dynamic-spinner-update

		// requirement -- all activities that call globalheader must have the helper
		DatabaseHelper h = OpenHelperManager.getHelper(context, DatabaseHelper.class);

		try {

			mSpinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
		    
		    CategoryAdapter adapter = new CategoryAdapter(xmlView.getContext(),
		    		android.R.layout.simple_spinner_item, h);

			// apply to list adapter.
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    mSpinner.setAdapter(adapter);
			mSpinner.setSelection(adapter.getSelectedIndex(), false);
		    // select to current category id
			
			/*
			String[] spinnerArray = new String[]{
					"GPS",
					"Create Category",
					"Settings",
					"About"
			};
			  ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(),   
					  android.R.layout.simple_dropdown_item_1line,spinnerArray);

				Spinner settingsSpinner = (Spinner) xmlView.findViewById(R.id.satelliteIcon);
				settingsSpinner.setAdapter(spinnerArrayAdapter);
			  */
			
			
			ImageButton settingsButton = (ImageButton)findViewById(R.id.settingsButton);
	
			
			final ArrayList<CharSequence> items = new ArrayList<CharSequence>();
        	final LocadexApplication app = (LocadexApplication)context.getApplicationContext();
			
			settingsButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					
					items.clear();
					items.add("Location Reminders: " + (app.useLocation?"ON":"OFF"));
					items.add("Manage Categories");
					//items.add("Settings");
					items.add("About");

					
				    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				    final Context context = v.getContext();
				    final CharSequence[] itemChar = items.toArray(new CharSequence[items.size()]);
				    builder.setItems(itemChar, new DialogInterface.OnClickListener() {

				        public void onClick(DialogInterface dialog, int item) {
				        	Log.i("SettingsButton", "item: " + item);
				            if(item == 0) {
				            	// GPS Enabled
				            	if(app.useLocation)
				            	{
				            		// set true
				            		app.useLocation = false;
				            		app.saveLocation();
				            		Toast.makeText(getContext(), "Locadex GPS Disabled", Toast.LENGTH_SHORT).show();
				            	}
				            	else
				            	{
				            		// set false
				            		app.useLocation = true;
				            		app.saveLocation();
				            		Toast.makeText(getContext(), "Locadex GPS Enabled", Toast.LENGTH_SHORT).show();
				            	}
				            	
				            } else if(item == 1) {
				            	// Manage Categories
				            	Intent aboutPage = new Intent(context, CategoriesActivity.class);
				            	context.startActivity(aboutPage);
				            } /*else if(item == 2) {
				            	// Settings
				            	Intent aboutPage = new Intent(context, SettingsActivity.class);
				            	context.startActivity(aboutPage);
				            }*/
				            else
				            {
				            	// About
				            	Intent aboutPage = new Intent(context, AboutActivity.class);
				            	context.startActivity(aboutPage);
				            }
				        }
				    });

				     AlertDialog dialog = builder.create();
				     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				     WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();

				     int[] location = new int[2];
				    v.getLocationOnScreen(location);
				 WMLP.x = 0;   //x position
				 WMLP.y = -location[1];   //y position

				 dialog.getWindow().setAttributes(WMLP);

				 dialog.show();
					
				}
				
			});
		}
			
		catch(Exception e)
		{
			Log.e("GlobalHeaderView", "Really failed hard querying categories... ");
			Log.e("GlobalHeaderView", e.getMessage());
			e.printStackTrace();
			
		}
		
		mSpinner.setOnItemSelectedListener(mListSelectedListener);
		
		Button home = (Button)findViewById(R.id.homeIcon);  
		home.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
    				if(currentActivity != null)
    				{
    					currentActivity.finish();
    					// Start single top activity for home, allows home from anywhere.
    					Intent i = new Intent(v.getContext(), HomeActivity.class);
    				    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    				    v.getContext().startActivity(i);    					
    				}
                	
                }

        });

		/*home.setOnTouchListener(new OnTouchListener(){

		public boolean onTouch(View v, MotionEvent event) {
				
				
				if(currentActivity != null)
				{
					Toast.makeText(v.getContext(), "THIS IS SUPPOSED TO BE GONE!",  Toast.LENGTH_LONG);
					currentActivity.finishActivity(Activity.RESULT_OK);
					
				}
				else
				{
					Toast.makeText(v.getContext(), "WTF MAN", Toast.LENGTH_LONG);
				}
				
				return true;
			}
			
		});
    */
	}
	
	public interface OnCategoryChangeListener
	{
		public abstract void onCategoryChanged();
	}
			
	private OnCategoryChangeListener catChangedListener = null;
	public void setOnCategoryChange(OnCategoryChangeListener change)
	{
		catChangedListener = change;
	}
	
	
	
	private Activity currentActivity;
	public void setActivity(Activity act)
	{
		currentActivity = act;
	}

    // Handle list clicks
	OnItemSelectedListener mListSelectedListener = new OnItemSelectedListener() {
    		
    		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    		
    			CategoryType o = (CategoryType)mSpinner.getAdapter().getItem(position);
    			o.setCurrent(true);
    			
    			if(catChangedListener != null)
    				catChangedListener.onCategoryChanged();

    		}
    		
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	    	// your code here
    	    }

    };
    
	public void refreshData()
	{
	    Spinner spinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
	    CategoryAdapter adapter = (CategoryAdapter)spinner.getAdapter();

		// reload from database
	    adapter.refreshData();
		
	    // reselect selected
	    spinner.setSelection(adapter.getSelectedIndex(), false);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);	    

	    Spinner spinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
	    CategoryAdapter adapter = (CategoryAdapter)spinner.getAdapter();
	}

	

}