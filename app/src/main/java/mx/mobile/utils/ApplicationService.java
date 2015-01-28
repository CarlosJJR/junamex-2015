package mx.mobile.utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import mx.mobile.junamex.R;
import mx.mobile.model.Event;
import mx.mobile.model.Location;
import mx.mobile.model.Speaker;

/**
 * Created by desarrollo16 on 21/01/15.
 */
public class ApplicationService extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Speaker.class);
        ParseObject.registerSubclass(Location.class);
        Parse.initialize(this, getString(R.string.parse_application_key), getString(R.string.parse_client_key));
    }
}
