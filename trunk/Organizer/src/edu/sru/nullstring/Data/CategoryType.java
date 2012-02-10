package edu.sru.nullstring.Data;


public class CategoryType {

	public enum ProviderType {
		Locadex,
		GoogleCalendar
	}
	
	// Category level contains the connection to data provider
	// We must be able to allow the subgroups to implement their own
	// But we need to know the features available in each category
	// Ex: Google Calendar may only be able to do 
	public ProviderType Provider; 
	
	public String CategoryName;
	public MarkerCollection MarkerCollection;
	public ReminderCollection ReminderCollection;
	
	
}
