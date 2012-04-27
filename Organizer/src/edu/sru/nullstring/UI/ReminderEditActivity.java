package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.Data.ReminderType.ReminderTypes;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.app.Application;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ToggleButton;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ReminderEditActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
	// persist a remindertype
	private ReminderType reminder;
	private DatabaseHelper helper;
	private boolean isCreateMode = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_new);
        
        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
        Bundle extras = getIntent().getExtras();        
        if(extras != null)
        {
 	       // Setup database helper and object early on
 	       int reminderID = extras.getInt("edu.sru.nullstring.reminderId");
 	       if(reminderID != 0)
 		   {
 	           try {
 				   Log.i("ReminderNewActivity", "open attempt with id " + Integer.toString(reminderID));
 	        	   reminder = helper.getReminderDao().queryForId(reminderID);
 	        	   Log.i("Locadex", "Success opening item for editing");
 			   } catch (SQLException e) {
 			   	   // TODO Auto-generated catch block
 				   Log.e("ReminderNewActivity", "Failed to open item for editing");
 				   e.printStackTrace();
 			   }
 	           finally
 	           {
 	        	  isCreateMode = true;
 	 	          reminder = new ReminderType(helper);
 	           }

 	       }
 	       else
 	       {
 	    	   Log.i("ReminderNewActivity","Wanted id unknown, must be 'create' mode.");
 	          reminder = new ReminderType(helper);
 	         isCreateMode = true;
 	       }
        }
 	   else
 	   {	
 		   Log.i("ReminderNewActivity","Bundle is null");
          reminder = new ReminderType(helper);
          isCreateMode = true;
 	   }
        
        
        // jordan this looks like ass!
        // please comment ffs

        
        Spinner rtype = (Spinner) findViewById(R.id.reminder_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.reminder_new_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rtype.setAdapter(adapter);
        rtype.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        RadioButton qnow = (RadioButton) findViewById(R.id.quick_now);
        RadioButton qtod = (RadioButton) findViewById(R.id.quick_today);
        RadioButton dday = (RadioButton) findViewById(R.id.date_day);
        RadioButton drep = (RadioButton) findViewById(R.id.date_rep);
        qnow.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		LinearLayout hl = (LinearLayout) findViewById(R.id.hours_layout);
        		LinearLayout ml = (LinearLayout) findViewById(R.id.minutes_layout);
        		TimePicker qtt = (TimePicker) findViewById(R.id.quick_today_time);
        		qtt.setVisibility(View.GONE);
        		hl.setVisibility(View.VISIBLE);
        		ml.setVisibility(View.VISIBLE);
        	}
        });
        qtod.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		LinearLayout hl = (LinearLayout) findViewById(R.id.hours_layout);
        		LinearLayout ml = (LinearLayout) findViewById(R.id.minutes_layout);
        		TimePicker qtt = (TimePicker) findViewById(R.id.quick_today_time);
        		hl.setVisibility(View.GONE);
        		ml.setVisibility(View.GONE);
        		qtt.setVisibility(View.VISIBLE);
        	}
        });
        dday.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		DatePicker rd = (DatePicker) findViewById(R.id.reminder_dat);
        		LinearLayout rl = (LinearLayout) findViewById(R.id.repeating_layout);
        		rl.setVisibility(View.GONE);
        		rd.setVisibility(View.VISIBLE);
        	}
        });
        drep.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		DatePicker rd = (DatePicker) findViewById(R.id.reminder_dat);
        		LinearLayout rl = (LinearLayout) findViewById(R.id.repeating_layout);
        		rd.setVisibility(View.GONE);
        		rl.setVisibility(View.VISIBLE);
        	}
        });
        
        CheckBox uloc = (CheckBox) findViewById(R.id.use_location);
        CheckBox utim = (CheckBox) findViewById(R.id.use_time);
        uloc.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Spinner loc	= (Spinner) findViewById(R.id.reminder_new_location);
        		if(((CheckBox) v).isChecked()) {
        			loc.setVisibility(View.VISIBLE);
        		}
        		else if(((CheckBox) v).isChecked() == false) {
        			loc.setVisibility(View.GONE);
        		}
        	}
        });
        utim.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		RadioGroup dat = (RadioGroup) findViewById(R.id.reminder_date);
        		TimePicker tp = (TimePicker) findViewById(R.id.reminder_new_time);
        		if(((CheckBox) v).isChecked()) {
        			dat.setVisibility(View.VISIBLE);
        			tp.setVisibility(View.VISIBLE);
        		}
        		else if(((CheckBox) v).isChecked() == false) {
        			dat.setVisibility(View.GONE);
        			tp.setVisibility(View.GONE);
        		}
        	}
        });

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			saveReminder();
    			finish();
        	}
        });

        
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			finish();
        	}
        });
        
    }

    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// apply data from item
        restoreData();
	}


	public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	
        	CheckBox ul = (CheckBox) findViewById(R.id.use_location);
        	CheckBox ut = (CheckBox) findViewById(R.id.use_time);
        	RadioGroup qui = (RadioGroup) findViewById(R.id.reminder_quick);
        	RadioGroup dat = (RadioGroup) findViewById(R.id.reminder_date);
        	Spinner loc	= (Spinner) findViewById(R.id.reminder_new_location);
        	TextView til = (TextView) findViewById(R.id.tim_label);
        	TimePicker tp = (TimePicker) findViewById(R.id.reminder_new_time);
        	
        	if(0==parent.getItemAtPosition(pos).toString().compareToIgnoreCase("Quick"))
            {
        		dat.setVisibility(View.GONE);
        		loc.setVisibility(View.GONE);
        		til.setVisibility(View.GONE);
        		ul.setVisibility(View.GONE);
        		ut.setVisibility(View.GONE);
        		tp.setVisibility(View.GONE);
        		qui.setVisibility(View.VISIBLE);
            } 
        	else if(0==parent.getItemAtPosition(pos).toString().compareToIgnoreCase("Date/Time"))
        	{
        		loc.setVisibility(View.GONE);
        		ul.setVisibility(View.GONE);
        		ut.setVisibility(View.GONE);
        	  	qui.setVisibility(View.GONE);
        	  	dat.setVisibility(View.VISIBLE);
        	  	til.setVisibility(View.VISIBLE);
        	  	tp.setVisibility(View.VISIBLE);
        	}
        	else if(0==parent.getItemAtPosition(pos).toString().compareToIgnoreCase("Advanced"))
        	{
        		til.setVisibility(View.GONE);
        		qui.setVisibility(View.GONE);
        		dat.setVisibility(View.GONE);
        		loc.setVisibility(View.GONE);
        		tp.setVisibility(View.GONE);
        	  	ul.setVisibility(View.VISIBLE);
        	  	ut.setVisibility(View.VISIBLE);
        	}
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
    public void restoreData()
    {
        Spinner rtype = (Spinner) findViewById(R.id.reminder_type);

        
    	// first set category mode
    	switch(reminder.getReminderType())
    	{
    	case Location: //TODO: make location item in switch
		case Advanced:
			rtype.setSelection(2);
			break;
		case Quick:
			rtype.setSelection(0);
    		RadioButton qtoday = (RadioButton)findViewById(R.id.quick_today);
    		if(!isCreateMode)
    		{
    		qtoday.setSelected(true); // set it to today mode, not hr/min from now
    		
			TimePicker qtodayTime = (TimePicker)findViewById(R.id.quick_today_time);
			qtodayTime.setCurrentHour(reminder.fireTimeHour);
			qtodayTime.setCurrentMinute(reminder.fireTimeMinute);
    		}
			break;
		case DateTime:
			rtype.setSelection(1);

			TimePicker qtodayTime = (TimePicker)findViewById(R.id.reminder_new_time);
			qtodayTime.setCurrentHour(reminder.fireTimeHour);
			qtodayTime.setCurrentMinute(reminder.fireTimeMinute);
			
			// update datetime picker
			DatePicker qtodayDate = (DatePicker)findViewById(R.id.reminder_dat);
    		qtodayDate.updateDate(reminder.fireTimeYear, reminder.fireTimeMonth, reminder.fireTimeDay);

			break;
    	}
    	
    	
    	RadioButton rrep = (RadioButton) findViewById(R.id.date_rep);
    	RadioButton rday = (RadioButton) findViewById(R.id.date_day);
    	
    	if(reminder.useRepeat == true)
    	{
    		rrep.setChecked(true);
    	}
    	else
    	{
    		rday.setChecked(true);
    	}

    	
    	Button repSun = (Button)findViewById(R.id.rep_sun);
    	Button repMon = (Button)findViewById(R.id.rep_mon);
    	Button repTue = (Button)findViewById(R.id.rep_tue);
    	Button repWed = (Button)findViewById(R.id.rep_wed);
    	Button repThu = (Button)findViewById(R.id.rep_thu);
    	Button repFri = (Button)findViewById(R.id.rep_fri);
    	Button repSat = (Button)findViewById(R.id.rep_sat);
    	repSun.setSelected(reminder.repeatSun);
    	repMon.setSelected(reminder.repeatMon);
    	repTue.setSelected(reminder.repeatTue);
    	repWed.setSelected(reminder.repeatWed);
    	repThu.setSelected(reminder.repeatThu);
    	repFri.setSelected(reminder.repeatFri);
    	repSat.setSelected(reminder.repeatSat);    

    	
    }
    
    public void saveDateFields()
    {
    	
    	// repeating
    	RadioButton rrep = (RadioButton) findViewById(R.id.date_rep);
    	RadioButton rday = (RadioButton) findViewById(R.id.date_day);
		// auto time
		TimePicker qtodayTime = (TimePicker)findViewById(R.id.reminder_new_time);
		int hour = qtodayTime.getCurrentHour();
		int min = qtodayTime.getCurrentMinute();
		// set time
		Date today = new Date();
        Calendar todayCal = Calendar.getInstance();  
        todayCal.setTime(today);  
        todayCal.set(Calendar.HOUR_OF_DAY, hour);
		todayCal.set(Calendar.MINUTE, min);

		// mode 1: use datepicker
    	if(rday.isChecked())
    	{
    		reminder.useRepeat = false;
    		DatePicker qtodayDate = (DatePicker)findViewById(R.id.reminder_dat);
            todayCal.set(Calendar.DAY_OF_MONTH,     qtodayDate.getDayOfMonth());
            todayCal.set(Calendar.MONTH,     		qtodayDate.getMonth());
            todayCal.set(Calendar.YEAR,     		qtodayDate.getYear());
    	}

		reminder.fireTimeDay = todayCal.get(Calendar.DAY_OF_MONTH);
		reminder.fireTimeMonth = todayCal.get(Calendar.MONTH);
		reminder.fireTimeYear = todayCal.get(Calendar.YEAR);
		reminder.fireTimeHour = todayCal.get(Calendar.HOUR_OF_DAY);
		reminder.fireTimeMinute = todayCal.get(Calendar.MINUTE);    	
    	// mode 2: repeating

		if(rrep.isChecked())
    	{
    	reminder.useRepeat = true;	
    	// repeat fields
    	ToggleButton repSun = (ToggleButton)findViewById(R.id.rep_sun);
    	ToggleButton repMon = (ToggleButton)findViewById(R.id.rep_mon);
    	ToggleButton repTue = (ToggleButton)findViewById(R.id.rep_tue);
    	ToggleButton repWed = (ToggleButton)findViewById(R.id.rep_wed);
    	ToggleButton repThu = (ToggleButton)findViewById(R.id.rep_thu);
    	ToggleButton repFri = (ToggleButton)findViewById(R.id.rep_fri);
    	ToggleButton repSat = (ToggleButton)findViewById(R.id.rep_sat);
    	reminder.repeatSun = repSun.isChecked();
    	reminder.repeatMon = repMon.isChecked();
    	reminder.repeatTue = repTue.isChecked();
    	reminder.repeatWed = repWed.isChecked();
    	reminder.repeatThu = repThu.isChecked();
    	reminder.repeatFri = repFri.isChecked();
    	reminder.repeatSat = repSat.isChecked();    
    	}
    }
    
    public void saveLocationFields()
    {
    	Spinner loc	= (Spinner) findViewById(R.id.reminder_new_location);    	
    }
    
    public void saveReminder()
    {
    	// 1: get mode
        Spinner rtype = (Spinner) findViewById(R.id.reminder_type);
        String spinType = rtype.getSelectedItem().toString();
        
    	CheckBox ul = (CheckBox) findViewById(R.id.use_location);
    	CheckBox ut = (CheckBox) findViewById(R.id.use_time);

    	// title = simple... no bullshit.
    	EditText title = (EditText)findViewById(R.id.editText1);
    	reminder.setTitle(title.getText().toString());
    	
    	//TODO: Category
    	
		Date today = new Date();
        Calendar todayCal = Calendar.getInstance();  
        todayCal.setTime(today);  
    	
    	if(0==spinType.compareToIgnoreCase("Quick"))
    	{
    		int hour = 0, min = 0;
    		// add todays date
			// reset calendar
            todayCal.setTime(today);  
    		
    		reminder.setReminderType(ReminderTypes.Quick);
    		// proper fields are in qui
    		RadioButton qtoday = (RadioButton)findViewById(R.id.quick_today);
    		if(qtoday.isChecked())
    		{
    			Log.e("ReminderNewActivity", "Attempting to base time off of a TimePicker.");
    			// auto time
    			TimePicker qtodayTime = (TimePicker)findViewById(R.id.quick_today_time);
    			hour = qtodayTime.getCurrentHour();
    			min = qtodayTime.getCurrentMinute();
    			Log.e("ReminderNewActivity", "Hour: " + hour);
    			Log.e("ReminderNewActivity", "Min: " + min);
        		todayCal.set(Calendar.HOUR_OF_DAY, hour);
        		todayCal.set(Calendar.MINUTE, min);
    		}
    		else
    		{
    			Log.e("ReminderNewActivity", "Attempting to base time off of hours/min in text.");
    			// manual add min hour
        		EditText qmin = (EditText)findViewById(R.id.quick_minutes);
        		EditText qhour = (EditText)findViewById(R.id.quick_hours);
        		try {
        		hour = Integer.parseInt(qhour.getText().toString());
    			Log.e("ReminderNewActivity", "Hour: " + hour);
        		}catch(Exception e)
        		{
        			hour = 0;
        			Log.e("ReminderNewActivity", "unable to cast hour str -> int");
        		}
        		try {
        		min = Integer.parseInt(qmin.getText().toString());
    			Log.e("ReminderNewActivity", "Min: " + min);
        		}catch(Exception e)
        		{
        			min = 0;
        			Log.e("ReminderNewActivity", "unable to cast min str -> int");
        		}
        		
        		todayCal.add(Calendar.HOUR_OF_DAY, hour);
        		todayCal.add(Calendar.MINUTE, min);
    		}

    		reminder.fireTimeDay = todayCal.get(Calendar.DAY_OF_MONTH);
    		reminder.fireTimeMonth = todayCal.get(Calendar.MONTH);
    		reminder.fireTimeYear = todayCal.get(Calendar.YEAR);
    		reminder.fireTimeHour = todayCal.get(Calendar.HOUR_OF_DAY);
    		reminder.fireTimeMinute = todayCal.get(Calendar.MINUTE);

    	}
    	if(0==spinType.compareToIgnoreCase("Advanced"))
    	{
    		reminder.setReminderType(ReminderTypes.Advanced);
    		reminder.advancedUseLocation = ul.isChecked();
    		if(reminder.advancedUseLocation)
    		{
    			// get marker id [location]
    			saveLocationFields();
    		}
    		reminder.advancedUseTime = ut.isChecked();
    		if(reminder.advancedUseTime)
    		{
    			// get all the datetime fields
    			saveDateFields();
    		}
    		
    	}
    	
    	if(0==spinType.compareToIgnoreCase("Location"))
    	{
    		reminder.setReminderType(ReminderTypes.Location);
    		// get marker id [location]
			saveLocationFields();
    	}
    	if(0==spinType.compareToIgnoreCase("Date/Time"))
    	{
    		reminder.setReminderType(ReminderTypes.DateTime);
    		// get datetime fields
			saveDateFields();

    	}
    		
		try {
    	
    	if(isCreateMode)
				reminder.create();
		else
    		reminder.update();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}


}

