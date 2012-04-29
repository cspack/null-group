package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.ChecklistType;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.ReminderType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// XML Based View
// Source: http://www.permadi.com/blog/2010/03/android-sdk-using-custom-view-in-xml-based-layout/
public class NextReminderView extends LinearLayout {

	private View xmlView;
	private Spinner mSpinner;
	private DatabaseHelper helper;
	private ReminderType item;
	
	private Handler refresher = new Handler();
	private Runnable refresherRunnable;
	
	// Constructor from XML files
	public NextReminderView(Context context, AttributeSet attrs) {
	super(context, attrs);


	// Expand global_header xml into content.
	LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	xmlView=layoutInflater.inflate(R.layout.next_reminder,this);

    helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);

	}

	public void refreshData()
	{
		QueryBuilder<ReminderType, Integer> q;
		try {
			q = helper.getReminderDao().queryBuilder();
			
			long current = new Date().getTime();
			
			// refresh all reminders
			
		q.orderBy(ReminderType.NEXT_FIRE_FIELD, true).where().ge(ReminderType.NEXT_FIRE_FIELD, current); // ascending
		item = q.queryForFirst();

		if(item != null)
	{
		// no items in reminders
		RelativeLayout next = (RelativeLayout)findViewById(R.id.nextReminderLayout);
		next.setVisibility(View.VISIBLE);

		Button viewBtn = (Button)findViewById(R.id.buttonView);
		viewBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				openEditorActivity(item);
			}
			
		});
		
		
		TextView none = (TextView)findViewById(R.id.textNoUpcoming);
		none.setVisibility(View.GONE);
		
		// FILL item with it...

		TextView topText = (TextView)findViewById(R.id.textRemindTop);
		topText.setText(item.getTitle());

		TextView btmText = (TextView)findViewById(R.id.textRemindBottom);
		btmText.setText(ReminderType.GetDisplayTimeString(item));

	}
	else
	{
		
		// no items in reminders
		
		TextView none = (TextView)findViewById(R.id.textNoUpcoming);
		none.setVisibility(View.VISIBLE);

		RelativeLayout next = (RelativeLayout)findViewById(R.id.nextReminderLayout);
		next.setVisibility(View.GONE);
	}
		} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
		}

	}


	public void openEditorActivity(ReminderType item)
	{
        Intent myIntent = new Intent(this.getContext(), ReminderEditActivity.class);
        myIntent.putExtra("edu.sru.nullstring.reminderId", item.getID());
        this.getContext().startActivity(myIntent);
	}

	
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		// SETUP 'oncreate' style stuff here that requires finding elements

		refreshData();
		
		refresherRunnable = new Runnable()
		{
			private Calendar cal = Calendar.getInstance();
			public void run()
			{
				refreshData();
				
				final long startTime = System.currentTimeMillis();
				cal.setTime(new Date(startTime));
				cal.add(Calendar.MINUTE, 1);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				refresher.postDelayed(this, cal.getTime().getTime() - startTime);
			}
		};
		
		
		refresher.post(refresherRunnable);

	
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);	    


	}

	

}