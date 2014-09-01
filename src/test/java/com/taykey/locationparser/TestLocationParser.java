package com.taykey.locationparser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.taykey.locationparser.common.IterableFile;
import com.taykey.locationparser.dao.MemLocationDao;
import com.taykey.locationparser.dto.Location;

public class TestLocationParser {

    LocationParser locationParser = new DefaultLocationParser(
	    new MemLocationDao());

    @Test
    public void parseTextTest() {
	int c = 0;
	IterableFile iterableFile = new IterableFile(
		"src/test/resources/large_input_test.txt");
	List<String> strings = new ArrayList<String>();
	for (String string : iterableFile) {
	    strings.add(string);
	}
	long time = System.currentTimeMillis();
	for (String string : strings) {
	    Location location = locationParser.parseText(string);
	    c = location == null ? c : c + 1;
	  //  System.out.println(parseText + "\t\t\t\t" + string);
	}
	time = System.currentTimeMillis() - time;
	System.out.println("took: " + time + " to parse " + strings.size()
		+ " lines. num of location found is: " + c);
	int avgPerSeconds = (int) (strings.size() / (time / 1000.0));
	System.out.println("avgPerSeconds: " + avgPerSeconds);

    }

    @Test
    public void parseSingleTextTest() {
	long time = System.currentTimeMillis();
	String string = "Bahía Blanca";
	Location location = locationParser.parseText(string);
	time = System.currentTimeMillis() - time;
	System.out.println();
	System.out.println("took: " + time + ".\t" + location + "\t\t\t\t"
		+ string);

    }

}
