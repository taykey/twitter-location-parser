package com.taykey.locationparser.populatedb;

import com.taykey.locationparser.common.IterableFile;
import com.taykey.locationparser.dao.LocationDao;
import com.taykey.locationparser.dto.Location;
import com.taykey.locationparser.dto.LocationType;

public class DefaultPupulateDB implements PopulateDB
{
	private LocationDao locationDao;
	
	public DefaultPupulateDB(LocationDao locationDao)
    {
		this.locationDao = locationDao;
    }
	
	@Override
    public void loadCities(String filePath)
    {
	    IterableFile iterator = new IterableFile(filePath);
	    for (String city : iterator)
        {
	        locationDao.addCity(city.toLowerCase());
        }
    }

	@Override
    public void loadCountries(String filePath)
    {
		IterableFile iterator = new IterableFile(filePath);
	    for (String country : iterator)
        {
	        locationDao.addCountry(country.toLowerCase());
        }
    }
	
	@Override
    public void loadLocations(String filePath)
    {
	    IterableFile iterator = new IterableFile(filePath);
	    for (String text : iterator)
        {
	    	String[] fields = text.split("\t");
	        locationDao.addLocation(new Location(fields[0],fields[1],fields[2],LocationType.valueOf(fields[3]), Integer.parseInt(fields[4])));
        }
    }

}
