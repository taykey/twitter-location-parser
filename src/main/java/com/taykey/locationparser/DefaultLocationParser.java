package com.taykey.locationparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taykey.locationparser.dao.LocationDao;
import com.taykey.locationparser.dto.Location;
import com.taykey.locationparser.dto.LocationType;
import com.taykey.locationparser.populatedb.DefaultPupulateDB;
import com.taykey.locationparser.populatedb.PopulateDB;

public class DefaultLocationParser implements LocationParser {

    private static Logger log = LoggerFactory
	    .getLogger(DefaultLocationParser.class);

    private LocationDao locationDao;

    public DefaultLocationParser(LocationDao locationDao) {
	this.locationDao = locationDao;
	PopulateDB populateDB = new DefaultPupulateDB(locationDao);
	populateDB.loadLocations("data/countries.tsv");
	populateDB.loadLocations("data/states.tsv");
	populateDB.loadLocations("data/cities.tsv");
    }

    @Override
    public Location parseText(String text) {
	List<String> words = ngrams(text, 3);
	Map<LocationType, Set<Location>> candidates = new HashMap<LocationType, Set<Location>>();
	Set<String> wordsWithLocation = new HashSet<String>();
	for (String word : words) {
	    if (isContained(wordsWithLocation, word)) {
		continue;
	    }

	    List<Location> locations = locationDao.getLocation(word);
	    if (locations == null) {
		continue;
	    }
	    wordsWithLocation.add(word);
	    for (Location location : locations) {
		Set<Location> set = candidates.get(location.getType());
		if (set == null) {
		    set = new HashSet<Location>();
		    candidates.put(location.getType(), set);
		}
		set.add(location);
	    }
	}

	return getLocationFromCandidates(text, candidates);

    }

    private Location getLocationFromCandidates(String text,
	    Map<LocationType, Set<Location>> candidates) {
	Set<Location> cityCandidates = candidates.get(LocationType.City);
	if (cityCandidates != null && cityCandidates.size() == 1) {
	    // in this point we are sure about which city we have. we need to
	    // get the right country.
	    return cityCandidates.iterator().next();
	}

	Set<Location> countryCandidates = candidates.get(LocationType.Country);
	if (countryCandidates != null && countryCandidates.size() == 1) {
	    // in this point we are sure about which country we have. we need to
	    // check if there is also a city
	    return countryCandidates.iterator().next();
	}

	Set<Location> stateCandidates = candidates.get(LocationType.State);
	if (stateCandidates != null && stateCandidates.size() == 1) {
	    return stateCandidates.iterator().next();
	}

	if (cityCandidates != null && cityCandidates.size() > 1) {
	    // in this point we know we have more then 1 city.
	    // we can choose the right city if there is a state or city info
	    // if there isn't we will choose the major one if there is a big
	    // diff.
	    Location max = null;
	    int maxPopulation = 0;
	    int secondMaxPopulation = 0;
	    for (Location location : cityCandidates) {
		int population = location.getPopulation();
		if (population > maxPopulation) {
		    secondMaxPopulation = maxPopulation;
		    maxPopulation = population;
		    max = location;
		}
	    }
	    if (maxPopulation > secondMaxPopulation * 10) {
		return max;
	    }
	}

	return null;
    }

    private boolean isContained(Set<String> wordsWithLocation, String word) {
	boolean contained = false;
	for (String prevWord : wordsWithLocation) {
	    if (prevWord.contains(word)) {
		contained = true;
		break;
	    }
	}
	return contained;
    }

    private List<String> ngrams(String text, int n) {
	List<String> ngrams = new ArrayList<String>();
	String[] twords = text.split("\\P{L}", 0);
	int l = 0;
	for (int i = 0; i < twords.length; i++) {
	    if (!twords[i].trim().isEmpty())
		l++;
	}
	String[] words = new String[l];
	l = 0;
	for (int i = 0; i < twords.length; i++) {
	    if (!twords[i].trim().isEmpty())
		words[l++] = twords[i].trim();
	}

	for (int k = n; k > 0; k--) {
	    for (int i = 0; i < words.length - k + 1; i++) {
		ngrams.add(concat(words, i, i + k));
	    }
	}

	return ngrams;
    }

    private String concat(String[] words, int start, int end) {
	StringBuilder sb = new StringBuilder();
	for (int i = start; i < end; i++)
	    sb.append((i > start ? " " : "") + words[i]);
	return sb.toString();
    }

}
