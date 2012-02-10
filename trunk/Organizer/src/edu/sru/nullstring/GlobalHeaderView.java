package edu.sru.nullstring;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

// XML Based View
// Source: http://www.permadi.com/blog/2010/03/android-sdk-using-custom-view-in-xml-based-layout/
public class GlobalHeaderView extends LinearLayout {

	// Constructor from XML files
	public GlobalHeaderView(Context context, AttributeSet attrs) {
	super(context, attrs);


		
	// Expand global_header xml into content.
		
	LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view=layoutInflater.inflate(R.layout.global_header,this);

    // Generate Categories
    // http://developer.android.com/resources/tutorials/views/hello-spinner.html

	}

}