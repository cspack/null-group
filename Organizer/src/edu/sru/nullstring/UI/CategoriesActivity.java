package edu.sru.nullstring.UI;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.*;
import edu.sru.nullstring.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Toast;
import android.widget.ListView;

public class CategoriesActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private ListView mListView;
	private DatabaseHelper helper;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.categories);
    	
    	mListView = (ListView)findViewById(R.id.categoryListView);
        
		try {

			
			// possible option for future refactoring / recreation
			// http://stackoverflow.com/questions/7159816/android-cursor-with-ormlite
			// this will make the lists of lists WAY faster, but its super complicated android code
			// you'd have to recreate all of the adaptors to use it
			
	        // connect to DAO helper, ugly but it works flawlessly.
	        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
	        
			// apply to list adapter.
			mListView.setAdapter(new CategoryAdapter(this,
	                R.layout.categories_list_row, helper));
				        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mListView.setTextFilterEnabled(true);
		
		// attach list item click
		mListView.setOnItemClickListener(mListClickListener);
		
		Button addItem = (Button)findViewById(R.id.addItem);
		addItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				addItem(v);
			}
		});
    }
    

    
	@Override
	protected void onResume() {
		super.onResume(); // important
		
		// refresh listview data
		((CategoryAdapter)mListView.getAdapter()).refreshData();
		
        Log.i("Locadex", "Attempting to refresh list onresume.");
	}
	
	
	private View lastView = null;
	private CategoryType currentCat;
    // Handle list clicks
    OnItemClickListener mListClickListener = new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			Log.i("NoteMainActivity:OnItemClickListener", String.valueOf(position));
    			// last view, hide remove button again
    			if(lastView != null)
    			{
    			
    				lastView.setBackgroundColor(Color.WHITE);
    				Button hider = (Button)lastView.findViewById(R.id.listRightButtons);
    				hider.setVisibility(View.GONE);
    			}

    			// current view, make remove button visible
    			if(v != null)
    			{
        			CategoryAdapter clAdapt = (CategoryAdapter) parent.getAdapter();
        			currentCat = clAdapt.getItem(position);
        			if(currentCat.getFixedType() == CategoryType.FixedTypes.None)
        			{
        				v.setBackgroundColor(Color.LTGRAY);
        				Button hider = (Button)v.findViewById(R.id.listRightButtons);
        				hider.setOnClickListener(new OnClickListener() {
    						public void onClick(View v)
    						{

    			    			if(lastView != null)
    			    			{
    			    			
    			    				lastView.setBackgroundColor(Color.WHITE);
    			    				Button hider = (Button)lastView.findViewById(R.id.listRightButtons);
    			    				hider.setVisibility(View.GONE);
    			    			}
    			    			lastView = null;
    							remove(currentCat);
    						}
    					});
    				hider.setVisibility(View.VISIBLE);
    				lastView = v;
        			}
    			}
    		}
    };
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.checklist_menu, menu);
        return true;        
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text:     Toast.makeText(this, "You pressed Jeb!", Toast.LENGTH_LONG).show();
                                break;
        }
        return true;
    }
    
    public boolean remove(CategoryType cat)
    {

    	// never delete a fixed category
		if(cat.getFixedType() != CategoryType.FixedTypes.None)
			return false;
		
		
        DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
    	try {
    		// TODO Step1: Confirm yes/no (notify items will be moved to 'unsorted')
    		
    		// Step2: Go thru all checklists, markers, notes, reminders, move items to Unsorted
    		CategoryType unsorted = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.Unsorted).query().get(0);
    		helper.MoveAllCategoryItems(cat.getID(), unsorted.getID());

    		// Step3: If activecategory == toDelete, Set active category as Unsorted
    		if(helper.getCurrentCategory().getID() == unsorted.getID())
    			unsorted.setCurrent(true);

    		// Step4: Delete
			helper.getCategoryDao().delete(cat);

			// refresh listview data
			((CategoryAdapter)mListView.getAdapter()).refreshData();

    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return false;
		}
    	return true;
    }
    
    // Create new category
    public boolean addItem(View v){
    	// popup category create window with title
    	// ok or cancel
    	// if okay, change
    	
        LayoutInflater factory = LayoutInflater.from(v.getContext());            
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext()); 

        alert.setTitle("Create Category"); 
        alert.setMessage("Enter your Category title"); 
        // Set an EditText view to get user input  

        final EditText input = new EditText(v.getContext());
        input.setText("");
        alert.setView(input);


        // Create
        alert.create();
        
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int whichButton) { 

        	try {
                CategoryType data = new CategoryType(CategoryType.FixedTypes.None);
                data.setDao(helper.getCategoryDao());
            	data.setTitle(input.getText().toString());
				data.create();
				((CategoryAdapter)mListView.getAdapter()).refreshData();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.e("CategoriesActivity","Update of new category.");
			}
        }
        }); 

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
            // Canceled. 
          } 
        }); 
        
        // Pop it!
        alert.show();


    	return true;
    }
    
    
}
