package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.app.Application;
import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ListView;

public class ChecklistMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.checklist_main);
    	

        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);

    	
    	mListView = (ListView)findViewById(R.id.checklistView);
        
		try {

			
			// possible option for future refactoring / recreation
			// http://stackoverflow.com/questions/7159816/android-cursor-with-ormlite
			// this will make the lists of lists WAY faster, but its super complicated android code
			// you'd have to recreate all of the adaptors to use it
			
	        // connect to DAO helper, ugly but it works flawlessly.
	        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	        
	        // Query for all into a list
	        List<ChecklistType> results;
			 	
			// create a new checklist, pass DAO into it.

	        
	        ChecklistType data = new ChecklistType(helper);
			data.setTitle("Hamer loves Androids!");
			// if category is 'all', set to unknown
			//data.create(); // add to database
			
	        
			// pull all checklists from database, no category
			results = helper.getChecklistDao().queryForAll();

			// apply to list adapter.
			mListView.setAdapter(new ChecklistAdapter(this,
	                android.R.layout.simple_list_item_1, results));
				        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mListView.setTextFilterEnabled(true);
		
		// attach list item click
		mListView.setOnItemClickListener(mListClickListener);        
    }
    

	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.RefreshData();
	}
	
	
	private View lastView = null;
    // Handle list clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			
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

    				v.setBackgroundColor(Color.LTGRAY);
    				Button hider = (Button)v.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.VISIBLE);
    				lastView = v;
    			}
    			//ChecklistType o = (ChecklistType)mListView.getAdapter().getItem(position);
    			//Toast.makeText(parent.getContext(), "You have chosen list: " + o.getTitle(), Toast.LENGTH_LONG).show();
    		}
    };
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.checklist_menu, menu);
        return true;        
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text:     Toast.makeText(this, "You pressed Jeb!", Toast.LENGTH_LONG).show();
                                break;
        }
        return true;
    }
    
    
}
