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
