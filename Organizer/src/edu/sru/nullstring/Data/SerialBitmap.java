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

package edu.sru.nullstring.Data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class SerialBitmap implements Serializable {

    public Bitmap bitmap = null;

    // for ORMLite!
    public SerialBitmap()
    {
    	
    	// commented because it's ok to leave bitmap null, but not the FUCKING OBJECT
    	
    	// set to default screen size, can't read in this context tho.
    	// remember: this is a raw Serializable class, not an android class
    	// this.bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
    }
    
    // TODO: Finish this constructor
    public SerialBitmap(Bitmap b) {
        bitmap = b;
    }
    
    // Converts the Bitmap into a byte array for serialization
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
  	   
    	Log.i("SerialBitmap", "Writing serialized bitmap.");
    	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    	try
    	{
    		bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
    	}
    	catch(Exception ex)
    	{
    		// well.. .it's gonna be null
    		Log.i("SerialBitmap", "Write encode failed, Bitmap won't be stored.");
    	}
    	byte bitmapBytes[] = byteStream.toByteArray();
    	out.write(bitmapBytes, 0, bitmapBytes.length);
    }

    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

    	Log.i("SerialBitmap", "Reading serialized bitmap.");

    	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    	int b;
    	while((b = in.read()) != -1)
    		byteStream.write(b);
    	byte bitmapBytes[] = byteStream.toByteArray();
    	try {

    		bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    		// make mutable
    		bitmap = convertToMutable(bitmap);
    		Log.i("SerialBitmap", "Decode succeded.");
    	}
    	catch(Exception ex)
    	{
    		// well.. .it's gonna be null
    		Log.i("SerialBitmap", "Decode failed, Bitmap will remain null.");
    	}
    }


public static Bitmap convertToMutable(Bitmap imgIn) {
    try {
        //this is the file going to use temporally to save the bytes. 
        // This file will not be a image, it will store the raw image data.
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

        //Open an RandomAccessFile
        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        //into AndroidManifest.xml file
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        // get the width and height of the source bitmap.
        int width = imgIn.getWidth();
        int height = imgIn.getHeight();
        Config type = imgIn.getConfig();

        //Copy the byte to the file
        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
        imgIn.copyPixelsToBuffer(map);
        //recycle the source bitmap, this will be no longer used.
        imgIn.recycle();
        System.gc();// try to force the bytes from the imgIn to be released

        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
        imgIn = Bitmap.createBitmap(width, height, type);
        map.position(0);
        //load it back from temporary 
        imgIn.copyPixelsFromBuffer(map);
        //close the temporary file and channel , then delete that also
        channel.close();
        randomAccessFile.close();

        // delete the temp file
        file.delete();

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } 

    return imgIn;
}

}