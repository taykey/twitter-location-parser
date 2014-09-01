package com.taykey.twitterlocationparser.populatedb;

import org.junit.Test;

import com.taykey.twitterlocationparser.dao.MemLocationDao;
import com.taykey.twitterlocationparser.populatedb.DefaultPupulateDB;

public class TestPopulateDb {

    @Test
    public void loadCitiesTest() {
	MemLocationDao memLocationDao = new MemLocationDao();
	DefaultPupulateDB pupulateDB = new DefaultPupulateDB(memLocationDao);
	pupulateDB.loadLocations("data/countries_v3.csv");
	pupulateDB.loadLocations("data/states_v3.csv");
	pupulateDB.loadLocations("data/cities_v3.csv");
    }
}
