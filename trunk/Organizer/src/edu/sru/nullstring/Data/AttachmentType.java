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
	
	// Handles a generic attachment
	// handles binary files, android files, and markers
	
	// attachment db collection id
	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField(index=true)
	private int noteid; // every attachment is attached to a note
	
	@DatabaseField
	private String title;
	public void setTitle(String newtitle)
	{
		this.title = newtitle;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	
	
	@DatabaseField
	private String fileName = "";
	
	public void setFileName(String newfilename)
	{
		this.fileName = newfilename;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	
	@DatabaseField
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
