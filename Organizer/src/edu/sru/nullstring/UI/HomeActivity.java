package edu.sru.nullstring.UI;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class HomeActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    /** Called when the activity is first created. **/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.main);
        
        // This'll probably break it unless i put it intoonlayout
        // Hide 'go home' button
        GlobalHeaderView head = (GlobalHeaderView)findViewById(R.id.topBanner);
        head.setActivity(this);
        Button b = (Button)head.findViewById(R.id.homeIcon);
        b.setVisibility(View.GONE);
        
        // attach events to buttons
        
        Button button = (Button)this.findViewById(R.id.notebookIcon);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), NoteMainActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});        

        button = (Button)this.findViewById(R.id.checklistIcon);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), ChecklistMainActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});

        
        button = (Button)this.findViewById(R.id.reminderIcon);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), ReminderMainActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});        

        
        button = (Button)this.findViewById(R.id.markerIcon);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), MarkerMainActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});

        
    }

	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.refreshData();
		

		NextReminderView n = (NextReminderView)findViewById(R.id.nextReminderView);
		if(n != null) n.refreshData();
		
	}
}