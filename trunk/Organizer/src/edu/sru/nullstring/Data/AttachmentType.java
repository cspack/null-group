package edu.sru.nullstring.Data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class AttachmentType {
	// Handles a generic attachment
	// handles binary files, android files, and markers
	
	// attachment db collection id
	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField(index=true)
	private int noteid; // every attachment is attached to a note
	
	@DatabaseField
	private String title;
	
	@DatabaseField
	private String fileName;
	
	@DatabaseField
	private String filePath;
	
	
}
