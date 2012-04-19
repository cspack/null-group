package edu.sru.nullstring.UI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.LatLonPoint;
import edu.sru.nullstring.Data.MarkerAdapter;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.MarkerType.MarkerState;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class MarkerEditInfoActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	public DatabaseHelper helper = null;
	public MarkerType editItem = null;
	public static final String LOG_TAG = "MarkerEditInfoActivity";

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.marker_info);

    	Activity act = this.getParent();
    	if(act instanceof MarkerEditActivity)
    	{
    		MarkerEditActivity mact = (MarkerEditActivity)act; 
    		editItem = mact.editItem;
    		helper = mact.helper;
    	}
    	else
    	{
    		// WTF
    		Log.e(LOG_TAG, "The parent isn't a marker edit activity. AW MAN!");
    		return;
    	}
    	
 	   // Associate category spinner with item
 	   Spinner subCatSpinner = (Spinner)findViewById(R.id.markerCategory);
 	   CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, 
 			   helper, CategoryAdapter.SubCategoryType.Marker, editItem);
 	   subCatSpinner.setAdapter(adapter);
 	   subCatSpinner.setSelection(adapter.getSelectedIndex());
 	   
 	   subCatSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			try {
				CategoryAdapter cat = (CategoryAdapter)(arg0.getAdapter());
				CategoryType itm = cat.getItem(arg2);
				editItem.setCategory(itm.getID());
				editItem.update();
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Edit item category update failed.");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
 		   
 	   });
 	   
 	   
 	   
 	   
 	   // Associate address field with item
 	   final EditText addr = (EditText)findViewById(R.id.editAddress);
 	   addr.setText(editItem.getAddress());
 	 
 	   Button fromLoc = (Button)findViewById(R.id.geocodeAddress);
 	   fromLoc.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			// TODO Auto-generated method stub

		 	  Geocoder geocoder = new Geocoder(v.getContext(), Locale.getDefault());
		 	  if(editItem.getMarkerState() == MarkerState.Defined)
		 	  {
			 	  LatLonPoint loc = editItem.getLocation();
			 	 List<Address> addresses;
				try {
					addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				 	 if(addresses.size() > 0)
				 	 {
				 		 Address address = addresses.get(0);
				 		 String addrStr = "";
		                    if (addresses.size() > 0) 
		                    {
		                        for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
		                             i++)
		                        	addrStr += addresses.get(0).getAddressLine(i) + "\n";
		                    }
				 		 addr.setText(addrStr);
				 		 editItem.setAddress(addrStr);
				 		 editItem.update();
				 	 }
				 	 else
				 	 {
				 		 // no results
				 		 Toast.makeText(v.getContext().getApplicationContext(), "No Address Found.", Toast.LENGTH_SHORT);
				 	 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();				 		 
			 		Toast.makeText(v.getContext().getApplicationContext(), "Address Lookup Failed.", Toast.LENGTH_SHORT);

				}
		 	  }
		 	  else
		 	  {
			 		 Toast.makeText(v.getContext().getApplicationContext(), "No Location Current is Defined.", Toast.LENGTH_SHORT);
		 	  }
		}});
 	   

 	   Button fromAddr = (Button)findViewById(R.id.geocodeLocation);
 	   fromAddr.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			// TODO Auto-generated method stub

		 	  Geocoder geocoder = new Geocoder(v.getContext(), Locale.getDefault());
			 	 List<Address> addresses;
				try {
					addresses = geocoder.getFromLocationName(addr.getText().toString(), 1);
				 	 if(addresses.size() > 0)
				 	 {
				 		 Address address = addresses.get(0);
				 		 editItem.setLocation(address.getLatitude(), address.getLongitude());
				 		 Toast.makeText(v.getContext().getApplicationContext(), "Success - Location set from Address!", Toast.LENGTH_SHORT);
				 		 editItem.update();
				 	 }
				 	 else
				 	 {
				 		 // no results
				 		 Toast.makeText(v.getContext().getApplicationContext(), "No Address Found.", Toast.LENGTH_SHORT);
				 	 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();				 		 
			 		Toast.makeText(v.getContext().getApplicationContext(), "Address Lookup Failed.", Toast.LENGTH_SHORT);

				}
		}});

    	
    }
    
    
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
 	   // Events before finishing with tab
		try {
	 	   EditText addr = (EditText)findViewById(R.id.editAddress);

			editItem.setAddress(addr.getText().toString());
			editItem.update();
						
		} catch (Exception e) {
			Log.e(LOG_TAG, "Refreshing DAO item failed [address]");
			e.printStackTrace();
		}
		
		
	}


    public void refreshData()
    {
		// i'm responsible for refreshing data here.
  	   EditText addr = (EditText)findViewById(R.id.editAddress);
  	   addr.setText(editItem.getAddress());
 		
  	   // Refresh adapter
  	   Spinner subCatSpinner = (Spinner)findViewById(R.id.markerCategory);
  	   CategoryAdapter spinAdap = (CategoryAdapter)subCatSpinner.getAdapter();
  	   spinAdap.refreshData();
    	
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// This is super important, activity will be paused and removed w/out being destroyed
		refreshData();

	}
    
    
}
