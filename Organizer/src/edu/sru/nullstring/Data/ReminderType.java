package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
	

	public static final String REMINDER_ID_FIELD = "reminder_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";
	public static final String REMINDER_STATE_FIELD = "state";
	public static final String REMINDER_TYPE_FIELD = "reminder_type";
	public static final String NEXT_FIRE_FIELD = "next_fire";

	
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
	
	// array that passes to the boolean type
	private final boolean[] repeatDays = new boolean[]{repeatSun, repeatMon, repeatTue, repeatWed, repeatThu, repeatFri, repeatSat};
	
	// When Quick use:
	
	
	// this processes all the stored information to get the next time it fires
	public long calculateNextFire()
	{
		Date today = new Date();
        Calendar todayCal = Calendar.getInstance();  
        todayCal.setTime(today);  

        Date fireTime;
		long temp = 0;
		switch(this.reminderType)
		{
		case Quick:
			// date that quick is valid for [when created] 
			// 		 fireTimeYear
			// 		 fireTimeMon
			// 		 fireTimeDay
			fireTime = new Date(fireTimeYear, fireTimeMonth, fireTimeDay, fireTimeHour, fireTimeMinute);
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
			
			fireTime = new Date(fireTimeYear, fireTimeMonth, fireTimeDay, fireTimeHour, fireTimeMinute);
			temp = fireTime.getTime();
			
			// repeating?
			if(useRepeat)
			{

				// reset calendar
				todayCal.setTime(today);  
			
				for(int checkWeek = 0; checkWeek < 7; checkWeek++ )
				{
					int weekDay = todayCal.DAY_OF_WEEK;
					if(repeatDays[weekDay])
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
