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
	
	EditText name;
	String title;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.checklist_create);
       
		name = (EditText)findViewById(R.id.entryChecklistTitle);
		title = name.getText().toString();
		
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
	}
	public boolean addItem(View v){
		DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
		ChecklistType data = new ChecklistType(helper);
		data.setTitle("Vanere is a goose");
		try {
			data.create(); // add to database
			List<ChecklistType> results = helper.getChecklistDao().queryForAll();
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
	
}
