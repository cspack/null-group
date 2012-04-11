package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.QueryBuilder;

import edu.sru.nullstring.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<CategoryType> {

	public static final String LOG_TAG = "CategoryAdapter";
	
    private List<CategoryType> items;
    private Context context;
    private int textViewResourceId;

    private DatabaseHelper helper;
    private Dao<CategoryType, Integer> categoryDao;
    private CategoryType allCategories;
    private CategoryType unsortedCategories;
    
    public enum SubCategoryType
    {
    	MainCategory,
    	Checklist,
    	Marker,
    	Note,
    	Reminder
    }
    
    private SubCategoryType currentSub = SubCategoryType.MainCategory;
    private Object currentItem = null;
    
    
    public static List<CategoryType> QueryItemList(Dao<CategoryType, Integer> cat)
    {
    	List<CategoryType> item = new ArrayList<CategoryType>();
    	try {
    		
    		QueryBuilder<CategoryType, Integer> q = cat.queryBuilder();
    		q.orderBy(CategoryType.TITLE_FIELD, true); // ascending
			item = q.where().eq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.None).query();
			
		} catch (SQLException e) {
			Log.e("Locadex", "Getting complex query from Category DAO failed.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item;		   	
    }
    

    
    @Override
	public int getCount() {
    	
        boolean ignoreAllCategories = (currentSub != SubCategoryType.MainCategory);

        int size = items.size()+(ignoreAllCategories?1:2);
        
        // Tested to be working
        // Log.d(LOG_TAG,"Cat: "+currentSub.toString()+ "; Ignore: " +ignoreAllCategories+"; Number of items: " + size);
        
        return size;

	}

    @Override
	public CategoryType getItem(int position) {

        CategoryType o = null;
    	
        boolean ignoreAllCategories = (currentSub != SubCategoryType.MainCategory); 
        
        if(ignoreAllCategories)
        {

            Log.d(LOG_TAG,"Ignoring all categories. Cat: "+currentSub.toString()+ "; Ignore: " +ignoreAllCategories+"; Position: " + position);
        	
	        // CASE 1: Looking for unsorted list.
	        if(position >= items.size())
	        {
	        	o = unsortedCategories;
	        }
	        // Case 3: all non-fixed categories
	        else
	        {
	        	o = items.get(position);
	        }
        }
        else
        {
            Log.d(LOG_TAG,"INCLUDING all categories. Cat: "+currentSub.toString()+ "; Ignore: " +ignoreAllCategories+"; Position: " + position);
        
        	// Case 2: All Categories
	        if(position == 0)
	        {
	        	o = allCategories;
	        }// CASE 1: Looking for unsorted list.
	        else if(position > items.size())
	        {
	        	o = unsortedCategories;
	        }
	        // Case 3: all non-fixed categories
	        else
	        {
	        	// Get pos - 1 [subtract for 'all categories']
	        	o = items.get(position-1);
	        	
	        }
        }
        return o;
	}

    public void refreshData()
    {

        refreshStaticCategories();
    	this.items.clear();
    	this.items = QueryItemList(categoryDao);
    	this.notifyDataSetChanged();
    	Log.i(LOG_TAG, "Forcing redraw! Cur Cat: " + this.getSelectedIndex());
    	
    }

    private void refreshStaticCategories()
    {
        // Get all categories field
        Log.i(LOG_TAG, "Refreshing static cats, currentSub:" + currentSub.toString());

    	// ALL CATEGORIES will be null/hidden for sub items, ignore it.
        if(currentSub == SubCategoryType.MainCategory)
        {

    	try {
        	List<CategoryType> item;
			item = categoryDao.queryForEq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.All);
			if(item.size()>0)
			{
				allCategories = item.get(0);
				Log.i(LOG_TAG, "All Categories pulled!!!");
			}
			else{
				
				Log.i(LOG_TAG, "All Categories wasn't defined!!!");
			}
			
		} catch (SQLException e) {
			Log.e("Locadex", "Getting 'All Categories' query from Category DAO failed.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        }
        
        
        // Get unsorted category field
        
    	try {
        	List<CategoryType> item;
			item = categoryDao.queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.Unsorted).query();
			if(item.size()>0)
			{
				unsortedCategories = item.get(0);
			}
			
			
			
		} catch (SQLException e) {
			Log.e(LOG_TAG, "Getting 'Unsorted Categories' query from Category DAO failed.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public CategoryAdapter(Context context, int textViewResourceId, DatabaseHelper helper) {
		this(context, textViewResourceId, helper, SubCategoryType.MainCategory, null);
	}
	public CategoryAdapter(Context context, int textViewResourceId, DatabaseHelper helper, SubCategoryType subCatType, Object link) {
            
    		// Try to construct without a list
    		super(context, textViewResourceId);
    	
            this.textViewResourceId = textViewResourceId;
            this.helper = helper;
            try {
				this.categoryDao = helper.getCategoryDao();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(LOG_TAG, "Helper has failed to query DAO.");
			}
            
            this.currentItem = link;
            		
            this.currentSub = subCatType;
            
            refreshStaticCategories();
        	
            // Get unfixed categories from database.
            this.items = QueryItemList(this.categoryDao);
            this.context = context;
    }

    
    
    @Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	
    	// Check for refresh?
    	if(position == 0)
    	{
    		items = QueryItemList(categoryDao);
    	}
    	
        View v = convertView;
        if (v == null) {
        	LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }
        

        //TODO: Import fields from the object itself into the layout
        CategoryType o = getItem(position);

        
        if (o != null) {
            TextView tt = (TextView)v.findViewById(android.R.id.text1);
            tt.setText(o.getTitle());
        }


        return v;
	}



    public int getSelectedIndex()
    {
    	
    	// figure out selected index based on child

    	int matchMe = -1;

    	if(currentSub == SubCategoryType.Checklist && currentItem != null)
    	{    		
    		ChecklistType item = (ChecklistType)currentItem;
    		matchMe = item.getCategoryID();
    	}
    	if(currentSub == SubCategoryType.Marker && currentItem != null)
    	{    		
    		MarkerType item = (MarkerType)currentItem;
    		matchMe = item.getCategoryID();
    	}
    	if(currentSub == SubCategoryType.Note && currentItem != null)
    	{    		
    		NoteType item = (NoteType)currentItem;
    		matchMe = item.getCategoryID();
    	}
    	if(currentSub == SubCategoryType.Reminder && currentItem != null)
    	{    		
    		ReminderType item = (ReminderType)currentItem;
    		matchMe = item.getCategoryID();
    	}
    	if(matchMe != -1)
    	{
    		if(unsortedCategories.getID() == matchMe)
    		{
    			return items.size();
    		}else
    		{
    			for(int i=0;i<items.size();i++)
    			{
    				if(items.get(i).getID() == matchMe)
    					return i;
    			}
    			
    			// If I get here, Category is either in a non existant category or 'All Categories'
    			// Move to Unsorted
    			Log.i(LOG_TAG,"Item's Category is invalid. Resetting to Unknown.");

      			try {
		 			if(currentItem instanceof ChecklistType)
		 			{
	 					((ChecklistType)currentItem).setCategory(unsortedCategories.getID());
	 					((ChecklistType)currentItem).update();
		 			}
		 			else if(currentItem instanceof MarkerType)
		 			{
	 					((MarkerType)currentItem).setCategory(unsortedCategories.getID());
	 					((MarkerType)currentItem).update();
		 			}
		 			else if(currentItem instanceof NoteType)
		 			{
	 					((NoteType)currentItem).setCategory(unsortedCategories.getID());
	 					((NoteType)currentItem).update();
		 			}
		 			else if(currentItem instanceof ReminderType)
		 			{
	 					((ReminderType)currentItem).setCategory(unsortedCategories.getID());
	 					((ReminderType)currentItem).update();
		 			}

				} catch (SQLException e) {
					Log.e(LOG_TAG, "Item's Category update failed.");
				}
    			
    			return items.size();
    			
    		}
    		
    	}
    	
    	
    	Log.i(LOG_TAG,"Searching for currently selected item.");
    	int cur = 0;

	    	// query for selected item
	    	if(allCategories != null && allCategories.isCurrent())
	    	{
	    		cur = 0;
	    	}
	    	else if(unsortedCategories.isCurrent())
	    	{
	    		cur = items.size() + 1; 
	    	}
	    	else
	    	{
	    		for(int i=0;i<items.size();i++)
	    		{
	    			if(items.get(i).isCurrent())
	    			{
	    				cur = i + 1;
	    				break;
	    			}
	    		}
	    	}
    	
    	
    	return cur;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
            View v = convertView;
            if (v == null) {

            	LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(textViewResourceId, null);
            }
            
            
            //TODO: Import fields from the object itself into the layout

            CategoryType o = getItem(position);
            boolean isStatic = o.getFixedType() != CategoryType.FixedTypes.None;
            if (o != null) {
            	// Drop down list
            	TextView tt = (TextView)v.findViewById(android.R.id.text1);
            	if(tt != null) tt.setText(o.getTitle());
            	
            	// List view
            	tt = (TextView)v.findViewById(R.id.text1);
            	if(tt != null) tt.setText(o.getTitle()); 
            	tt = (TextView)v.findViewById(R.id.text2);
            	if(tt != null) tt.setText(isStatic ? "Static category (Cannot be deleted)" : "");
            }
   

            return v;
    }


}
