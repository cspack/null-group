package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MarkerAdapter extends ArrayAdapter<MarkerType> {
    private List<MarkerType> items;
    private Context context;
    private DatabaseHelper helper;
    private boolean showAllMarkers = false;
    private int textViewResourceId = R.layout.marker_list_row;
    
    public MarkerAdapter(Context context, int textViewResourceId, DatabaseHelper helper) {
            super(context, textViewResourceId);
            this.textViewResourceId = textViewResourceId;
            this.helper = helper;
            reloadMarkers();
            this.context = context;
    }
    
    public MarkerAdapter(Context context, int textViewResourceId, DatabaseHelper helper, boolean showAllMarkers) {
        super(context, textViewResourceId);
        this.helper = helper;
        this.showAllMarkers = showAllMarkers;
        reloadMarkers();
        this.context = context;
        this.textViewResourceId = textViewResourceId;
    }

    
    public MarkerType getMarker(int position)
    {
    	return items.get(position);
    }
    

    @Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	
        View v = convertView;
        if (v == null) {
        	LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }
        
        //TODO: Import fields from the object itself into the layout
        MarkerType o = getItem(position);

        
        if (o != null) {
            TextView tt = (TextView)v.findViewById(android.R.id.text1);

            DatabaseHelper h = OpenHelperManager.getHelper(context, DatabaseHelper.class); 
         	String cattitle = h.getCategoryString(o);
            tt.setText( cattitle + " > " + o.getTitle());
        }


        return v;
	}


    
    
    private void reloadMarkers()
    {

    	try {
    		
    		CategoryType curCat = helper.getCurrentCategory();
    		if(showAllMarkers || curCat.getFixedType() == CategoryType.FixedTypes.All)
    		{
    			items = helper.getMarkerDao().queryForAll();
    		}
    		else
    		{
    			items = helper.getMarkerDao().queryForEq(MarkerType.CAT_ID_FIELD, curCat.getID());
    		}
    		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return;
		}
    }

    @Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		// db update goes here.. see category type.
		
	}
    

    @Override
	public int getCount() {

    	// Add fixed items to item size.
		return items.size();
	}

    

    public void refreshData()
    {

    	reloadMarkers();
    	notifyDataSetChanged();
    	
    }
    
    @Override
	public MarkerType getItem(int position) {
    	return items.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(textViewResourceId, null);
            }
            
            
            //TODO: Import fields from the object itself into the layout
            
            MarkerType o = items.get(position);
            if (o != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText("Title: "+o.getTitle());
                    }
            		
                    DatabaseHelper h = OpenHelperManager.getHelper(context, DatabaseHelper.class); 
                 	String cattitle = h.getCategoryString(o);
                    
                    // for small view
                    TextView st = (TextView) v.findViewById(android.R.id.text1);
                    if (st != null) {
                          st.setText(cattitle + " > " + o.getTitle());
                    }
                    if(bt != null){
                    	bt.setText("Category: "+ cattitle);

                    }
            }
            
            return v;
    }

	public int findMarkerPosition(int markerId) {

		int pos = -1;
		for(MarkerType t : items)
		{
			pos++;
			if(t.getID() == markerId)
				return pos;
		}
		
		return -1;
	}


}
