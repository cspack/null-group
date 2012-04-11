package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MarkerAdapter;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MarkerEditInfoActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	public DatabaseHelper helper = null;
	public MarkerType editItem = null;
	public static final String LOG_TAG = "MarkerEditInfoActivity";

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.marker_info);

    	Activity act = this.getParent();
    	if(act instanceof MarkerEditActivity)
    	{
    		MarkerEditActivity mact = (MarkerEditActivity)act; 
    		editItem = mact.editItem;
    		helper = mact.helper;
    	}
    	else
    	{
    		// WTF
    		Log.e(LOG_TAG, "The parent isn't a marker edit activity. AW MAN!");
    		return;
    	}
    	
 	   // Associate category spinner with item
 	   Spinner subCatSpinner = (Spinner)findViewById(R.id.markerCategory);
 	   CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, 
 			   helper, CategoryAdapter.SubCategoryType.Marker, editItem);
 	   subCatSpinner.setAdapter(adapter);
 	   subCatSpinner.setSelection(adapter.getSelectedIndex());
 	   
 	   // Associate address field with item

 	   

    	
    }
    
    
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
    }
    
    
}
