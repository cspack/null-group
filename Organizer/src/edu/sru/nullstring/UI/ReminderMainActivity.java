package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.UI.GlobalHeaderView.OnCategoryChangeListener;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.Data.ReminderAdapter;
import edu.sru.nullstring.Data.ReminderType;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ReminderMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mReminderView;
	private DatabaseHelper helper;

	
	private Handler refresher = new Handler();
	private Runnable refresherRunnable;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	
        setContentView(R.layout.reminder_main);
        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        head.setOnCategoryChange(new OnCategoryChangeListener(){
			public void onCategoryChanged() {
				((ReminderAdapter)mReminderView.getAdapter()).refreshData();
			}
        });
        
        mReminderView = (ListView)findViewById(R.id.reminderView);

        // DB code
        try {
        	DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
			mReminderView.setAdapter(new ReminderAdapter(this,
	                R.layout.reminder_list_row, helper));
        } catch(Exception ex)
        {
        	Log.e("ReminderMainActivity", "Some database stuff failed =/");
        }
        
		mReminderView.setTextFilterEnabled(true);
		
		// attach list item click
		mReminderView.setOnItemClickListener(mListClickListener); 
		mReminderView.setOnItemLongClickListener(mListLongClickListener); 

        
        
    	Button addItem = (Button)findViewById(R.id.addItem);
    	addItem.setOnClickListener(new OnClickListener() {
    		public void onClick(View v)
    		{
    	        Intent myIntent = new Intent(v.getContext(), ReminderEditActivity.class);
    	        startActivityForResult(myIntent, 0); 
    		}
    	});
    	
    	
    	// Setup UI Refresher

		refresherRunnable = new Runnable()
		{
			private Calendar cal = Calendar.getInstance();
			public void run()
			{
				// refresh
				onResume();
				
				final long startTime = System.currentTimeMillis();
				cal.setTime(new Date(startTime));
				cal.add(Calendar.MINUTE, 1);
				cal.set(Calendar.SECOND, 0);
				refresher.postDelayed(this, cal.getTime().getTime() - startTime);
			}
		};
		
		
		refresher.post(refresherRunnable);
    }
    


	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.refreshData();
		
		// refresh reminder list
		ReminderAdapter r = (ReminderAdapter)mReminderView.getAdapter();
		r.refreshData();
		
	}
	
	private View lastView = null;
	private ReminderType currentReminder;
    // Handle list long press clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			Log.i("ReminderMainActivity:OnItemClickListener", String.valueOf(position));
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
        			ReminderAdapter nada = (ReminderAdapter) parent.getAdapter();
        			currentReminder = nada.getItem(position);

        			openEditorActivity(currentReminder);
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
        			ReminderAdapter nada = (ReminderAdapter) parent.getAdapter();
        			currentReminder = nada.getItem(position);
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
    							remove(currentReminder);
    						}
    					});
    				hider.setVisibility(View.VISIBLE);
    				lastView = v;
    			}
        		
        		return true;
    		}
    };

	public void openEditorActivity(ReminderType item)
	{
        Intent myIntent = new Intent(this, ReminderEditActivity.class);
        myIntent.putExtra("edu.sru.nullstring.reminderId", item.getID());
        startActivityForResult(myIntent, 0);
	}

	
    public boolean remove(ReminderType note)
    {
    	try {
    		// delete item
			note.delete();
			// Refresh list
			((ReminderAdapter)mReminderView.getAdapter()).refreshData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }

}
