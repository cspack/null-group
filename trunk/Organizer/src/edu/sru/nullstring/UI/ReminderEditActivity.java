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
        	Log.e("ReminderEditActivity","Found extra's bundle.");
 	       // Setup database helper and object early on
 	       int reminderID = extras.getInt("edu.sru.nullstring.reminderId");
 	       if(reminderID != 0)
 		   {
 	        	Log.e("ReminderEditActivity","Found reminder! Id = " +reminderID);
 	           try {
 				   Log.i("ReminderNewActivity", "open attempt with id " + Integer.toString(reminderID));
 	        	   reminder = helper.getReminderDao().queryForId(reminderID);
 	        	   Log.i("Locadex", "Success opening item for editing");
 			   } catch (SQLException e) {
 			   	   // TODO Auto-generated catch block
 				   Log.e("ReminderNewActivity", "Failed to open item for editing");
 				   e.printStackTrace();
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
        
        
  	   // Associate category spinner with item
  	   Spinner subCatSpinner = (Spinner)findViewById(R.id.subCatSpinner);
  	   CategoryAdapter catAdapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, 
  			   helper, CategoryAdapter.SubCategoryType.Reminder, reminder);
  	   subCatSpinner.setAdapter(catAdapter);
  	   subCatSpinner.setSelection(catAdapter.getSelectedIndex());
  	   
  	   subCatSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

 		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
 				long arg3) {

 			try {
 				CategoryAdapter cat = (CategoryAdapter)(arg0.getAdapter());
 				CategoryType itm = cat.getItem(arg2);
 				reminder.setCategory(itm.getID());
 			} catch (Exception e) {
 				Log.e("ReminderEditActivity", "Edit item category update failed.");
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 		}

 		public void onNothingSelected(AdapterView<?> arg0) {
 			// TODO Auto-generated method stub
 			
 		}
  		   
  	   });

  	   // Associate category spinner with item
  	   Spinner markerSpinner = (Spinner)findViewById(R.id.reminder_new_location);
  	   MarkerAdapter markAdapter = new MarkerAdapter(this, android.R.layout.simple_spinner_item, 
  			   helper, true);
  	   markerSpinner.setAdapter(markAdapter);
  	   markerSpinner.setSelection(markAdapter.findMarkerPosition(reminder.markerId));
  	 	markerSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

 		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
 				long arg3) {

 			try {
 				MarkerAdapter adp = (MarkerAdapter)(arg0.getAdapter());
 				MarkerType itm = adp.getItem(arg2);
 				reminder.markerId = itm.getID();
 				
 			} catch (Exception e) {
 				Log.e("ReminderEditActivity", "Edit item category update failed.");
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 		}

 		public void onNothingSelected(AdapterView<?> arg0) {
 			// TODO Auto-generated method stub
 			
 		}
  		   
  	   });
        
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
        
        // restore initial values
        restoreData();
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
    			if (reminder.advancedUseTime)
    			{
    			//begin copy from use_time event listener
        			dat.setVisibility(View.VISIBLE);
        			tp.setVisibility(View.VISIBLE);
        		//end copy
    			}
    			if (reminder.advancedUseLocation)
    			{
    			//begin copy from use_time event listener
        			loc.setVisibility(View.VISIBLE);
        		//end copy
    			}

        	}
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
    public void restoreData()
    {
    	if(isCreateMode) {
        	Log.e("ReminderEditActivity", "Not restoring information, because default object.");
    		return;
    	}
    	
    	debugPrint();
    	
    	Log.e("ReminderEditActivity", "Restoring reminder... Title: " + reminder.getTitle());
    	EditText title = (EditText)findViewById(R.id.editText1);
    	title.setText(reminder.getTitle());
    	
    	Spinner rtype = (Spinner) findViewById(R.id.reminder_type);

        
    	// first set category mode
    	switch(reminder.getReminderType())
    	{
    	case Location: //TODO: make location item in switch
    		break;
		case Quick:
			rtype.setSelection(0);
    		RadioButton qtoday = (RadioButton)findViewById(R.id.quick_today);
    		if(!isCreateMode)
    		{
    		qtoday.setChecked(true); // set it to today mode, not hr/min from now

    		LinearLayout hl = (LinearLayout) findViewById(R.id.hours_layout);
    		LinearLayout ml = (LinearLayout) findViewById(R.id.minutes_layout);
    		TimePicker qtt = (TimePicker) findViewById(R.id.quick_today_time);
    		hl.setVisibility(View.GONE);
    		ml.setVisibility(View.GONE);
    		qtt.setVisibility(View.VISIBLE);
    		
			TimePicker qtodayTime = (TimePicker)findViewById(R.id.quick_today_time);
			qtodayTime.setCurrentHour(reminder.fireTimeHour);
			qtodayTime.setCurrentMinute(reminder.fireTimeMinute);
    		}
			break;
		case Advanced:
			rtype.setSelection(2);
			CheckBox useLocation = (CheckBox)findViewById(R.id.use_location);
			useLocation.setChecked(reminder.advancedUseLocation);
			//TODO: Display fields
			// Insert location spinner modification
			if(reminder.advancedUseLocation)
			{
				useLocation.setVisibility(View.VISIBLE);
			}
			
			CheckBox useTime = (CheckBox)findViewById(R.id.use_time);
			useTime.setChecked(reminder.advancedUseTime);
			if (reminder.advancedUseTime)
			{
			//begin copy from use_time event listener
				TimePicker tp = (TimePicker) findViewById(R.id.reminder_new_time);
				RadioGroup dat = (RadioGroup) findViewById(R.id.reminder_date);
    			dat.setVisibility(View.VISIBLE);
    			tp.setVisibility(View.VISIBLE);
    		//end copy
			}
    			else  break;
			
		case DateTime:
			if(reminder.getReminderType() == ReminderTypes.DateTime)
			{
				rtype.setSelection(1);
			}
			TimePicker qtodayTime = (TimePicker)findViewById(R.id.reminder_new_time);
			qtodayTime.setCurrentHour(reminder.fireTimeHour);
			qtodayTime.setCurrentMinute(reminder.fireTimeMinute);
			
			// update datetime picker
			DatePicker qtodayDate = (DatePicker)findViewById(R.id.reminder_dat);
    		qtodayDate.updateDate(reminder.fireTimeYear, reminder.fireTimeMonth, reminder.fireTimeDay);

			break;
    	}
    	
    	

    	
    	Spinner loc	= (Spinner) findViewById(R.id.reminder_new_location);
    	MarkerAdapter mloc = (MarkerAdapter)loc.getAdapter();
    	loc.setSelection(mloc.findMarkerPosition(reminder.markerId));

    	
    	RadioButton rrep = (RadioButton) findViewById(R.id.date_rep);
    	RadioButton rday = (RadioButton) findViewById(R.id.date_day);
    	
    	if(reminder.useRepeat == true)
    	{
    		rrep.setChecked(true);
    		DatePicker rd = (DatePicker) findViewById(R.id.reminder_dat);
    		LinearLayout rl = (LinearLayout) findViewById(R.id.repeating_layout);
    		rd.setVisibility(View.GONE);
    		rl.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		rday.setChecked(true);
    	}

    	
    	ToggleButton repSun = (ToggleButton)findViewById(R.id.rep_sun);
    	ToggleButton repMon = (ToggleButton)findViewById(R.id.rep_mon);
    	ToggleButton repTue = (ToggleButton)findViewById(R.id.rep_tue);
    	ToggleButton repWed = (ToggleButton)findViewById(R.id.rep_wed);
    	ToggleButton repThu = (ToggleButton)findViewById(R.id.rep_thu);
    	ToggleButton repFri = (ToggleButton)findViewById(R.id.rep_fri);
    	ToggleButton repSat = (ToggleButton)findViewById(R.id.rep_sat);
    	repSun.setChecked(reminder.repeatSun);
    	repMon.setChecked(reminder.repeatMon);
    	repTue.setChecked(reminder.repeatTue);
    	repWed.setChecked(reminder.repeatWed);
    	repThu.setChecked(reminder.repeatThu);
    	repFri.setChecked(reminder.repeatFri);
    	repSat.setChecked(reminder.repeatSat);    

    	
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
    		
    	
    	if(reminder.advancedUseLocation || reminder.getReminderType() == ReminderTypes.Location)
    	{
    	
	    	Spinner loc	= (Spinner) findViewById(R.id.reminder_new_location);
	    	MarkerAdapter mloc = (MarkerAdapter)loc.getAdapter();
	    	
	    	int selMarkerId = mloc.getItem(loc.getSelectedItemPosition()).getID();
	    	reminder.markerId = selMarkerId;
    	}
    	else
    	{
    		reminder.markerId = 0;
    	}
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
        		todayCal.set(Calendar.HOUR_OF_DAY, hour);
        		todayCal.set(Calendar.MINUTE, min);
    		}
    		else
    		{
    			// manual add min hour
        		EditText qmin = (EditText)findViewById(R.id.quick_minutes);
        		EditText qhour = (EditText)findViewById(R.id.quick_hours);
        		try {
        		hour = Integer.parseInt(qhour.getText().toString());
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
    	
			// sync next fire with DB
			reminder.calculateNextFire();
			
			debugPrint();
			
    	if(isCreateMode)
				reminder.create();
		else
    		reminder.update();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}


    public void debugPrint()
    {
    	Log.i("Reminder-Debug", "Fire Month: " + reminder.fireTimeMonth + " Day: " + reminder.fireTimeDay + " Year:"  + reminder.fireTimeYear );
    	Log.i("Reminder-Debug", "Fire Hour: "  + reminder.fireTimeHour + " Minute: " + reminder.fireTimeMinute);
    }
    
}

