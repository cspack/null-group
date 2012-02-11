package edu.sru.nullstring.Data;

import java.util.GregorianCalendar;

import android.widget.TextView;

import edu.sru.nullstring.Data.ReminderType.ReminderState;

public class NoteCollection {
	public CategoryType Category;

	public String Title;
	public GregorianCalendar CreationDate;	
	public String Text;
	public AttachmentCollection Attachments;
}
