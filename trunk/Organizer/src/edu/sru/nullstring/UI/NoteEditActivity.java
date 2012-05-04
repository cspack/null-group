/*
 * Copyright (c) 2012, SRU Cygnus Nullstring.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

*     Redistributions of source code must retain the above copyright notice, 
		this list of conditions and the following disclaimer.
	
*     Redistributions in binary form must reproduce the above copyright notice, 
     	this list of conditions and the following disclaimer in the documentation 
     	and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package edu.sru.nullstring.UI;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.Data.SerialBitmap;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;


public class NoteEditActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {    

	public DatabaseHelper helper = null;
	public NoteType editItem = null;
	int noteID;
	
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.note_edit);
       final TabHost tabHost = getTabHost();
       
       tabHost.addTab(tabHost.newTabSpec("tab1")
               .setIndicator("Text Note")
               .setContent(new Intent(this, NoteTextActivity.class)));

       tabHost.addTab(tabHost.newTabSpec("tab2")
               .setIndicator("Drawing")
               .setContent(new Intent(this, NoteDrawingActivity.class)));
       
       helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
       
       // Here is where you refresh the UI for things that may have changed:
       GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
       if(h != null) h.setActivity(this);
	   	
       Bundle extras = getIntent().getExtras();        
       if(extras != null)
       {
	       // Setup database helper and object early on
	       noteID = extras.getInt("edu.sru.nullstring.noteId");
	       if(noteID != 0)
		   {
	
	           try {
				   Log.i("NoteEditActivity", "open attempt with id " + Integer.toString(noteID));
	        	   editItem = helper.getNoteDao().queryForId(noteID);
	        	   Log.i("Locadex", "Success opening item for editing");
			   } catch (SQLException e) {
			   	   // TODO Auto-generated catch block
				   Log.e("Locadex", "Failed to open item for editing");
				   e.printStackTrace();
			   }
	           /*
		       // Edit UI of NoteHeader
		       TextView noteName = (TextView) this.findViewById(R.id.title);
		       noteName.setText(editItem.getTitle());*/
		       
	       }
	       else
	       {
	    	   Log.i("Locadex:NoteEditActivity","Wanted id unknown.");
	       }
       }
	   else
	   {	
		   Log.i("Locadex:NoteEditActivity","Bundle is null");
	   }
   }
}