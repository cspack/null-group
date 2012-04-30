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
import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MarkerAdapter;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;

public class MarkerMainActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	
        setContentView(R.layout.marker_main);
        


        
        
        // Attach event to add button

		ImageButton addItem = (ImageButton)findViewById(R.id.addItem);
		addItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				addItem(v);
			}
		});
        
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, MarkerListActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("list").setIndicator("List",
                          res.getDrawable(R.drawable.checklist_32))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, MarkerMapActivity.class);
        spec = tabHost.newTabSpec("map").setIndicator("Map",
                          res.getDrawable(R.drawable.map_32))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    	
        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        head.setOnCategoryChange(new OnCategoryChangeListener(){

			public void onCategoryChanged() {
				tryRefreshList();
			}
        	
        });
        
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
	
	public void openEditorActivity(MarkerType item)
	{
        Intent myIntent = new Intent(this, MarkerEditActivity.class);
        myIntent.putExtra("edu.sru.nullstring.markerId", item.getID());
        startActivityForResult(myIntent, 0);
	}
	
	public void tryRefreshList()
	{
		try
		{
		// Refresh
		String tabTag = getTabHost().getCurrentTabTag(); 
		Activity activity = getLocalActivityManager().getActivity(tabTag); 

		if(activity instanceof MarkerListActivity)
		{
		
			MarkerAdapter m = (MarkerAdapter)((MarkerListActivity)activity).getListView().getAdapter();
			if(m != null) m.refreshData();
		}
		

		if(activity instanceof MarkerMapActivity)
		{
			((MarkerMapActivity)activity).refreshMarkers();
		}
		}catch(Exception ex)
		{
			// This method will fail until everythings loaded, but it's ok we don't need it immediately.
		}

	}
	

    public boolean addItem(View v){
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
        MarkerType data = new MarkerType(helper);
		data.setTitle("New marker");
		
		try {
			data.create(); // add to database

			tryRefreshList();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}

		openEditorActivity(data);
    	return true;
    }
    
    

    public boolean remove(MarkerType marker)
    {
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
    	try {
    		marker.delete();
    		
    		
    		/*
			List<NoteType> results = helper.getNoteDao().queryForAll();
			mListView.setAdapter(new MarkerAdapter(this,
	                android.R.layout.simple_list_item_1, results));
	                */
    		
    		// Refresh
    		String tabTag = getTabHost().getCurrentTabTag(); 
    		Activity activity = getLocalActivityManager().getActivity(tabTag); 

    		if(activity instanceof MarkerListActivity)
    		{
    		
    			MarkerAdapter m = (MarkerAdapter)((MarkerListActivity)activity).getListView().getAdapter();
    			if(m != null) m.refreshData();
    		}
    		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }
}
