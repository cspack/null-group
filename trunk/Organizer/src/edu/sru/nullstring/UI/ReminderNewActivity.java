package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
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

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ReminderNewActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_new);
        
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
        
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			finish();
        	}
        });
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
}

