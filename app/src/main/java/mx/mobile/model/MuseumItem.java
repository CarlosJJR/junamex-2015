package mx.mobile.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by desarrollo16 on 29/01/15.
 */
@ParseClassName("MuseumItem")
public class MuseumItem extends ParseObject {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";

    public MuseumItem() {
        super();
    }

    public String getName() {
        return getString(NAME);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(IMAGE);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MuseumItem) {
            MuseumItem copy = (MuseumItem) o;
            if (!copy.getUpdatedAt().after(getUpdatedAt())
                    && copy.getObjectId().equals(getObjectId()))
                return true;

        }
        return false;
    }
}
