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

    private static Logger log = LoggerFactory.getLogger(DefaultLocationParser.class);
    
    private LocationDao locationDao;

    public DefaultLocationParser(LocationDao locationDao) {
	this.locationDao = locationDao;
	PopulateDB populateDB = new DefaultPupulateDB(locationDao);
	populateDB.loadLocations("data/countries.tsv");
	populateDB.loadLocations("data/states.tsv");
	populateDB.loadLocations("data/cities.tsv");
    }

    @Override
    public String parseText(String text) {
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

    private String getLocationFromCandidates(String text,
	    Map<LocationType, Set<Location>> candidates) {
	if (candidates.get(LocationType.City) != null && candidates.get(LocationType.City).size() == 1) {
	    // in this point we are sure about which city we have. we need to
	    // get the right country.
	    Location city = new ArrayList<Location>(
		    candidates.get(LocationType.City)).get(0);
	    String stateCode = city.getStateCode();
	    Location state = locationDao.getStateByCode(stateCode);
	    if (stateCode != null) {
		stateCode = state.getName();
	    } else {
		log.debug("found city with no state. text: {}. city: {}", text, city);
	    }

	    String countryCode = city.getCountryCode();
	    Location country = locationDao.getCountryByCode(countryCode);
	    if (country != null) {
		countryCode = country.getName();
	    } else {
		log.debug("found city with no country. text: {}. city: {}", text, city);
	    }

	    return city.getName() + "," + stateCode + "," + countryCode;
	}

	if (candidates.get(LocationType.Country) != null && candidates.get(LocationType.Country).size() == 1) {
	    // in this point we are sure about which country we have. we need to
	    // check if there is also a
	    // city.
	    Location country = new ArrayList<Location>(
		    candidates.get(LocationType.Country)).get(0);
	    return country.getName();
	}

	if (candidates.get(LocationType.State) != null && candidates.get(LocationType.State).size() == 1) {
	    // in this point we are sure about which city we have. we need to
	    // get the right country.
	    Location state = new ArrayList<Location>(
		    candidates.get(LocationType.State)).get(0);
	    String countryStr = state.getCountryCode();
	    Location country = locationDao.getCountryByCode(countryStr);
	    if (country != null) {
		countryStr = country.getName();
	    } else {
		log.debug("found state with no country. text: {}. state: {}", text, state);
	    }

	    return state.getName() + "," + countryStr;

	}

	if (candidates.get(LocationType.City) != null && candidates.get(LocationType.City).size() > 1) {
	    // in this point we know we have more then 1 city. with no country
	    // information.
	    // if there is only 1 major city. we will choose it.
	    Location max = null;
	    int maxPopulation = 0;
	    int secondMaxPopulation = 0;
	    for (Location location : candidates.get(LocationType.City)) {
		int population = location.getPopulation();
		if (population > maxPopulation) {
		    secondMaxPopulation = maxPopulation;
		    maxPopulation = population;
		    max = location;
		}

	    }
	    if (maxPopulation > secondMaxPopulation * 10) {
		String stateCode = max.getStateCode();
		Location state = locationDao.getStateByCode(stateCode);
		if (stateCode != null) {
		    stateCode = state.getName();
		} else {
		    log.debug("found city with no state. text: {}. city: {}", text, max);
		}

		String countryCode = max.getCountryCode();
		Location country = locationDao.getCountryByCode(countryCode);
		if (country != null) {
		    countryCode = country.getName();
		} else {
		    log.debug("found city with no country. text: {}. city: {}", text, max);
		}

		return max.getName() + "," + stateCode + "," + countryCode;
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
