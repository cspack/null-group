package edu.sru.nullstring.Data;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import edu.sru.nullstring.Data.AttachmentType;
import edu.sru.nullstring.Data.CategoryType.FixedTypes;

import edu.sru.nullstring.R;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "locadex.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 7;

	// the DAO object we use to access the SimpleData table
	// private Dao<SimpleData, Integer> simpleDao = null;
	// private RuntimeExceptionDao<SimpleData, Integer> simpleRuntimeDao = null;

	private Dao<AttachmentType, Integer> attachmentDao = null;
	private Dao<CategoryType, Integer> categoryDao = null;
	private Dao<ChecklistType, Integer> checklistDao = null;
	private Dao<ChecklistItemType, Integer> checklistItemsDao = null;
	private Dao<MarkerType, Integer> markerDao = null;
	private Dao<NoteType, Integer> noteDao = null;
	private Dao<ReminderType, Integer> reminderDao = null;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}
	
	
	private void BuildFixedCategories()
	{

		try {
		
		Dao<CategoryType, Integer> dao = this.getCategoryDao();	
		
		if(dao.queryForEq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.All).size() == 0)
		{
		
			// Generate default categories
			CategoryType all = new CategoryType(CategoryType.FixedTypes.All);
			all.setTitle("All Categories"); // TODO: Make string literal
			dao.create(all);
			
		}

		if(dao.queryForEq(CategoryType.FIXED_TYPE_FIELD, CategoryType.FixedTypes.Unsorted).size() == 0)
		{
		
			// Generate default categories
			CategoryType uns = new CategoryType(CategoryType.FixedTypes.Unsorted);
			uns.setTitle("Unsorted"); // TODO: Make string literal
			dao.create(uns);
			
		}
		
		} catch (SQLException e) {
			Log.e("Locadex","Generation of fixed categories failed.");
			e.printStackTrace();
		}


	}
	

	private void CreateAllTables() throws SQLException
	{
		TableUtils.createTable(connectionSource, AttachmentType.class);

		
		// -- CATEGORY --
		TableUtils.createTable(connectionSource, CategoryType.class);

		
		TableUtils.createTable(connectionSource, ChecklistType.class);
		TableUtils.createTable(connectionSource, ChecklistItemType.class);
		TableUtils.createTable(connectionSource, MarkerType.class);
		TableUtils.createTable(connectionSource, NoteType.class);
		TableUtils.createTable(connectionSource, ReminderType.class);
	}
	
	private void DropAllTables() throws SQLException
	{
		TableUtils.dropTable(connectionSource, AttachmentType.class, true);
		TableUtils.dropTable(connectionSource, CategoryType.class, true);
		TableUtils.dropTable(connectionSource, ChecklistType.class, true);
		TableUtils.dropTable(connectionSource, ChecklistItemType.class, true);
		TableUtils.dropTable(connectionSource, MarkerType.class, true);
		TableUtils.dropTable(connectionSource, NoteType.class, true);
		TableUtils.dropTable(connectionSource, ReminderType.class, true);
	}
	
	// Override the on db open to make sure null category exists.
	/*
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		
		// Verify fixed categories are created
		// BuildFixedCategories();
		
	}
*/

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		
		Log.e("Locadex","Creating database for the first time.");
		
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			CreateAllTables();
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

		/*
		// here we try inserting data in the on-create as a test
		RuntimeExceptionDao<SimpleData, Integer> dao = getSimpleDataDao();
		long millis = System.currentTimeMillis();
		// create some entries in the onCreate
		SimpleData simple = new SimpleData(millis);
		dao.create(simple);
		simple = new SimpleData(millis + 1);
		dao.create(simple);
		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
		*/
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			
			// So far we don't need to modify the data at all....
			// TODO: Read more into querying DAO of an out of date database
			
			DropAllTables();
			CreateAllTables();
			
			/*
			TableUtils.dropTable(connectionSource, CategoryType.class, true);
			TableUtils.dropTable(connectionSource, ChecklistType.class, true);
			TableUtils.dropTable(connectionSource, MarkerType.class, true);
			TableUtils.dropTable(connectionSource, NoteType.class, true);
			TableUtils.dropTable(connectionSource, ReminderType.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
			*/
		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "Database upgrade failed", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<AttachmentType, Integer> getAttachmentDao() throws SQLException {
		if (attachmentDao == null) {
			attachmentDao = getDao(AttachmentType.class);
		}
		return attachmentDao;
	}	

	public Dao<CategoryType, Integer> getCategoryDao() throws SQLException {
		if (categoryDao == null) {
			categoryDao = getDao(CategoryType.class);
			BuildFixedCategories(); // make sure fixed categories exist.
		}
		
		return categoryDao;
	}

	public Dao<ChecklistType, Integer> getChecklistDao() throws SQLException {
		if (checklistDao == null) {
			checklistDao = getDao(ChecklistType.class);
		}
		return checklistDao;
	}
	
	public Dao<ChecklistItemType, Integer> getChecklistItemDao() throws SQLException {
		if (checklistItemsDao == null) {
			checklistItemsDao = getDao(ChecklistItemType.class);
		}
		return checklistItemsDao;
	}

	public Dao<MarkerType, Integer> getMarkerDao() throws SQLException {
		if (markerDao == null) {
			markerDao = getDao(MarkerType.class);
		}
		return markerDao;
	}

	public Dao<NoteType, Integer> getNoteDao() throws SQLException {
		if (noteDao == null) {
			noteDao = getDao(NoteType.class);
		}
		return noteDao;
	}

	public Dao<ReminderType, Integer> getReminderDao() throws SQLException {
		if (reminderDao == null) {
			reminderDao = getDao(ReminderType.class);
		}
		return reminderDao;
	}
	

	public String getCategoryString(CategoryType c)
	{
		return c == null ? "Unknown" : c.getTitle();
	}
	
	public String getCategoryString(ChecklistType c)
	{
		return getCategoryString(c.getCategory(this));
	}

	public String getCategoryString(MarkerType c)
	{
		return getCategoryString(c.getCategory(this));
	}

	public String getCategoryString(NoteType c)
	{
		return getCategoryString(c.getCategory(this));
	}

	public String getCategoryString(ReminderType c)
	{
		return getCategoryString(c.getCategory(this));
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		attachmentDao = null;
		categoryDao = null;
		checklistDao = null;
		markerDao = null;
		noteDao = null;
		reminderDao = null;
	}


	public boolean MoveAllCategoryItems(int OldCatID, int NewCatID) throws SQLException
	{
		Dao<ChecklistType, Integer> checkDao = this.getChecklistDao();
		List<ChecklistType> cresults = checkDao.queryForEq(ChecklistType.CAT_ID_FIELD, OldCatID);
		for(ChecklistType t : cresults)
		{
			t.setCategory(NewCatID);
			t.update();
		}
		
		Dao<MarkerType, Integer> markerDao = this.getMarkerDao();
		List<MarkerType> mresults = markerDao.queryForEq(MarkerType.CAT_ID_FIELD, OldCatID);
		for(MarkerType t : mresults)
		{
			t.setCategory(NewCatID);
			t.update();
		}

		Dao<NoteType, Integer> noteDao = this.getNoteDao();
		List<NoteType> nresults = noteDao.queryForEq(NoteType.CAT_ID_FIELD, OldCatID);
		for(NoteType t : nresults)
		{
			t.setCategoryID(NewCatID);
			t.update();
		}

		Dao<ReminderType, Integer> reminderDao = this.getReminderDao();
		List<ReminderType> rresults = reminderDao.queryForEq(ReminderType.CAT_ID_FIELD, OldCatID);
		for(ReminderType t : rresults)
		{
			t.setCategory(NewCatID);
			t.update();
		}
		
		return true;		
	}
	
	public CategoryType getCurrentCategory() {
		
		try {
			List<CategoryType> sel = this.categoryDao.queryForEq(CategoryType.IS_CURRENT_FIELD, true);
			if(sel.size() > 0)
			{
				return sel.get(0);
			}
			else
			{

				sel = this.categoryDao.queryForEq(CategoryType.FIXED_TYPE_FIELD, FixedTypes.Unsorted);
				CategoryType unsorted = sel.get(0);
				unsorted.setCurrent(true);
				return unsorted;
			}
		} catch (Exception e) {
			return null;
		}

	}
	
}