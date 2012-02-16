package edu.sru.nullstring.UI;

import edu.sru.nullstring.R;
import edu.sru.nullstring.R.layout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

// XML Based View
// Source: http://www.permadi.com/blog/2010/03/android-sdk-using-custom-view-in-xml-based-layout/
public class GlobalHeaderView extends LinearLayout {

	private View xmlView;
	
	
	// Constructor from XML files
	public GlobalHeaderView(Context context, AttributeSet attrs) {
	super(context, attrs);


		
	// Expand global_header xml into content.
		
	LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	xmlView=layoutInflater.inflate(R.layout.global_header,this);

	
    // Generate Categories
    // http://developer.android.com/resources/tutorials/views/hello-spinner.html

	// Dynamic spinner
	// http://stackoverflow.com/questions/5453580/android-dynamic-spinner-update

	

    Spinner spinner = (Spinner) xmlView.findViewById(R.id.categorySpinner);
    final String[] demoItems = new String[] {"All", "Personal", "School", "Work"};
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(xmlView.getContext(),
                android.R.layout.simple_spinner_item, demoItems);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);	    
	}

	

}