package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.ChecklistType;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// XML Based View
// Source: http://www.permadi.com/blog/2010/03/android-sdk-using-custom-view-in-xml-based-layout/
public class GlobalHeaderView extends LinearLayout {

	private View xmlView;
	private Spinner mSpinner;
	
	// Constructor from XML files
	public GlobalHeaderView(Context context, AttributeSet attrs) {
	super(context, attrs);


	// Expand global_header xml into content.
		

	LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	xmlView=layoutInflater.inflate(R.layout.global_header,this);

	
    // Generate Categories
    // http://developer.android.com/resources/tutorials/views/hello-spinner.html

	// Dynamic spinner
	// http://stackoverflow.com/questions/5453580/android-dynamic-spinner-update

		// requirement -- all activities that call globalheader must have the helper
		DatabaseHelper h = OpenHelperManager.getHelper(context, DatabaseHelper.class);

		try {
			Dao<CategoryType, Integer> dao = h.getCategoryDao();

			
			mSpinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
		    
		    CategoryAdapter adapter = new CategoryAdapter(xmlView.getContext(),
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
		
		mSpinner.setOnItemSelectedListener(mListSelectedListener);
		
		Button home = (Button)findViewById(R.id.homeIcon);  
		home.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
    				if(currentActivity != null)
    				{
    					currentActivity.finish();
    					
    				}
                	
                }

        });

		/*home.setOnTouchListener(new OnTouchListener(){

		public boolean onTouch(View v, MotionEvent event) {
				
				
				if(currentActivity != null)
				{
					Toast.makeText(v.getContext(), "THIS IS SUPPOSED TO BE GONE!",  Toast.LENGTH_LONG);
					currentActivity.finishActivity(Activity.RESULT_OK);
					
				}
				else
				{
					Toast.makeText(v.getContext(), "WTF MAN", Toast.LENGTH_LONG);
				}
				
				return true;
			}
			
		});
    */
	}
			
	
	private Activity currentActivity;
	public void setActivity(Activity act)
	{
		currentActivity = act;
	}

    // Handle list clicks
	OnItemSelectedListener mListSelectedListener = new OnItemSelectedListener() {
    		
    		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    		
    			CategoryType o = (CategoryType)mSpinner.getAdapter().getItem(position);
    			o.setCurrent(true);

    		}
    		
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	    	// your code here
    	    }

    };
    
	public void RefreshData()
	{
	    Spinner spinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
	    CategoryAdapter adapter = (CategoryAdapter)spinner.getAdapter();

		// reload from database
	    adapter.RefreshData();
		
	    // reselect selected
	    spinner.setSelection(adapter.GetSelectedIndex(), false);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);	    

	    Spinner spinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
	    CategoryAdapter adapter = (CategoryAdapter)spinner.getAdapter();
	}

	

}