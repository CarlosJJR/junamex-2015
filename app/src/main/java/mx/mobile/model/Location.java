package mx.mobile.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by desarrollo16 on 27/01/15.
 */
@ParseClassName("Location")
public class Location extends ParseObject {

    public static final String NAME = "name";
    public static final String COORDINATES = "coordinates";

    public Location() {
        super();
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public ParseGeoPoint getCoordinates() {
        return getParseGeoPoint(COORDINATES);
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        put(COORDINATES, coordinates);
    }
}
