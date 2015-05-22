package mx.mobiles.junamex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;

import mx.mobiles.utils.FacebookLogin;

/**
 * Created by desarrollo16 on 06/03/15.
 */
public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    public static final String VIBRATION_ENABLED = "vibration_enabled";
    public static final String REFRESH_DATA = "auto_refresh_data";
    public static final String NO_ACTIVE_USER = "no_active_user";

    TextView facebookControl, dataUsageSubtitle;
    private SwitchCompat notificationsVibrate, dataUsage;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        notificationsVibrate = (SwitchCompat) findViewById(R.id.notifications_vibrate);
        dataUsage = (SwitchCompat) findViewById(R.id.data_usage);
        facebookControl = (TextView) findViewById(R.id.facebook_login);
        dataUsageSubtitle = (TextView) findViewById(R.id.data_usage_subtitle);

        updateControls();

        notificationsVibrate.setOnCheckedChangeListener(this);
        dataUsage.setOnCheckedChangeListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    private void updateControls() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean vibration = sharedPreferences.getBoolean(VIBRATION_ENABLED, true);
        boolean autoRefresh = sharedPreferences.getBoolean(REFRESH_DATA, true);

        notificationsVibrate.setChecked(vibration);

        dataUsage.setChecked(autoRefresh);
        int dataUsageValue = autoRefresh ? R.string.auto_refresh : R.string.manual_refresh;
        dataUsageSubtitle.setText(dataUsageValue);

        if (FacebookLogin.isUserLoggedIn())
            facebookControl.setText(getString(R.string.com_facebook_loginview_logged_in_as, Profile.getCurrentProfile().getName()));
        else {
            facebookControl.setText(R.string.com_facebook_loginview_log_in_button);
            facebookControl.setTag(NO_ACTIVE_USER);
            facebookControl.setOnClickListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (compoundButton.getId()) {

            case R.id.notifications_vibrate:
                sharedPreferences
                        .edit()
                        .putBoolean(VIBRATION_ENABLED, checked)
                        .apply();
                break;

            case R.id.data_usage:
                sharedPreferences
                        .edit()
                        .putBoolean(REFRESH_DATA, checked)
                        .apply();

                int dataUsageValue = checked ? R.string.auto_refresh : R.string.manual_refresh;
                dataUsageSubtitle.setText(dataUsageValue);
                break;

            default:
        }
    }

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();
        if (tag.equals(NO_ACTIVE_USER)) {

            callbackManager = FacebookLogin.logIn(SettingsActivity.this, new FacebookCallback<LoginResult>() {

                private ProfileTracker mProfileTracker;

                @Override
                public void onSuccess(LoginResult result) {

                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                            facebookControl.setText(getString(R.string.com_facebook_loginview_logged_in_as, newProfile.getName()));
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                    setResult(RESULT_OK);
                }

                @Override
                public void onCancel() {
                    Log.d("Facebook login", "Uh oh. The user cancelled the Facebook login.");
                    FacebookLogin.showErrorSnackBar(SettingsActivity.this);
                    setResult(RESULT_CANCELED);
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e("Facebook login", e.getLocalizedMessage());
                    FacebookLogin.showErrorSnackBar(SettingsActivity.this);
                    setResult(RESULT_CANCELED);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
