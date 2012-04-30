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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.NoteType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerEditActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {

	public static final String LOG_TAG = "MarkerEditActivity";

	public DatabaseHelper helper = null;
	public MarkerType editItem = null;
	
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.marker_edit);
       //setContentView(new MyView(this));
       

		
	   	// Here is where you refresh the UI for things that may have changed:
	   	GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
	   	if(h != null) h.setActivity(this);
       
       Bundle extras = getIntent().getExtras();        
       if(extras != null)
       {
	       // Setup database helper and object early on
	       int wantedId = extras.getInt("edu.sru.nullstring.markerId");
	       if(wantedId != 0)
	       {
	    	   
	    	   Log.i(LOG_TAG,"Wanted id is : " + wantedId);
	    	   
	       helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	       try {
			editItem = helper.getMarkerDao().queryForId(wantedId);
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Failed to open item for editing");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	       
	       if(editItem != null)
	       {
	    	   if(editItem.getMarkerState() == MarkerType.MarkerState.Unset)
	    	   {
	    		   // pop notification
	    		   Context context = getApplicationContext();
	    		   CharSequence text = getResources().getString(R.string.marker_noPoint);
	    		   int duration = Toast.LENGTH_LONG;
	    		   Toast toast = Toast.makeText(context, text, duration);
	    		   toast.show();

	    	   }
	    	   
	       }
	       

	       ImageButton editButton = (ImageButton) this.findViewById(R.id.addItem);
	       final Drawable drawableTop = getResources().getDrawable(android.R.drawable.ic_menu_edit);
	       editButton.setImageDrawable(drawableTop);
	       editButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Log.e(LOG_TAG,"Creating ...");
				// TODO Auto-sgenerated method stub
				// http://stackoverflow.com/questions/3426917/how-to-add-two-edit-text-fields-in-an-alert-dialog
		        LayoutInflater factory = LayoutInflater.from(v.getContext());            

		        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext()); 

		        alert.setTitle("Rename Marker"); 
		        alert.setMessage("Enter your new title"); 
		        // Set an EditText view to get user input  

		        final EditText input = new EditText(v.getContext());
		        input.setText(editItem.getTitle());
                alert.setView(input);


		        // Create
		        alert.create();
		        
		        alert.setPositiveButton("Rename", new DialogInterface.OnClickListener() { 
		        public void onClick(DialogInterface dialog, int whichButton) { 
		        	editItem.setTitle(input.getText().toString());
		        	try {
						editItem.update();
						refreshData();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						Log.e("MarkerEditActivity","Update of editItem failed.");
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

				
			}
	    	   
	       });

	       
	       refreshData();
	       
	       }
	       else
	       {
	    	   Log.i(LOG_TAG,"Wanted id unknown.");
	       }
       }
	   else
	   {
	
		   Log.i(LOG_TAG,"Bundle is null");
	   }
       
       

       Resources res = getResources(); // Resource object to get Drawables
       TabHost tabHost = getTabHost();  // The activity TabHost
       TabHost.TabSpec spec;  // Resusable TabSpec for each tab
       Intent intent;  // Reusable Intent for each tab


       // Do the same for the other tabs
       intent = new Intent().setClass(this, MarkerEditMapActivity.class);
       spec = tabHost.newTabSpec("map").setIndicator("Map",
                         res.getDrawable(R.drawable.map_32))
                     .setContent(intent);
       tabHost.addTab(spec);
       
       // Create an Intent to launch an Activity for the tab (to be reused)
       intent = new Intent().setClass(this, MarkerEditInfoActivity.class);
       // Initialize a TabSpec for each tab and add it to the TabHost
       spec = tabHost.newTabSpec("info").setIndicator("Info",
                         res.getDrawable(R.drawable.checklist_32))
                     .setContent(intent);
       tabHost.addTab(spec);

       tabHost.setCurrentTab(0);

   }
   
   
   // Refresh editItem fields
   public void refreshData()
   {
	   if(editItem == null)
		   return;
	   
       // Edit UI of MarkerHeader
       TextView noteName = (TextView) this.findViewById(R.id.text1);
       noteName.setText(editItem.getTitle());
       noteName.setTextSize(18.0f);
   }
   

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	
		
	// Here is where you refresh the UI for things that may have changed:
	GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
	h.setActivity(this);
	if(h != null) h.refreshData();
}
   
   
}