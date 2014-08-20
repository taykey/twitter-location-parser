package com.taykey.locationparser.populatedb;

public interface PopulateDB
{
	void loadCities(String filePath);
	
	void loadCountries(String filePath);

	void loadLocations(String filePath);
}
