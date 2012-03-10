package edu.sru.nullstring.UI;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class NoteMainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_main);
    }
    
    private static final int CREATE_MENU_ID = Menu.FIRST;
    private static final int FONTSIZE_MENU_ID = Menu.FIRST + 1;
    private static final int TEST_MENU_ID = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, CREATE_MENU_ID, 0, "Create").setShortcut('3', 'c');
        menu.add(0, FONTSIZE_MENU_ID, 0, "Font Size").setShortcut('4', 's');
        menu.add(0, TEST_MENU_ID, 0, "Test").setShortcut('5', 'z');
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CREATE_MENU_ID:
                //create note
                return true;
            case FONTSIZE_MENU_ID:
                //adjust font size
                return true;
            case TEST_MENU_ID:
                //Testing menu items
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    


	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// Here is where you refresh the UI for things that may have changed:
		GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
		if(h != null) h.RefreshData();
		
	}
}
