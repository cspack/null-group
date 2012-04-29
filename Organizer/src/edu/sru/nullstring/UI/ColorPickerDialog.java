/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.sru.nullstring.UI;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerDialog extends Dialog {

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener mListener;
    private int mInitialColor;

    
    private static class ColorPickerView extends View {
        private Paint mPaint;
        private Paint mCenterPaint;
        private Paint mBlack;
        private Paint mWhite;
        private final int[] mColors;
        private OnColorChangedListener mListener;
        
        ColorPickerView(Context c, OnColorChangedListener l, int color, int size) {
            super(c);
            CENTER_X = (int) (size*0.4f);
            CENTER_Y = (int) (size*0.4f);
            CENTER_RADIUS = (int) (size*0.1f);
    		Log.i("ColorPickerDialog:ColorPickerView", "cx: "+CENTER_X+", cy: "+CENTER_Y+", r: "+CENTER_RADIUS+", size: "+size+", total: "+((CENTER_X*2)+CENTER_RADIUS));
            
            mListener = l;
            mColors = new int[] {
                0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                0xFFFFFF00, 0xFFFF0000
            };
            Shader s = new SweepGradient(0, 0, mColors, null);
            
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(size*0.15f);
            
            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5);
            
            mBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBlack.setColor(0xFF000000);
            mBlack.setStyle(Paint.Style.STROKE);
            mBlack.setStrokeWidth(size*0.14f);
            
            mWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
            mWhite.setColor(0xFFFFFFFF);
            mWhite.setStyle(Paint.Style.STROKE);
            mWhite.setStrokeWidth(size*0.14f);

        }
        
        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        @Override 
        protected void onDraw(Canvas canvas) {
            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;
            
            canvas.translate(CENTER_X, CENTER_Y);
            
            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);            
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);      
            canvas.drawCircle(-r+CENTER_RADIUS, r+(CENTER_RADIUS*2)+mBlack.getStrokeWidth()*0.5f, CENTER_RADIUS, mBlack);      
            canvas.drawCircle(r-CENTER_RADIUS, r+(CENTER_RADIUS*2)+mWhite.getStrokeWidth()*0.5f, CENTER_RADIUS, mWhite);
            
            if (mTrackingCenter) {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);
                
                if (mHighlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0,
                                  CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                                  mCenterPaint);
                
                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
        }
        
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(CENTER_X*2, CENTER_Y*3);
        }

        private int CENTER_X;
        private int CENTER_Y;
        private int CENTER_RADIUS;

        private int floatToByte(float x) {
            int n = java.lang.Math.round(x);
            return n;
        }
        private int pinToByte(int n) {
            if (n < 0) {
                n = 0;
            } else if (n > 255) {
                n = 255;
            }
            return n;
        }
        
        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }
        
        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }
            
            float p = unit * (colors.length - 1);
            int i = (int)p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);
            
            return Color.argb(a, r, g, b);
        }
        
        private int rotateColor(int color, float rad) {
            float deg = rad * 180 / 3.1415927f;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            
            ColorMatrix cm = new ColorMatrix();
            ColorMatrix tmp = new ColorMatrix();

            cm.setRGB2YUV();
            tmp.setRotate(0, deg);
            cm.postConcat(tmp);
            tmp.setYUV2RGB();
            cm.postConcat(tmp);
            
            final float[] a = cm.getArray();

            int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
            int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
            int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);
            
            return Color.argb(Color.alpha(color), pinToByte(ir),
                              pinToByte(ig), pinToByte(ib));
        }
        
        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;
            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;
            boolean inBlack = y > r+CENTER_RADIUS && x < 0;
            boolean inWhite = y > r+CENTER_RADIUS && x > 0;
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else {
                        if (inBlack) {
                            mCenterPaint.setColor(0xFF000000);
	                        invalidate();
                        }
                        else if (inWhite) {
                            mCenterPaint.setColor(0xFFFFFFFF);
	                        invalidate();
                        }
                        else {
	                        float angle = (float)java.lang.Math.atan2(y, x);
	                        // need to turn angle [-PI ... PI] into unit [0....1]
	                        float unit = angle/(2*PI);
	                        if (unit < 0) {
	                            unit += 1;
	                        }
	                        mCenterPaint.setColor(interpColor(mColors, unit));
	                        invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            mListener.colorChanged(mCenterPaint.getColor());
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }

    public ColorPickerDialog(Context context,
                             OnColorChangedListener listener,
                             int initialColor) {
        super(context);
        
        mListener = listener;
        mInitialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display d = getWindow().getWindowManager().getDefaultDisplay();
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color) {
                mListener.colorChanged(color);
                dismiss();
            }
        };
        int size;
        if(((double)d.getWidth()/(double)d.getHeight()) >= 1)
        	size=(int) (d.getHeight()*0.7f);
        else
        	size=d.getWidth();
        setContentView(new ColorPickerView(getContext(), l, mInitialColor, size));
        setTitle("Pick a Color");
    }
}
