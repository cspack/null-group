package edu.sru.nullstring.UI;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class MarkerMainActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	
        setContentView(R.layout.marker_main);
        
        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        
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
    	
    }
}
