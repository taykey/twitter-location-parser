package com.taykey.locationparser.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taykey.locationparser.dto.Location;
import com.taykey.locationparser.dto.LocationType;

public class MemLocationDao implements LocationDao
{
	Map<String,List<Location>> locations = new HashMap<String,List<Location>>();
	
	public MemLocationDao()
    {
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
		
		if (LocationType.Country.equals(location.getType()))
		{
			List<Location> list = locations.get(location.getCountryCode());
	        {
	        	list = new ArrayList<Location>();
	        	locations.put(location.getCountryCode(),list);
	        }
	        list.add(location);
		}
		
    }
	
	@Override
    public List<Location> getLocation(String location)
    {
	    return locations.get(location.toLowerCase());
    }

	@Override
    public List<Location> getCountryByCode(String countryCode)
    {
	    return locations.get(countryCode);
    }
}
