package com.taykey.locationparser.dao;

import java.util.List;

import com.taykey.locationparser.dto.Location;

public interface LocationDao {

    void addLocation(Location location);

    List<Location> getLocation(String location);

    Location getCountryByCode(String countryCode);

    Location getStateByCode(String stateCode);

}
