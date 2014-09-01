package com.taykey.locationparser;

import com.taykey.locationparser.dto.Location;

public interface LocationParser {

    Location parseText(String text);
}
