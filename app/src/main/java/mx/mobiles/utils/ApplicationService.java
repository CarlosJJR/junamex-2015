package mx.mobiles.utils;

import android.app.Application;
import android.os.Build;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import mx.mobiles.junamex.R;
import mx.mobiles.model.Event;
import mx.mobiles.model.Location;
import mx.mobiles.model.MuseumItem;
import mx.mobiles.model.Speaker;

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
        ParseObject.registerSubclass(MuseumItem.class);
        Parse.initialize(this, getString(R.string.parse_application_key), getString(R.string.parse_client_key));
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("apiVersion", Utilities.getCodename() + " "  + Build.VERSION.SDK_INT);
        installation.saveInBackground();

        // Initialize Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());


        Utilities.overrideFont(getApplicationContext(), "SERIF", "fonts/8bitOperatorPlus-Regular.ttf");
    }
}
