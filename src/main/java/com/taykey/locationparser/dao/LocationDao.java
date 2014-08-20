package com.taykey.locationparser.dao;

import java.util.List;

import com.taykey.locationparser.dto.Location;

public interface LocationDao
{
	boolean isCity(String city);

	boolean isCountry(String country);

	boolean isLocation(String location);
	
	void addCountry(String country);

	void addCity(String city);

	void addLocation(Location location);
	
	List<Location> getLocation(String location);
	
}
