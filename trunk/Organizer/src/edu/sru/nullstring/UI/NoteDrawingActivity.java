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

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.NoteAdapter;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.Data.SerialBitmap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class NoteDrawingActivity extends GraphicsActivity
	implements ColorPickerDialog.OnColorChangedListener {    

	private DatabaseHelper helper = null;
    private MyView sketchRegion = null;
    private NoteEditActivity act = null;
	int noteID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        act = (NoteEditActivity) this.getParent();
        helper = act.helper;
        
		Bundle extras = act.getIntent().getExtras();        
		if(extras != null)
		{
			// Setup database helper and object early on
			noteID = extras.getInt("edu.sru.nullstring.noteId");
			if(noteID != 0)
			{

				try {
					Log.i("NoteEditActivity", "open attempt with id " + Integer.toString(noteID));
					act.editItem = helper.getNoteDao().queryForId(noteID);
					Log.i("Locadex", "Success opening item for editing");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.e("Locadex", "Failed to open item for editing");
					e.printStackTrace();
				}             
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
	       
		sketchRegion = new MyView(this);
		setContentView(sketchRegion);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(10);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}

	private Paint       mPaint;
	private MaskFilter  mEmboss;
	private MaskFilter  mBlur;

	public void colorChanged(int color) {
		mPaint.setColor(color);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(sketchRegion != null)
			sketchRegion.pauseView();
	}

	protected void onResume() {
		super.onResume();
		if(sketchRegion != null)
			sketchRegion.restoreView();     
	}

	public class MyView extends View {
		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;

		public Bitmap	mBitmap;
		private Canvas  mCanvas;
		private Path    mPath;
		private Paint   mBitmapPaint;

		public MyView(Context c) {
			super(c);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			restoreView();
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}


		public void pauseView()
		{
			Log.i("NoteEditActivity:onPause", "Saving bitmap...");
			// save
			act.editItem.setBitmap(mBitmap);
			try {
				act.editItem.update();
				Log.i("NoteEditActivity:onPause", "Saving bitmap success.");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("NoteEditActivity:onPause", "Saving bitmap failed.");
			}

		}

		public void restoreView()
		{
			Display d = getWindowManager().getDefaultDisplay(); 

			//mBitmap = Bitmap.createBitmap(act.editItem.getBitmap());
			Bitmap b = act.editItem.getBitmap();
			//mBitmap = ;
			if(b == null) 
			{
				Log.i("NoteEditActivity:restoreView", "Bitmap was null, recreated.");
				mBitmap = Bitmap.createBitmap(d.getWidth(), d.getHeight(), Bitmap.Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			else
			{
				Log.i("NoteEditActivity:restoreView", "Using saved bitmap.");
				//mBitmap = Bitmap.createBitmap(mBitmap);

				mBitmap = Bitmap.createBitmap(d.getWidth(), d.getHeight(), Bitmap.Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
				mCanvas.drawBitmap(b, 0, 0, null);
			}
			
			// "sym-link" the bitmap objects ASAP
			act.editItem.setBitmap(mBitmap);

			//mCanvas = new Canvas(mBitmap);
			// canvas is now linked to underlying bitmap

		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0xFFAAAAAA);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, mPaint);
		}

		private float mX, mY, sX, sY;
		private static final float TOUCH_TOLERANCE = 1;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
			sX = x;
			sY = y;
		}
		
		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
				mX = x;
				mY = y;
			}
		}
		
		private void touch_up() {
			if(sX == mX && sY == mY)
				mPath.lineTo(mX+1, mY+1);
			else
				mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}

	   private static final int COLOR_MENU_ID = Menu.FIRST;
	   private static final int STROKE_MENU_ID = Menu.FIRST+1;
	   private static final int EMBOSS_MENU_ID = Menu.FIRST + 2;
	   private static final int BLUR_MENU_ID = Menu.FIRST + 3;
	   private static final int ERASE_MENU_ID = Menu.FIRST + 4;
	   private static final int SRCATOP_MENU_ID = Menu.FIRST + 5;

	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       super.onCreateOptionsMenu(menu);

	       menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
	       menu.add(0, STROKE_MENU_ID, 0, "Width").setShortcut('4', 'w');
	       menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('5', 'e');
	       menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('6', 'b');
	       menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('7', 'z');
	       menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('8', 't');

	       return true;
	   }
	   
	   @Override
	   public boolean onPrepareOptionsMenu(Menu menu) {
	       super.onPrepareOptionsMenu(menu);
	       return true;
	   }
	   
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       mPaint.setXfermode(null);
	       mPaint.setAlpha(0xFF);

	       switch (item.getItemId()) {
	           case COLOR_MENU_ID:
	               new ColorPickerDialog(this, this, mPaint.getColor()).show();
	               return true;
	           case STROKE_MENU_ID:
	        	   final AlertDialog.Builder alert = new AlertDialog.Builder(this);

	        	   final SeekBar slider = new SeekBar(this);
	        	   slider.setMax(60);
	        	   slider.setProgress((int)mPaint.getStrokeWidth());
	        	   slider.setPadding(10, 0, 10, 10);

	        	   final TextView progressText = new TextView(this);
	        	   progressText.setText(""+slider.getProgress());
	        	   progressText.setGravity(1);
	        	   
	        	   OnSeekBarChangeListener l = new OnSeekBarChangeListener(){
	        		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	        		    	progressText.setText(""+ slider.getProgress());
	        		    }

	        		    public void onStartTrackingTouch(SeekBar seekBar) {
	        		    	progressText.setText(""+ slider.getProgress());
	        		    }

	        		    public void onStopTrackingTouch(SeekBar seekBar) {
	        		    	progressText.setText(""+ slider.getProgress());
	        		    }
	        	   };

	        	   slider.setOnSeekBarChangeListener(l);

	        	   alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        	   {
	        		   public void onClick(DialogInterface dialog, int whichButton)
	        		   {
	        			   // edit the title of the note
	        			   float value = slider.getProgress();
	        			   mPaint.setStrokeWidth(value);
	        		   }
	        	   });

	        	   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        		   public void onClick(DialogInterface dialog, int whichButton) {
	        			   // Canceled.
	        		   }
	        	   });
	        	   
	        	   LinearLayout ll = new LinearLayout(this);
	        	   ll.setOrientation(1);
	        	   ll.addView(slider);
	        	   ll.addView(progressText);
	        	   alert.setMessage("Stroke Width: ");
	        	   alert.setView(ll);
	        	   alert.show();
	               return true;
	           case EMBOSS_MENU_ID:
	               if (mPaint.getMaskFilter() != mEmboss) {
	                   mPaint.setMaskFilter(mEmboss);
	               } else {
	                   mPaint.setMaskFilter(null);
	               }
	               return true;
	           case BLUR_MENU_ID:
	               if (mPaint.getMaskFilter() != mBlur) {
	                   mPaint.setMaskFilter(mBlur);
	               } else {
	                   mPaint.setMaskFilter(null);
	               }
	               return true;
	           case ERASE_MENU_ID:
	               mPaint.setXfermode(new PorterDuffXfermode(
	                                                       PorterDuff.Mode.CLEAR));
	               return true;
	           case SRCATOP_MENU_ID:
	               mPaint.setXfermode(new PorterDuffXfermode(
	                                                   PorterDuff.Mode.SRC_ATOP));
	               mPaint.setAlpha(0x80);
	               return true;
	       }
	       return super.onOptionsItemSelected(item);
	   }
	   
}
