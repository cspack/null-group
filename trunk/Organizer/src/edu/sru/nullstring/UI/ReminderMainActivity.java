package edu.sru.nullstring.UI;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ReminderMainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_main);
        

    	
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    }
}
