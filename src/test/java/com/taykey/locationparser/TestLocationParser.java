package com.taykey.locationparser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.taykey.locationparser.common.IterableFile;
import com.taykey.locationparser.dao.MemLocationDao;

public class TestLocationParser
{

	LocationParser locationParser = new DefaultLocationParser(new MemLocationDao());
	
	@Test
	public void parseTextTest()
	{
		int c = 0;
		IterableFile iterableFile = new IterableFile("/mnt/taykey/dev/udy/test/data/input_test_2.txt");
		List<String> strings = new ArrayList<String>();
		for (String string : iterableFile)
        {
			strings.add(string);
        }
		long time = System.currentTimeMillis();
		for (String string : strings)
        {
			String parseText = locationParser.parseText(string);
			if (parseText != null)
			{	
				c++;
				System.out.println(parseText +"\t\t\t\t"+string);
			}
        }
		time = System.currentTimeMillis() - time;
		System.out.println("took: "+time+" to parse "+strings.size()+" lines. num of location found is: " + c);
		
	}
	
	
	@Test
	public void parseSingleTextTest()
	{
		long time = System.currentTimeMillis();
		String string = "New York";
		String parseText = locationParser.parseText(string);
		time = System.currentTimeMillis() - time;
		System.out.println();
		System.out.println("took: "+time+".\t"+parseText +"\t\t\t\t"+string);
		
	}
	
	
}
