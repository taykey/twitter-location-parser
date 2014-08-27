package com.taykey.locationparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.taykey.locationparser.dao.LocationDao;
import com.taykey.locationparser.dto.Location;
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
		populateDB.loadLocations("data/countries_v3.csv");
		populateDB.loadLocations("data/cities_v3.csv");
	}



	@Override
	public String parseText(String text)
	{
		List<String> words = ngrams(text, 3);
		Set<Location> countryCandidates = new HashSet<Location>();
		Set<Location> stateCandidates = new HashSet<Location>();
		Set<Location> cityCandidates = new HashSet<Location>();
		for (String word: words)
		{
			List<Location> locations = locationDao.getLocation(word);
			if (locations == null)
				continue;
			for (Location location : locations)
            {
				switch (location.getType())
                {
					case Country:
						countryCandidates.add(location);
						break;
					case State:
						stateCandidates.add(location);
						break;
					case City:
						cityCandidates.add(location);
					default:
						break;
				}
					
            }
		}
		if (countryCandidates.size() == 1)
		{
			// in this point we are sure about which country we have. we need to check if there is also a city.
			Location country = new ArrayList<Location>(countryCandidates).get(0);
			return country.getName();
		}
		
		if (cityCandidates.size() == 1)
		{
			// in this point we are sure about which city we have. we need to get the right country. 
			Location city = new ArrayList<Location>(cityCandidates).get(0);
			String countryStr = city.getCountryCode();
			List<Location> countries = locationDao.getLocation(countryStr);
			if (countries != null)
				countryStr = countries.get(0).getName();
			return  city.getName()+","+countryStr;
			
		}
		
		if (cityCandidates.size() > 1 && countryCandidates.size() > 1) 
		{
			// in this point we are sure about which city we have. we need to get the right country. 
			//System.out.println(cityCandidates+ " "+countryCandidates);
			citiesCountries+=1;
			return null;
		}

		if (cityCandidates.size() > 1) 
		{
			// in this point we know we have more then 1 city. with no country information.
			// if there is only 1 major city. we will choose it.
			Location max = null;
			int maxPopulation = 0;
			int secondMaxPopulation = 0;
			for (Location location : cityCandidates)
            {
				int population = location.getPopulation();
				if (population > maxPopulation)
				{
					secondMaxPopulation = maxPopulation;
					maxPopulation = population;
					max = location;
				}
				
            }
			if  (maxPopulation > secondMaxPopulation * 10)
			{
				String countryStr = max.getCountryCode();
				List<Location> countries = locationDao.getLocation(countryStr);
				if (countries != null)
					countryStr = countries.get(0).getName();
				return  max.getName()+","+countryStr;
			}
				
			
		}

		if (countryCandidates.size() > 1) 
		{
			// in this point we are sure about which city we have. we need to get the right country. 
		//	System.out.println(countryCandidates);
			countries+=1;
		}

		return null;

	}
	private int citiesCountries = 0;
	private int cities = 0;
	private int countries = 0;

	private List<String> ngrams(String text, int n)
	{
		List<String> ngrams = new ArrayList<String>();
		String[] twords = text.split("\\W", 0);
		int l = 0;
		for (int i = 0; i < twords.length; i++ ){ if (!twords[i].trim().isEmpty()) l++;}
		String[] words = new String[l];
		l = 0;
		for (int i = 0; i < twords.length; i++ ){ if (!twords[i].trim().isEmpty()) words[l++] = twords[i].trim();}
		
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
