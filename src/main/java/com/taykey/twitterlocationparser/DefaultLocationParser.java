package com.taykey.twitterlocationparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taykey.twitterlocationparser.dao.LocationDao;
import com.taykey.twitterlocationparser.dao.MemLocationDao;
import com.taykey.twitterlocationparser.dto.Location;
import com.taykey.twitterlocationparser.dto.LocationType;
import com.taykey.twitterlocationparser.populatedb.DefaultPopulateDB;
import com.taykey.twitterlocationparser.populatedb.PopulateDB;

public class DefaultLocationParser implements LocationParser {

    private static Logger log = LoggerFactory
            .getLogger(DefaultLocationParser.class);

    private LocationDao locationDao;

    private PopulateDB populateDB;

    public DefaultLocationParser() {
        locationDao = new MemLocationDao();
        PopulateDB populateDB = new DefaultPopulateDB(locationDao);
        populateDB.loadLocations();
    }

    public DefaultLocationParser(LocationDao locationDao) {
        this.setLocationDao(locationDao);
    }

    @Override
    public Location parseText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
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

        Location location = getLocationFromCandidates(candidates);
        log.trace("text: {}\t\t\tlocation: {}", text, location);
        return location;
    }

    private Location getLocationFromCandidates(
            Map<LocationType, Set<Location>> candidates) {

        Set<Location> cityCandidates = candidates.get(LocationType.City);
        Set<Location> stateCandidates = candidates.get(LocationType.State);
        Set<Location> countryCandidates = candidates.get(LocationType.Country);

        if (cityCandidates != null) {
            // in this point we are sure about which city we have.
            // we can check if we have states or countries and cross them to
            // increase confidence.
            if (cityCandidates.size() == 1
                    && (stateCandidates == null || stateCandidates.isEmpty())
                    && (countryCandidates == null || countryCandidates.isEmpty())) {
                return cityCandidates.iterator().next();
            }

            if (cityCandidates.size() > 1) {
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
        }

        if (stateCandidates != null && stateCandidates.size() == 1) {
            // in this point we are sure about which state we have.
            // we can check if we have cities or countries and cross them to
            // increase confidence.
            if ((countryCandidates == null || countryCandidates.isEmpty())
                    && (cityCandidates == null || cityCandidates.isEmpty())) {
                return stateCandidates.iterator().next();
            }
        }

        if (countryCandidates != null && countryCandidates.size() == 1) {
            // in this point we are sure about which country we have.
            // we can check if we have cities or states and cross them to
            // increase confidence.
            if ((stateCandidates == null || stateCandidates.isEmpty())
                    && (cityCandidates == null || cityCandidates.isEmpty())) {
                return countryCandidates.iterator().next();
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

    public LocationDao getLocationDao() {
        return locationDao;
    }

    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public PopulateDB getPopulateDB() {
        return populateDB;
    }

    public void setPopulateDB(PopulateDB populateDB) {
        this.populateDB = populateDB;
    }

}
