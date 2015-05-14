package mx.mobiles.utils;

import android.app.Activity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;

/**
 * Created by desarrollo16 on 14/05/15.
 */

public class FacebookLogin {

    public static CallbackManager logIn(Activity activity, FacebookCallback<LoginResult> loginCallback) {

        CallbackManager callbackManager  = CallbackManager.Factory.create();
        if (Profile.getCurrentProfile() == null) {

            ArrayList<String> permissions = new ArrayList<>();
            permissions.add("email");

            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, loginCallback);
            loginManager.logInWithReadPermissions(activity, permissions);
        } else {
            Log.d("FacebookLogin", "Already signed in");
        }
        return callbackManager;
    }

    public static boolean isUserLoggedIn() {
        return Profile.getCurrentProfile() != null;
    }
}
