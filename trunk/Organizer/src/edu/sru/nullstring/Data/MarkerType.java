package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.PreparedQuery;

import edu.sru.nullstring.Data.CategoryType.FixedTypes;

import android.location.Location;
import android.util.Log;

public class MarkerType extends BaseDaoEnabled<MarkerType, Integer>
{
	MarkerType()
	{
		// constructor for ormlite
	}

	public enum MarkerState
	{
		Unset,
		Defined
	}
	
	public MarkerType(DatabaseHelper helper)
	{
		
		CategoryType curCat = helper.getCurrentCategory();
		try {
			this.setDao(helper.getMarkerDao());
			if(curCat.getFixedType() == FixedTypes.All)
			{
				// query unsorted category
				PreparedQuery<CategoryType> unsortedCat = helper.getCategoryDao().queryBuilder().where().eq(CategoryType.FIXED_TYPE_FIELD, FixedTypes.Unsorted).prepare();
				curCat = helper.getCategoryDao().queryForFirst(unsortedCat);
			}
		} catch (SQLException e) {
			Log.e("Locadex:MarkerType", "Database helper failed when creating MarkerType.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.catid = curCat.getID();
	}


	public static final String MARKER_ID_FIELD = "marker_id";
	public static final String CAT_ID_FIELD = "cat_id";
	public static final String TITLE_FIELD = "title";
	public static final String LAT_FIELD = "latitude";
	public static final String LON_FIELD = "longitude";
	public static final String MARKERSTATE_FIELD = "marker_state";
	public static final String LOCSTR_FIELD = "location_str";
	
	@DatabaseField(generatedId = true, columnName = MARKER_ID_FIELD)
	private int id;
	
	@DatabaseField(index = true, columnName = CAT_ID_FIELD)
	private int catid;
	
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

	
	@DatabaseField(columnName = LAT_FIELD)
	private double latitude;

	@DatabaseField(columnName = MARKERSTATE_FIELD)
	private MarkerState markerState = MarkerState.Unset;
	
	@DatabaseField(columnName = LON_FIELD)
	private double longitude;

	@DatabaseField(columnName = LOCSTR_FIELD)
	private String locationStr;


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

	public int getID() {
		return this.id;
	}

	public MarkerState getMarkerState() {
		return this.markerState;
	}
	
	public void setMarkerState(MarkerState ms) {
		 this.markerState = ms;
	}
	
	
}
