package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ListView;

public class NoteMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
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
    private static final int FONTSIZE_MENU_ID = Menu.FIRST + 1;
    private static final int TEST_MENU_ID = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, CREATE_MENU_ID, 0, "Create").setShortcut('3', 'c');
        menu.add(0, FONTSIZE_MENU_ID, 0, "Font Size").setShortcut('4', 's');
        menu.add(0, TEST_MENU_ID, 0, "Test").setShortcut('5', 'z');
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
                return true;
            case FONTSIZE_MENU_ID:
                //adjust font size
                return true;
            case TEST_MENU_ID:
                //Testing menu items
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
    				Button hider = (Button)lastView.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.GONE);
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
    		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
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
        			NoteAdapter nada = (NoteAdapter) parent.getAdapter();
        			currentNote = nada.getItem(position);
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
    			    			};
    							remove(currentNote);
    						}
    					});
    				hider.setVisibility(View.VISIBLE);
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
	
    public boolean addItem(View v){
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
        NoteType data = new NoteType(helper);
		data.setTitle("New Note!");
		
		try {
			data.create(); // add to database
			// Refresh list
			((NoteAdapter)mListView.getAdapter()).refreshData();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}

        openEditorActivity(data);
    	return true;
    }
}
