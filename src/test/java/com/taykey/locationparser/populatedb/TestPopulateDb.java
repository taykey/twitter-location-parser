package com.taykey.locationparser.populatedb;

import org.junit.Test;

import com.taykey.locationparser.dao.MemLocationDao;

public class TestPopulateDb
{

	@Test
	public void loadCitiesTest()
	{
		MemLocationDao memLocationDao = new MemLocationDao();
		DefaultPupulateDB pupulateDB = new DefaultPupulateDB(memLocationDao);
		pupulateDB.loadCities("/mnt/taykey/dev/udy/test/data/countries.csv");
		//pupulateDB.loadCountries(filePath);
		
	}
}
