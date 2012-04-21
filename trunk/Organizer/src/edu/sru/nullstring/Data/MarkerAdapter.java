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
    
    public MarkerAdapter(Context context, int textViewResourceId, DatabaseHelper helper) {
            super(context, textViewResourceId);
            this.helper = helper;
            reloadMarkers();
            this.context = context;
    }
    
    public MarkerType getMarker(int position)
    {
    	return items.get(position);
    }
    

    
    private void reloadMarkers()
    {

    	try {
    		
    		CategoryType curCat = helper.getCurrentCategory();
    		if(curCat.getFixedType() == CategoryType.FixedTypes.All)
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
                v = vi.inflate(R.layout.marker_list_row, null);
            }
            
            
            //TODO: Import fields from the object itself into the layout
            
            MarkerType o = items.get(position);
            if (o != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText("Title: "+o.getTitle());
                    }
                    if(bt != null){
                		DatabaseHelper h = OpenHelperManager.getHelper(context, DatabaseHelper.class); 
                     	String cattitle = h.getCategoryString(o);
                    	bt.setText("Category: "+ cattitle);

                    }
            }
            
            return v;
    }


}
