package com.taykey.locationparser;

import java.util.ArrayList;
import java.util.List;

import com.taykey.locationparser.dao.LocationDao;
import com.taykey.locationparser.dto.Location;
import com.taykey.locationparser.dto.LocationType;
import com.taykey.locationparser.populatedb.DefaultPupulateDB;
import com.taykey.locationparser.populatedb.PopulateDB;

public class DefaultLocationParser implements LocationParser
{

	private LocationDao locationDao;



	public DefaultLocationParser(LocationDao locationDao)
	{
		this.locationDao = locationDao;
		PopulateDB populateDB = new DefaultPupulateDB(locationDao);
		//populateDB.loadCountries("/mnt/taykey/dev/udy/test/data/countries.csv");
		//populateDB.loadCities("/mnt/taykey/dev/udy/test/data/cities_.csv");
		populateDB.loadLocations("/mnt/taykey/dev/udy/test/data/countries_v2.csv");
		populateDB.loadLocations("/mnt/taykey/dev/udy/test/data/cities_v2.csv");
	}



	@Override
	public String parseText(String text)
	{
		List<String> words = ngrams(text, 3);
		String country = null;
		String city = null;
		for (String word: words)
		{
			List<Location> locations = locationDao.getLocation(word);
			if (locations == null)
				continue;
			if (locations.size() == 1)
			{
				Location location = locations.get(0);
				if (location.getType().equals(LocationType.Country))
					country = location.getName();
				if (location.getType().equals(LocationType.City))
					city = location.getName();
			}
			
			if (city != null && country != null)
				return city+","+country;
		}
		if (city == null && country == null)
			return null;
		
		if (city != null)
			return city;
		
		return country;
	}



	private List<String> ngrams(String text, int n)
	{
		List<String> ngrams = new ArrayList<String>();
		String[] words = text.split("[\\s,.\\-\\\\!\\\\]",0);
		for (int i = 0; i < words.length; i++ )
        {
			words[i] = words[i].trim();
        }
		for (int k = n; k > 0; k-- )
        {
			for (int i = 0; i < words.length - k + 1; i++ )
			{
				ngrams.add(concat(words, i, i + k));
			}
        }
		
		return ngrams;
	}



	private String concat(String[] words, int start, int end)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++ )
			sb.append( (i > start ? " " : "") + words[i]);
		return sb.toString();
	}

}
