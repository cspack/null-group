package edu.sru.nullstring.Data;

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

public class ChecklistAdapter extends ArrayAdapter<ChecklistType> {
    private List<ChecklistType> items;
    private Context context;
    
    public ChecklistAdapter(Context context, int textViewResourceId, List<ChecklistType> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
    }

    
    
    @Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		// db update goes here.. see category type.
		
	}



	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	
    	
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.checklist_list_row, null);
            }
            
            
            Log.w("Locadex","Requerying listview!");
            //TODO: Import fields from the object itself into the layout
            
            ChecklistType o = items.get(position);
            if (o != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText(o.getTitle());
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
