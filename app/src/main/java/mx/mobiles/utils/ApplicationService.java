package mx.mobiles.utils;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        Utilities.overrideFont(getApplicationContext(), "SERIF", "fonts/8bitOperatorPlus-Regular.ttf");
    }

    private void printHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "mx.mobiles.junamex",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("Error", e.getLocalizedMessage());
        }
    }
}
