package mx.mobile.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by desarrollo16 on 27/01/15.
 */
@ParseClassName("Speaker")
public class Speaker extends ParseObject {

    public static final String NAME = "name";
    public static final String BIOGRAPHY = "speakerInfo";
    public static final String PHOTO = "speakerPhoto";

    public Speaker() {
        super();
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public String getBiography() {
        return getString(BIOGRAPHY);
    }

    public void setBiography(String biography) {
        put(BIOGRAPHY, biography);
    }

    public ParseFile getPhoto() {
        return getParseFile(PHOTO);
    }

    public void setPhoto(ParseFile photo) {
        put(PHOTO, photo);
    }
}
