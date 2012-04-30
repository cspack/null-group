package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MarkerAdapter;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MarkerListActivity extends OrmLiteBaseListActivity<DatabaseHelper> {

	private int selectedPosition = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	//        setContentView(android.R.layout.simple_list_item_1);

        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
        
        setListAdapter(new MarkerAdapter(this,
                R.layout.marker_list_row, helper));
        
        mainActivity = (MarkerMainActivity)this.getParent();
        
        this.getListView().setOnItemClickListener(mListClickListener);
        this.getListView().setOnItemLongClickListener(mListLongClickListener);
        

    }

    private MarkerType item;
    
    /**
     * Edit the note's title
     * @param position
     * @return
     */
    public boolean editTitle(int position)
    {
    	item = (MarkerType)MarkerListActivity.this.getListView().getItemAtPosition(position);

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit");

		LinearLayout alertLayout = new LinearLayout(this);
		alertLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final Spinner catSpin = new Spinner(this);
		populateCatSpinner(catSpin);
		
		// Set the max length of a note's title to 40 characters
		int maxLength = 40;
		InputFilter[] FilterArr = new InputFilter[1];
		FilterArr[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(FilterArr);
		
		// Set the text to the current title and highlight it
		input.setText(item.getTitle());
		input.setSelectAllOnFocus(true);
		
		catSpin.setSelection(((CategoryAdapter)catSpin.getAdapter()).getSelectedIndex());
		
		alertLayout.addView(input);
		alertLayout.addView(catSpin);
		
		
		
		alert.setView(alertLayout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// edit the title of the checklist
				String value = input.getText().toString();
				item.setTitle(value);
				
				CategoryAdapter catAdapt = (CategoryAdapter)catSpin.getAdapter();
				CategoryType itm = catAdapt.getItem(catSpin.getSelectedItemPosition());
				item.setCategory(itm.getID());
				try
				{
					item.update();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				((MarkerAdapter)MarkerListActivity.this.getListAdapter()).refreshData();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
    	
    	return true;
    }

    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		MarkerAdapter m = (MarkerAdapter)this.getListView().getAdapter();
		if(m != null) m.refreshData();
		
		
	}

	/**
	 * Populates the category spinner with the current categories
	 */
	private void populateCatSpinner(Spinner mSpinner)
	{
		// requirement -- all activities that call globalheader must have the helper
		DatabaseHelper h = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		
		try {
			Dao<CategoryType, Integer> dao = h.getCategoryDao();

			//mSpinner = (Spinner)findViewById(R.id.categorySpinner);
			
			// this is requiring adapter
		    //CategoryAdapter adapter = new CategoryAdapter(this,
		    //		android.R.layout.simple_spinner_item, h);
		    
		 	CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, 
		 			h, CategoryAdapter.SubCategoryType.Marker, item);

			// apply to list adapter.
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    mSpinner.setAdapter(adapter);
			mSpinner.setSelection(adapter.getSelectedIndex(), false);
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


	private MarkerMainActivity mainActivity = null;
	private View lastView = null;
	private MarkerType currentMarker;
    // Handle list long press clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			
    			Log.i("MarkerMainActivity:OnItemClickListener", String.valueOf(position));
    			// last view, hide remove button again
    			if(lastView != null)
    			{
    			
    				lastView.setBackgroundColor(Color.WHITE);
    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.GONE);
    			}

    			// current view, open it
    			if(v != null)
    			{
        			MarkerAdapter nada = (MarkerAdapter) parent.getAdapter();
        			currentMarker = nada.getItem(position);

        			mainActivity.openEditorActivity(currentMarker);
    			}
    			
    			
    		}
    };
    // Handle list long press clicks
    OnItemLongClickListener mListLongClickListener = new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
    		{

    			Log.i("MarkerListActivity:OnItemLongClickListener", String.valueOf(position));
    			// last view, hide remove button again
    			if(lastView != null)
    			{
    				lastView.setBackgroundColor(Color.WHITE);
    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.GONE);
    				ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
    				edit.setVisibility(View.GONE);
    			}

    			// current view, make remove button visible
    			if(v != null)
    			{
        			MarkerAdapter nada = (MarkerAdapter) parent.getAdapter();
        			currentMarker = nada.getItem(position);
    				v.setBackgroundColor(Color.LTGRAY);
    				ImageButton hider = (ImageButton)v.findViewById(R.id.listRightButtons);
    				hider.setOnClickListener(new OnClickListener() {
    						public void onClick(View v)
    						{
    			    			if(lastView != null)
    			    			{
    			    				lastView.setBackgroundColor(Color.WHITE);
    			    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    			    				hider.setVisibility(View.GONE);
    			    				ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
    			    				edit.setVisibility(View.GONE);
    			    			};
    							mainActivity.remove(currentMarker);
    						}
    					});
    				hider.setVisibility(View.VISIBLE);
    				
    				ImageButton edit = (ImageButton)v.findViewById(R.id.editBtn);
    				edit.setOnClickListener(new OnClickListener() {
    					public void onClick(View v)
    					{
    		    			if(lastView != null)
    		    			{
    		    				lastView.setBackgroundColor(Color.WHITE);
    		    				ImageButton hider = (ImageButton)lastView.findViewById(R.id.listRightButtons);
    		    				hider.setVisibility(View.GONE);
    		    				
    		    				ImageButton edit = (ImageButton)lastView.findViewById(R.id.editBtn);
    		    				edit.setVisibility(View.GONE);
    		    			};
    						editTitle(selectedPosition);
    						
    						((MarkerAdapter)(MarkerListActivity.this.getListAdapter())).refreshData();
    					}
    				});
    				edit.setVisibility(View.VISIBLE);

    				lastView = v;
    			}
        		
        		return true;
    		}
    };
    
    
}
