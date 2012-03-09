package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
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
    private List<CategoryType> items;
    private Context context;
    private int textViewResourceId;
    
    private Dao<CategoryType, Integer> categoryDao;
    private CategoryType allCategories;
    private CategoryType unsortedCategories;
    
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

    	// Add fixed items to item size.
		return items.size()+2;
	}

    @Override
	public CategoryType getItem(int position) {

        CategoryType o = null;
    	
        // CASE 1: Looking for unsorted list.
        if(position > items.size())
        {
        	o = unsortedCategories;
        }
        // Case 2: All Categories
        else if(position == 0)
        {
        	o = allCategories;
        }
        // Case 3: all non-fixed categories
        else
        {
        	// Get pos - 1 [subtract for 'all categories']
        	o = items.get(position-1);
        	
        }
        return o;
	}

    public void RefreshData()
    {
    	this.items = QueryItemList(categoryDao);
    }


	public CategoryAdapter(Context context, int textViewResourceId, Dao<CategoryType, Integer> catDao) {
            
    		// Try to construct without a list
    		super(context, textViewResourceId);
    	
            this.textViewResourceId = textViewResourceId;
            this.categoryDao = catDao;
            
            // Get all categories field

        	try {
            	List<CategoryType> item;
    			item = catDao.queryForEq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.All);
    			if(item.size()>0)
    			{
    				allCategories = item.get(0);
    				Log.i("Locadex", "All Categories pulled!!!");
    			}
    			else{
    				
    				Log.i("Locadex", "All Categories wasn't defined!!!");
    			}
    			
    		} catch (SQLException e) {
    			Log.e("Locadex", "Getting 'All Categories' query from Category DAO failed.");
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

            
            // Get unsorted category field
            
        	try {
            	List<CategoryType> item;
    			item = catDao.queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.Unsorted).query();
    			if(item.size()>0)
    			{
    				unsortedCategories = item.get(0);
    			}
    			
    			
    		} catch (SQLException e) {
    			Log.e("Locadex", "Getting 'Unsorted Categories' query from Category DAO failed.");
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        	
            // Get unfixed categories from database.
            this.items = QueryItemList(catDao);
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



    public int GetSelectedIndex()
    {
    	Log.i("Locadex","Pulling currently selected index....");
    	int cur = 0;
    	// query for selected item
    	if(allCategories.getCurrent())
    	{
    		cur = 0;
    	}
    	else if(unsortedCategories.getCurrent())
    	{
    		cur = items.size()+1;
    	}
    	else
    	{
    		for(int i=0;i<items.size();i++)
    		{
    			if(items.get(i).getCurrent())
    			{
    				cur = i;
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
                v = vi.inflate(android.R.layout.simple_spinner_item, null);
            }
            
            
            //TODO: Import fields from the object itself into the layout

            CategoryType o = getItem(position);
            
            if (o != null) {
            	TextView tt = (TextView)v.findViewById(android.R.id.text1);
            	tt.setText(o.getTitle());         
            }
   

            return v;
    }


}
