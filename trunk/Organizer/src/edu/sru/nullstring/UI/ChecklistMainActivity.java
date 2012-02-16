package edu.sru.nullstring.UI;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChecklistMainActivity extends ListActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, PENS));
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.checklist_main, menu);
	    return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.text:     Toast.makeText(this, "You pressed the text!", Toast.LENGTH_LONG).show();
	                            break;
	    }
	    return true;
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Object o = this.getListAdapter().getItem(position);
		String pen = o.toString();
		Toast.makeText(this, "You have chosen: " + " " + pen, Toast.LENGTH_LONG).show();
		}
}
