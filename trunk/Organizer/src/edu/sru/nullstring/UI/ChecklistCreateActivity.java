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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ListView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ChecklistCreateActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.checklist_create);
       
		populateCatSpinner();
		
		// adds an eventhandler to create a new checklist
		Button addItem = (Button)findViewById(R.id.ok);
		addItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				
				if (addItem(v))
				{
					finish();
				}
			}
		});
		
		Button cancelBtn = (Button)findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
					finish();
			}
		});
		
	}
	
	/**
	 * Adds an item to the checklist
	 * @param v
	 * @return true on success
	 */
	public boolean addItem(View v){
		DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
		ChecklistType data = new ChecklistType(helper);
		
		// get the value from the textbox
		EditText name = (EditText)findViewById(R.id.entryChecklistTitle);
		String title = name.getText().toString();
		
		Spinner catSpin = (Spinner)findViewById(R.id.categorySpinner);
		// + 1 makes up for the offset of the categories starting at 1 and not 0
		int catID = catSpin.getSelectedItemPosition() + 1;
		
		data.setCategoryID(catID);
		data.setTitle(title);

		try {
			data.create(); // add to database
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
		
		return true;
   }
	
	protected void onStop() {
	    // TODO Auto-generated method stub
	    setResult(2);
	    super.onStop();
	}
	@Override
	protected void onDestroy() {
	    // TODO Auto-generated method stub
	    setResult(2);
	    super.onDestroy();
	}

	/**
	 * Populates the category spinner with the current categories
	 */
	private void populateCatSpinner()
	{
		Spinner mSpinner;
		
		
		// requirement -- all activities that call globalheader must have the helper
		DatabaseHelper h = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		
		try {
			Dao<CategoryType, Integer> dao = h.getCategoryDao();

			mSpinner = (Spinner)findViewById(R.id.categorySpinner);
			
		    CategoryAdapter adapter = new CategoryAdapter(this,
		    		android.R.layout.simple_spinner_item, dao);

			// apply to list adapter.
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    mSpinner.setAdapter(adapter);
			mSpinner.setSelection(adapter.GetSelectedIndex(), false);
		    // select to current category id
			 
		} catch (SQLException e) {
			Log.e("Locadex", "Failed loading data into GlobalHeaderView. ");
			// TODO Auto-generated catch block
			// e.printStackTrace();
			
		}
		catch(Exception e)
		{
			Log.e("Locadex", "Really failed hard querying categories... ");
			Log.e("Locadex", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
