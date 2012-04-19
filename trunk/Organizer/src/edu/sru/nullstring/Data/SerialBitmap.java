package edu.sru.nullstring.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        out.write(bitmapBytes, 0, bitmapBytes.length);
    }

    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1)
            byteStream.write(b);
        byte bitmapBytes[] = byteStream.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }
}