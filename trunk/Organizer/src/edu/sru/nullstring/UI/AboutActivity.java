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

public class AboutActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    /** Called when the activity is first created. **/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
    	//Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.about);
        
    }

	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.refreshData();
		
	}
}