package edu.sru.nullstring.UI;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. **/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.main);
        
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
}