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
import android.widget.CheckBox;
import android.widget.TextView;

public class ChecklistItemAdapter extends ArrayAdapter<ChecklistItemType> {
    private List<ChecklistItemType> items;
    private Context context;
    private DatabaseHelper helper;
    private int itemID;
    
    private void reloadChecklists()
    {

    	try
    	{
    		items = helper.getChecklistItemDao().queryForEq(ChecklistItemType.LIST_ID_FIELD, itemID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return;
		}
    }
    public ChecklistItemAdapter(Context context, int textViewResourceId, DatabaseHelper helper, int itemID)
    {
            super(context, textViewResourceId);
            this.helper = helper;
            this.itemID = itemID;            
            reloadChecklists();
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
	public ChecklistItemType getItem(int position) {
    	return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.checklist_list_item_row, null);
            }
            
            
            //TODO: Import fields from the object itself into the layout
            ChecklistItemType o = items.get(position);
            if (o != null)
            {
                    TextView tt = (TextView) v.findViewById(R.id.text);
                    CheckBox cb = (CheckBox) v.findViewById(R.id.chkbox);
                    if (tt != null)
                    {
                    	tt.setText(o.getText() + " ID: " + o.getListID());
                    }
                    if (cb != null)
                    {
                    	cb.setChecked(o.getChecked());
                    }
                    /**
                     * Turn in the 30th
                     * - Design Spec
                     * - User Manual / Help system
                     * - Testing (unit / interface)
                     * - User testing
                     * - tech document
                     */
            }
            
            return v;
    }



    public void refreshData()
    {

    	reloadChecklists();
    	notifyDataSetChanged();
    	
    }
    
}
