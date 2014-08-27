package com.taykey.locationparser.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.taykey.locationparser.dto.Location;

public class MemLocationDao implements LocationDao
{
	Set<String> cities; 
	
	Set<String> countries;
	
	Map<String,List<Location>> locations = new HashMap<String,List<Location>>();
	
	public MemLocationDao()
    {
		cities = new HashSet<String>();
		countries  = new HashSet<String>();
    }
	
	
	@Override
	public boolean isCity(String city)
    {
	    return cities.contains(city.toLowerCase());
    }

	@Override
	public boolean isCountry(String country)
    {
	    return countries.contains(country.toLowerCase());
    }

	@Override
	public boolean isLocation(String location)
    {
	    return cities.contains(location.toLowerCase()) || countries.contains(location.toLowerCase());
    }
	
	
	@Override
	public void addCity(String city)
    {
		cities.add(city.toLowerCase());
    }
	
	@Override
	public void addCountry(String country)
    {
		countries.add(country.toLowerCase());
    }

	@Override
	public void addLocation(Location location)
    {
		String alternateNames = location.getAlternateNames();
		alternateNames += ","+location.getName();
		String[] names = alternateNames.split(",");
		for (String name : names)
        {
	        List<Location> list = locations.get(name.toLowerCase());
	        if (list == null)
	        {
	        	list = new ArrayList<Location>();
	        	locations.put(name.toLowerCase(),list);
	        }
	        list.add(location);
	        
        }
    }
	
	@Override
    public List<Location> getLocation(String location)
    {
	    return locations.get(location.toLowerCase());
    }

}
