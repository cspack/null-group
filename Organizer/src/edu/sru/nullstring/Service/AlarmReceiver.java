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

package edu.sru.nullstring.Service;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.ReminderType;
import edu.sru.nullstring.UI.HomeActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	private static final int HELLO_ID = 1;

	public void DisplayReminderFire(Context context, ReminderType t)
	{
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		int icon = R.drawable.locadex_icon;
		CharSequence tickerText = "Reminder '" +t.getTitle()+ "' is occurring!";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		
		CharSequence contentTitle = "Locadex Reminder Occured";
		CharSequence contentText = "Reminding you: " + t.getTitle();
		Intent notificationIntent = new Intent(context,HomeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.flags |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(HELLO_ID, notification);


	}

	public ReminderType getNextReminder()
	{
		ReminderType item = null;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.SECOND, 0); // set to current minute
		c.set(Calendar.MILLISECOND, 0);
		
		ReminderType result = null;
		QueryBuilder<ReminderType, Integer> q;
		try {
			q = databaseHelper.getReminderDao().queryBuilder();
			
			long current = c.getTimeInMillis();
			
			// refresh all reminders
			
		q.orderBy(ReminderType.NEXT_FIRE_FIELD, true).where().ge(ReminderType.NEXT_FIRE_FIELD, current); // ascending
		item = q.queryForFirst();
		}catch(Exception e)
		{
			
		}
		return item;
	}
	

	/* Begin required orm lite code */
	private DatabaseHelper databaseHelper = null;

	@Override
    public void onReceive(Context context, Intent intent) {
    	Log.e("AlarmReceiver","Fired an alarm receiver!");
		// 0: Set Globals
	    databaseHelper  = OpenHelperManager.getHelper(context, DatabaseHelper.class);

        int remId = intent.getIntExtra("edu.sru.nullstring.alarmmId", 0);
        
        if(databaseHelper==null)
        {
        	Log.e("AlarmReceiver","No Database!!!");
        }else if(remId == 0)
        {
        	Log.e("AlarmReceiver","No Reminder!!!");
        }else
        {
        	ReminderType reminder;
			try {
				reminder = databaseHelper.getReminderDao().queryForId(remId);
	        	DisplayReminderFire(context.getApplicationContext(), reminder);
	        	
	        	
	        	// RIGHT HERE, fire a new alarm based on new!
			} catch (SQLException e) {
	        	Log.e("AlarmReceiver","Reminder Display Crashed!!!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        
        OpenHelperManager.releaseHelper();
        
    }
}
