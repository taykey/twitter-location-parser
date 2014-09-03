package com.taykey.twitterlocationparser.populatedb;

import org.junit.Test;

import com.taykey.twitterlocationparser.dao.MemLocationDao;
import com.taykey.twitterlocationparser.populatedb.DefaultPopulateDB;

public class TestPopulateDb {

    @Test
    public void loadCitiesTest() {
        MemLocationDao memLocationDao = new MemLocationDao();
        PopulateDB pupulateDB = new DefaultPopulateDB(memLocationDao);
        pupulateDB.loadLocations();
    }
}
