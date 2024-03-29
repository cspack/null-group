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
import java.util.List;
import java.util.TimeZone;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import edu.sru.nullstring.LocadexApplication;
import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.LatLonPoint;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.ReminderType;
import edu.sru.nullstring.UI.HomeActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocadexService extends OrmLiteBaseService<DatabaseHelper> {

	private static long nextAlarmTime;
	private static boolean locationRunning = false;

	private DatabaseHelper helper = null;
	private Handler refresher = new Handler();
	private Runnable refresherRunnable;
	private ReminderType item = null;
	private LocationManager locationManager = null;
	private Location dummyLocation = new Location("Locadex");
	private PendingIntent operation;

	public ReminderType getNextReminder() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.SECOND, 0); // set to current minute
		c.set(Calendar.MILLISECOND, 0);

		ReminderType result = null;
		QueryBuilder<ReminderType, Integer> q;
		try {
			q = helper.getReminderDao().queryBuilder();

			long current = c.getTimeInMillis();

			// refresh all reminders

			q.orderBy(ReminderType.NEXT_FIRE_FIELD, true).where()
					.ge(ReminderType.NEXT_FIRE_FIELD, current); // ascending
			item = q.queryForFirst();
			result = item;
		} catch (Exception e) {

		}
		return result;
	}

	public void checkForNextFire() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.set(Calendar.SECOND, 0); // set to current minute
		c.set(Calendar.MILLISECOND, 0);

		// Current minute:
		QueryBuilder<ReminderType, Integer> q;
		try {
			item = getNextReminder();

			if (c.getTimeInMillis() / 60000L > nextAlarmTime / 60000L) {
				if (operation != null)
					alarmManager.cancel(operation);
			}

			if (item != null) {
				long nowInMinutes = Math.round(c.getTimeInMillis() / 60000.0);
				long itemInMinutes = Math.round(item.nextFire / 60000.0);

				// Log.i("LocadexService", "Now: " + nowInMinutes + ", Item: " +
				// itemInMinutes);

				// i has result
				// test, try forcing alarm intent every time.
				if (false)
				// if( nowInMinutes == itemInMinutes )
				{
					// Log.d("LocadexService",
					// "Not setting alarm, firing event NOW.");
					// OMG its fire time!
					DisplayReminderFire(item);
				} else {
					// clear
					if (itemInMinutes != nextAlarmTime) {
						// Log.i("Locadex Service", "Next alarm time: " +
						// nextAlarmTime + ", nextFire: " + item.nextFire);
						if (operation != null) {
							alarmManager.cancel(operation);
						}

						// Log.i("LocadexService", "Defined an alarm!");
						Intent intent = new Intent(this, AlarmReceiver.class);
						intent.putExtra("edu.sru.nullstring.alarmId",
								item.getID());
						operation = PendingIntent.getBroadcast(this, 0, intent,
								0);
						nextAlarmTime = itemInMinutes;
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								item.nextFire, operation);
					}
				}

			} else {
				// no results
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Context context = this;
	private static final int HELLO_ID = 1;

	public void DisplayReminderFire(ReminderType t) {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.locadex_icon;
		CharSequence tickerText = "Reminder '" + t.getTitle()
				+ "' is occurring!";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "Locadex Reminder Occured";
		CharSequence contentText = "Reminding you: " + t.getTitle();
		Intent notificationIntent = new Intent(context, HomeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.flags |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(HELLO_ID, notification);

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		nextAlarmTime = 0;
	}

	private AlarmManager alarmManager;

	private LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {

			//Log.i("LocadexService", "Location update recieved.");
			// TODO Auto-generated method stub
			if (locationRunning) {
				/*Log.i("LocadexService",
						"Location updates are enabled, handling it.");*/
				// check fixes
				// 1: Get a list of all location reminders
				List<ReminderType> locations = null;
				try {
					locations = helper.getReminderDao().queryBuilder().where()
							.ne(ReminderType.MARKER_ID_FIELD, 0).query();

					// 2: Query distance of all to location
					Dao<MarkerType, Integer> markerDao = helper.getMarkerDao();

					for (ReminderType t : locations) {

						MarkerType marker = markerDao.queryForId(t.markerId);
						if (marker != null) {
							LatLonPoint pt = marker.getLocation();
							dummyLocation.setLatitude(pt.getLatitude());
							dummyLocation.setLongitude(pt.getLongitude());
							/*Log.i("LocadexService",
									"Distance between locations:"
											+ location
													.distanceTo(dummyLocation));*/
							if (location.distanceTo(dummyLocation) < 1000) {
								// BAM!
								DisplayReminderFire(t);
							}

						}
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			if (locationRunning) {
				// check fixes
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int result = super.onStartCommand(intent, flags, startId);

		// 0: Set Globals
		helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// 1: Create Reminder Refresher for every minute
		// Setup UI Refresher

		refresherRunnable = new Runnable() {
			private Calendar cal = Calendar.getInstance();

			public void run() {
				// refresh
				checkForNextFire();

				// 2: Create Location Update Checker, based on Flag
				LocadexApplication loc = (LocadexApplication) context
						.getApplicationContext();
				if (loc.useLocation) {
					if (!locationRunning) {
						Log.i("LocadexService", "Enabling GPS updates!");
						// start

						Criteria crit = new Criteria();
						crit.setAccuracy(Criteria.ACCURACY_FINE);
						String provider = locationManager.getBestProvider(crit,
								true);

						// set to last gps
						Location lastGPSLoc = locationManager
								.getLastKnownLocation(provider);
						// fire event
						locationListener.onLocationChanged(lastGPSLoc);

						locationManager.requestLocationUpdates(provider,
								30 * 1000, 100, locationListener);

						locationRunning = true;
					}
				} else {
					if (locationRunning) {
						// stop
						Log.i("LocadexService", "Disabling GPS updates!");

						locationManager.removeUpdates(locationListener);
						locationRunning = false;
					}
				}

				// 3: Run it again.

				final long startTime = System.currentTimeMillis();
				cal.setTime(new Date(startTime));
				cal.add(Calendar.MINUTE, 1);
				cal.set(Calendar.SECOND, 0);
				refresher
						.postDelayed(this, cal.getTime().getTime() - startTime);
			}
		};

		refresher.post(refresherRunnable);

		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
