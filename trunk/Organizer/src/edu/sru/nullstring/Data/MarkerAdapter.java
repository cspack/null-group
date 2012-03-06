package edu.sru.nullstring.Data;

import java.util.List;

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
    
    public MarkerAdapter(Context context, int textViewResourceId, List<MarkerType> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.checklist_list_row, null);
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
                    	DatabaseHelper h = ((LocadexApplication)(this.getContext().getApplicationContext())).getDatabaseHelper();

                    	CategoryType cat = o.getCategory(h);
                     	String cattitle = cat == null ? "Unknown" : cat.getTitle();
                    	bt.setText("Category: "+ cattitle);
                    }
            }
            
            return v;
    }


}
