package com.taykey.twitterlocationparser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.taykey.twitterlocationparser.common.IterableFile;
import com.taykey.twitterlocationparser.dto.Location;

public class TestLocationParser {

    LocationParser locationParser = new DefaultLocationParser();

    @Test
    public void parseTextTest() {
        int c = 0;
        IterableFile iterableFile = new IterableFile("large_input_test.txt");
        List<String> strings = new ArrayList<String>();
        for (String string : iterableFile) {
            strings.add(string);
        }
        long time = System.currentTimeMillis();
        for (String string : strings) {
            Location location = locationParser.parseText(string);
            c = location == null ? c : c + 1;
            // System.out.println(location == null ? "null" : location.getName()
            // + "\t\t\t\t" + string);
        }
        time = System.currentTimeMillis() - time;

        int avgPerSeconds = (int) (strings.size() / (time / 1000.0));
        int minAvgPerSoceond = 10000;
        Assert.assertTrue("the speed should be at least " + minAvgPerSoceond
                + " per second", avgPerSeconds > minAvgPerSoceond);

        System.out.println("took: " + time + " to parse " + strings.size()
                + " lines. num of location found is: " + c);
        System.out.println("avgPerSeconds: " + avgPerSeconds);
    }

    @Test
    public void parseSingleTextTest() {
        long time = System.currentTimeMillis();
        String string = "the end of the line";
        Location location = locationParser.parseText(string);
        time = System.currentTimeMillis() - time;
        System.out.println("took: " + time + ".\t" + location + "\t\t\t\t"
                + string);

    }

}
