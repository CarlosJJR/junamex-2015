package mx.mobile.utils;

import android.app.Application;

import com.parse.Parse;

import mx.mobile.junamex.R;

/**
 * Created by desarrollo16 on 21/01/15.
 */
public class ApplicationService extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getString(R.string.parse_application_key), getString(R.string.parse_client_key));
    }
}
