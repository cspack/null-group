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

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.Data.SerialBitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class NoteTextActivity extends Activity{
	public DatabaseHelper helper = null;
	NoteEditActivity act = null;
	EditText noteText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.note_edit_text);
        act = (NoteEditActivity) this.getParent();
        helper = act.helper;
    	noteText = (EditText)findViewById(R.id.note);
    }
    

    @Override
    protected void onPause(){
		super.onPause();
		try {
	    	noteText = (EditText)findViewById(R.id.note);
	    	act.editItem.noteContent = noteText.getText().toString();

				act.editItem.update();
				Log.i("NoteTextActivity:onPause", "Saving text success.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("NoteTextActivity:onPause", "Saving text failed.");
			}
					
    }

    @Override
	protected void onResume()
	{
		super.onResume();
    	try{
    		if(act.editItem.noteContent == null)
    			act.editItem.noteContent = "";	    	
    		
    		noteText = (EditText)findViewById(R.id.note);
    		noteText.setText(act.editItem.noteContent);
    	}catch(Exception e){
			e.printStackTrace();
			Log.i("NoteTextActivity:onResume", "Restoring text failed.");
    		
    	}
    	
	}
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.note_text_menu, menu);
        return true;        
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.small:     
	    		noteText.setTextSize(12);
	    		break;
	    	case R.id.medium:     
	    		noteText.setTextSize(22);
	    		break;
	    	case R.id.large:     
	    		noteText.setTextSize(32);
	    		break;
    	}
        return true;
    }

    public static class LinedEditText extends EditText {
        private Rect mRect;
        private Paint mPaint;

        // we need this constructor for LayoutInflater
        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0x800000FF);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            int count = getLineCount();
            Rect r = mRect;
            Paint paint = mPaint;

            for (int i = 0; i < count; i++) {
                int baseline = getLineBounds(i, r);

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            }

            super.onDraw(canvas);
        }
    }
}
