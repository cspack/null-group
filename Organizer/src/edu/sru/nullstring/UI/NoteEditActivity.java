package edu.sru.nullstring.UI;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.NoteType;
import edu.sru.nullstring.Data.SerialBitmap;
import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;


public class NoteEditActivity extends GraphicsActivity
       implements ColorPickerDialog.OnColorChangedListener {    

	private DatabaseHelper helper = null;
	private NoteType editItem = null;
	private MyView sketchRegion = null;
	int noteID;
	
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       //setContentView(R.layout.note_edit);

       helper = OpenHelperManager.getHelper(this, DatabaseHelper.class); 
       
       // Replace stub 'page' view with a real view...
       // It's hacky but screw it, it does the job.
       
//       View fillThisView = findViewById(R.id.page); 
//       ViewGroup parent = (ViewGroup) fillThisView.getParent();
//       int index = parent.indexOfChild(fillThisView);
//       parent.removeView(fillThisView);
//       parent.addView(new MyView(this), index);
       
       
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
	//	       // Edit UI of NoteHeader
	//	       TextView noteName = (TextView) this.findViewById(R.id.text1);
	//	       noteName.setText(editItem.getTitle());
	//	       noteName.setTextSize(18.0f);
	//	       
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

//	   	// Here is where you refresh the UI for things that may have changed:
//	   	GlobalHeaderView h = (GlobalHeaderView)findViewById(R.id.topBanner);
//	   	if(h != null) h.setActivity(this);


       sketchRegion = new MyView(this);
       setContentView(sketchRegion);
       mPaint = new Paint();
       mPaint.setAntiAlias(true);
       mPaint.setDither(true);
       mPaint.setColor(0xFFFF0000);
       mPaint.setStyle(Paint.Style.STROKE);
       mPaint.setStrokeJoin(Paint.Join.ROUND);
       mPaint.setStrokeCap(Paint.Cap.ROUND);
       mPaint.setStrokeWidth(12);
       
       mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
       mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
   }
   
   private Paint       mPaint;
   private MaskFilter  mEmboss;
   private MaskFilter  mBlur;
   
   public void colorChanged(int color) {
       mPaint.setColor(color);
   }

   public class MyView extends View {
       

	   private static final float MINP = 0.25f;
	   private static final float MAXP = 0.75f;
	   
	   public Bitmap  mBitmap;
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
    	   Log.i("NoteEditActivity:pauseView", "Saving bitmap...");
    	   // save
    	   editItem.setBitmap(mBitmap);
    	   try {
			editItem.update();
	    	   Log.i("NoteEditActivity:pauseView", "Saving bitmap success.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	   Log.i("NoteEditActivity:pauseView", "Saving bitmap failed.");
		}
    	   
       }
       
       public void restoreView()
       {
           Display d = getWindowManager().getDefaultDisplay(); 

           mBitmap = editItem.getBitmap();
           
           if(mBitmap == null) 
    	   {
    	   Log.i("NoteEditActivity:restoreView", "Bitmap was null, recreated.");
    	   mBitmap = Bitmap.createBitmap(d.getWidth(), d.getHeight(), Bitmap.Config.ARGB_8888);
    	   }
           else
           {
        	   Log.i("NoteEditActivity:restoreView", "Using saved bitmap.");
        	   mBitmap = Bitmap.createBitmap(mBitmap);
           }
           // "sym-link" the bitmap objects ASAP
           editItem.setBitmap(mBitmap);
           
           
           mCanvas = new Canvas(mBitmap);
           // canvas is now linked to underlying bitmap
    	   
       }
       
       @Override
       protected void onDraw(Canvas canvas) {
           canvas.drawColor(0xFFAAAAAA);
           canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
           canvas.drawPath(mPath, mPaint);
       }
       
       private float mX, mY;
       private static final float TOUCH_TOLERANCE = 4;
       
       private void touch_start(float x, float y) {
           mPath.reset();
           mPath.moveTo(x, y);
           mX = x;
           mY = y;
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
           mPath.lineTo(mX, mY);
           // commit the path to our offscreen
           mCanvas.drawPath(mPath, mPaint);
           // kill this so we don't double draw
           mPath.reset();
           editItem.setBitmap(mBitmap);
           

           
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
   private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
   private static final int BLUR_MENU_ID = Menu.FIRST + 2;
   private static final int ERASE_MENU_ID = Menu.FIRST + 3;
   private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
       
       menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
       menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
       menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
       menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
       menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

       /****   Is this the mechanism to extend with filter effects?
       Intent intent = new Intent(null, getIntent().getData());
       intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
       menu.addIntentOptions(
                             Menu.ALTERNATIVE, 0,
                             new ComponentName(this, NotesList.class),
                             null, intent, 0, null);
       *****/
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
   



@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();

	if(sketchRegion != null)
		sketchRegion.pauseView();
	

}

protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	
	if(sketchRegion != null)
		sketchRegion.restoreView();	
	}
   
   
   
}