package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NoteAdapter extends ArrayAdapter<NoteType> {
    private List<NoteType> items;
    private Context context;
    private DatabaseHelper helper;
    
    
    private void reloadNotes()
    {

    	try {
    		
    		CategoryType curCat = helper.getCurrentCategory();
    		if(curCat.getFixedType() == CategoryType.FixedTypes.All)
    		{
    			items = helper.getNoteDao().queryForAll();
    		}
    		else
    		{
    			items = helper.getNoteDao().queryForEq(NoteType.CAT_ID_FIELD, curCat.getID());
    		}
    		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return;
		}
    }
    public NoteAdapter(Context context, int textViewResourceId, DatabaseHelper helper) {
            super(context, textViewResourceId);
            this.helper = helper;
            reloadNotes();
            this.context = context;
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

    @Override
	public NoteType getItem(int position) {
    	return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.note_list_row, null);
            }
            
            
            //TODO: Import fields from the object itself into the layout
            
            NoteType o = items.get(position);
            if (o != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText("Title: "+o.getTitle());
                    }
                    if(bt != null)
                    {
                		DatabaseHelper h = OpenHelperManager.getHelper(context, DatabaseHelper.class); 
                     	String cattitle = h.getCategoryString(o);
                    	bt.setText("Category: "+ cattitle);

                    }
            }
            
            return v;
    }



    public void refreshData()
    {

    	reloadNotes();
    	notifyDataSetChanged();
    	
    }
    
}