package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.app.Application;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import android.widget.ListView;

public class ChecklistMainActivity extends ListActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

    	
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.global_header, lv, false);
        lv.addHeaderView(header, null, false);

        
		try {

	        // connect to DAO helper, ugly but it works flawlessly.
	        DatabaseHelper helper = ((LocadexApplication)this.getApplication()).getDatabaseHelper();
	        
	        // Query for all into a list
	        List<ChecklistType> results;
			 	
			// create a new checklist, pass DAO into it.

	        
	        ChecklistType data = new ChecklistType(helper.getChecklistDao());
			data.setTitle("Hamer loves Androids!");
			data.create(); // add to database
			
	        
			// pull all checklists from database, no category
			results = helper.getChecklistDao().queryForAll();

			// apply to list adapter.
			setListAdapter(new ChecklistAdapter(this,
	                android.R.layout.simple_list_item_1, results));
	        	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        getListView().setTextFilterEnabled(true);
        
    }
    
	static final String[] PENS = new String[]{
		"Jeb",
		"32",
		"is",
		"playing",
		"with",
		"his",
		"awesome",
		"Phablet"
		};
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ChecklistType o = (ChecklistType)this.getListAdapter().getItem(position);
		Toast.makeText(this, "You have chosen list: " + o.getTitle(), Toast.LENGTH_LONG).show();
		}
    
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
