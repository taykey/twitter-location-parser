package com.taykey.locationparser.populatedb;

import org.junit.Test;

import com.taykey.locationparser.dao.MemLocationDao;

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
