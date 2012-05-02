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

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

import edu.sru.nullstring.Data.CategoryType.FixedTypes;


import android.location.Location;
import android.util.Log;

@DatabaseTable (tableName="reminders")
public class ReminderType extends BaseDaoEnabled<ReminderType, Integer> {
	
	ReminderType()
	{
	}

	
	public static String GetDisplayTimeString(ReminderType t)
	{
		long time = t.calculateNextFire();
		
		Date compareTo = new Date(time);
		Date nowDate = new Date();
		long derivTime = compareTo.getTime() - nowDate.getTime();
		Calendar cal = Calendar.getInstance();
		Date derivDate = new Date(derivTime);
		
		// base on GMT
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(derivDate);
		StringBuilder build = new StringBuilder();
		
		long numDays = derivTime / (1000 * 60 * 60 * 24);
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int mins = cal.get(Calendar.MINUTE);
		
		//Log.i("ReminderType:DisplayTime", "Seconds until firing: " + (derivTime / 1000));

		if(t.getReminderType() == ReminderTypes.Location || (t.advancedUseLocation && !t.advancedUseTime))
		{
			return ("Location-aware only reminder");
		}

		if(derivTime < 0)
		{
			return ("Reminder completed");
		}
		
		boolean hasContent = false;
		if(mins >= 0)
		{
			build.append(Integer.toString(mins) + " minute" + (mins != 1 ? "s":""));
			hasContent = true;
		}

		if(hours > 0)
		{
			build.insert(0, Integer.toString(hours) + " hour" + (hours != 1 ? "s":"") + (hasContent ? " and " : ""));
			hasContent = true;
		}

		if(numDays > 0)
		{
			build.insert(0, Long.toString(numDays) + " day" + (numDays != 1 ? "s":"") + (hasContent ? " and " : ""));
			hasContent = true;
		}

		build.insert(0, "In ");
		return build.toString();
	}
	
	public ReminderType(DatabaseHelper helper)
	{
		CategoryType curCat = helper.getCurrentCategory();
		try {
			this.setDao(helper.getReminderDao());
			if(curCat.getFixedType() == FixedTypes.All)
			{
				// query unsorted category
				PreparedQuery<CategoryType> unsortedCat = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, FixedTypes.Unsorted).prepare();
				curCat = helper.getCategoryDao().queryForFirst(unsortedCat);
			}
		} catch (SQLException e) {
			Log.e("Locadex", "Database helper failed when creating ReminderType.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.catid = curCat.getID();
	}
	
	
	public boolean getDayOfWeek(int weekday)
	{
		boolean ret = false;
		switch(weekday)
		{
		case 0:	ret = repeatSun;break;
		case 1:	ret = repeatMon;break;
		case 2:	ret = repeatTue;break;
		case 3:	ret = repeatWed;break;
		case 4:	ret = repeatThu;break;
		case 5:	ret = repeatFri;break;
		case 6:	ret = repeatSat;break;
		}
		return ret;
	}

	public static final String REMINDER_ID_FIELD = "reminder_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";
	public static final String REMINDER_STATE_FIELD = "state";
	public static final String REMINDER_TYPE_FIELD = "reminder_type";
	public static final String NEXT_FIRE_FIELD = "next_fire";
	public static final String MARKER_ID_FIELD = "markerId";

	
	public int getID()
	{
		return id;
	}
	
	public static enum ReminderState 
	{
		Loading,
		Completed,
		Active,
		Disabled
	}
	
	public static enum ReminderTypes
	{
		Location,
		Quick,
		DateTime,
		Advanced
	}
	
	@DatabaseField(generatedId = true, columnName = REMINDER_ID_FIELD)
	private int id;
	
	@DatabaseField(index = true, columnName = CAT_ID_FIELD)
	private int catid;

	@DatabaseField(columnName = NEXT_FIRE_FIELD)
	public long nextFire;

	// Quick flags
	@DatabaseField
	public boolean quickUseRelativeTime = true;
	
	// Advanced flags
	@DatabaseField
	public boolean advancedUseLocation = false;
	@DatabaseField
	public boolean advancedUseTime = false;
	
	// When Quick & DateTime use:
	@DatabaseField
	public int fireTimeHour = 0; // 24 hours
	@DatabaseField
	public int fireTimeMinute = 0;
	@DatabaseField
	public int fireTimeMonth = 0;
	@DatabaseField
	public int fireTimeDay = 0;
	@DatabaseField
	public int fireTimeYear = 0;

	// swap between date and repeat weekdays with this flag
	@DatabaseField
	public boolean useRepeat = false;

	// repeat
	@DatabaseField
	public boolean repeatMon = false;
	@DatabaseField
	public boolean repeatTue = false;
	@DatabaseField
	public boolean repeatWed = false;
	@DatabaseField
	public boolean repeatThu = false;
	@DatabaseField
	public boolean repeatFri = false;
	@DatabaseField
	public boolean repeatSat = false;
	@DatabaseField
	public boolean repeatSun = false;

	// associated marker
	@DatabaseField
	public int markerId = 0;
	
	// When Quick use:
	
	
	// this processes all the stored information to get the next time it fires
	public long calculateNextFire()
	{
		long fire = this.nextFire;		

		Date today = new Date();
        Calendar todayCal = Calendar.getInstance();  
        todayCal.setTime(today);
		todayCal.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date fireTime;
		long temp = 0;
		switch(this.reminderType)
		{
		case Quick:
			// date that quick is valid for [when created] 
			// 		 fireTimeYear
			// 		 fireTimeMon
			// 		 fireTimeDay
			
			//Log.e("ReminderType", "This reminder is set to Quick, creating nextFireTime from it.");
			fireTime = new Date(fireTimeYear - 1900, fireTimeMonth, fireTimeDay, fireTimeHour, fireTimeMinute);
			temp = fireTime.getTime();

			break;
		case Advanced: // handle advanced same as date time w/ conditional 
		case DateTime:
			
			// if this is advanced & disabled time, then quit out
			if(this.reminderType == ReminderTypes.Advanced)
			{
				// this is because, if this is fault, only location may be enabled
				if(!this.advancedUseTime)
					break;
				
				// location is NOT THE SCOPE OF NEXTFIRETIME
			}
			
			fireTime = new Date(fireTimeYear - 1900, fireTimeMonth, fireTimeDay, fireTimeHour, fireTimeMinute);
			temp = fireTime.getTime();
			
			// repeating?
			if(useRepeat)
			{

				// reset calendar
				todayCal.setTime(today);  

				// test one week
				for(int checkWeek = 0; checkWeek < 7; checkWeek++ )
				{
					todayCal.setTimeZone(TimeZone.getDefault());
					int weekDay = todayCal.get(Calendar.DAY_OF_WEEK);
					todayCal.setTimeZone(TimeZone.getTimeZone("GMT"));
					if(getDayOfWeek(weekDay))
					{
						// found a day.. done.
						todayCal.set(Calendar.HOUR_OF_DAY, fireTimeHour);
						todayCal.set(Calendar.MINUTE, fireTimeMinute);
						temp = todayCal.getTime().getTime();
						break;
					}
					
					todayCal.add(Calendar.DAY_OF_MONTH, 1);
				}
				
			}
			

				
			break;
		case Location:
			// location is not in the scope of nextFire
			// no time enabled.
			temp = 0;
			break;
		}
		
		nextFire = temp;

		if(fire != nextFire)
		{
			// update db
			try {
				update();
			} catch (SQLException e) {
				// you need a DB helper, this will never update :'(
			}
		}
		
		return nextFire;
	}
	
	@DatabaseField(columnName = TITLE_FIELD)
	private String title;
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	@DatabaseField(columnName = REMINDER_STATE_FIELD)
	private ReminderState state = ReminderState.Loading;

	@DatabaseField(columnName = REMINDER_TYPE_FIELD)
	private ReminderTypes reminderType = ReminderTypes.Quick;

	public ReminderTypes getReminderType()
	{
		return reminderType;
	}
	public void setReminderType(ReminderTypes t)
	{
		reminderType = t;
		calculateNextFire();
	}

	public int getCategoryID()
	{
		return this.catid;
	}
	public void setCategory(int cat)
	{
		this.catid = cat;
	}
	
	public CategoryType getCategory(DatabaseHelper h)
	{
		CategoryType result = null;
		try {
			List<CategoryType> allres;
			allres = h.getCategoryDao().queryForEq(CategoryType.CAT_ID_FIELD, this.catid);

			if(allres.size() > 0)
			{
				result = allres.get(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	

	
}
