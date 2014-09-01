package com.taykey.twitterlocationparser.populatedb;

import com.taykey.twitterlocationparser.common.IterableFile;
import com.taykey.twitterlocationparser.dao.LocationDao;
import com.taykey.twitterlocationparser.dto.Location;
import com.taykey.twitterlocationparser.dto.LocationType;

public class DefaultPupulateDB implements PopulateDB {

    private LocationDao locationDao;

    public DefaultPupulateDB(LocationDao locationDao) {
	this.locationDao = locationDao;
    }

    @Override
    public void loadLocations(String filePath) {
	IterableFile iterator = new IterableFile(filePath);
	for (String text : iterator) {
	    String[] fields = text.split("\t");
	    locationDao.addLocation(new Location(fields[0], fields[1],
		    fields[2], fields[3], LocationType.valueOf(fields[4]), Integer
			    .parseInt(fields[5])));
	}
    }

}
