package edu.sru.nullstring.Data;

import java.util.ArrayList;

public class DataProvider {

	private ArrayList<CategoryType> m_Categories = new ArrayList<CategoryType>();
	
	public enum ProviderType
	{
		Locadex,
		GoogleCalendar
	}
	
	public enum ProviderResult
	{
		Failed,
		Success
	}
	
	public ArrayList<CategoryType> ListCategories()
	{
		// after expanding once, save categories to memory...
		
		return m_Categories;
	}
	
	public ProviderResult UpdateMarker(MarkerType m)
	{
		return ProviderResult.Failed;
	}
	
	public ProviderResult DeleteMarker(MarkerType m)
	{
		return ProviderResult.Failed;
	}
	
	public ProviderResult AddMarker(MarkerType m)
	{
		return ProviderResult.Failed;
	}
	
}
