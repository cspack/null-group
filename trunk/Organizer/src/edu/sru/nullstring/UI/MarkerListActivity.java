package edu.sru.nullstring.UI;

import edu.sru.nullstring.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MarkerListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.marker_list);
    }
    
}