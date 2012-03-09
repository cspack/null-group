package edu.sru.nullstring.Data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class AttachmentType extends BaseDaoEnabled<AttachmentType, Integer> {
	AttachmentType(){}

	public AttachmentType(Dao<AttachmentType, Integer> dao)
	{
		this.setDao(dao);
	}
	
	public static final String ATTACH_ID_FIELD = "attach_id";
	public static final String NOTE_ID_FIELD = "note_id";
	public static final String TITLE_FIELD = "title";
	public static final String FILE_NAME_FIELD = "file_name";
	public static final String FILE_PATH_FIELD = "file_path";
	
	// Handles a generic attachment
	// handles binary files, android files, and markers
	
	// attachment db collection id
	@DatabaseField(generatedId=true, columnName = ATTACH_ID_FIELD)
	private int id;
	
	@DatabaseField(index=true, columnName = NOTE_ID_FIELD)
	private int noteid; // every attachment is attached to a note
	
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
	
	
	
	@DatabaseField(columnName = FILE_NAME_FIELD)
	private String fileName = "";
	
	public void setFileName(String newfilename)
	{
		this.fileName = newfilename;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	
	@DatabaseField(columnName = FILE_PATH_FIELD)
	private String filePath;
	
	public void setFilePath(String newfilepath)
	{
		this.filePath = newfilepath;
	}

	public String getFilePath()
	{
		return this.filePath;
	}
	
	
}
