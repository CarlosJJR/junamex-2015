package mx.mobiles.junamex;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

/**
 * Created by desarrollo16 on 06/03/15.
 */
public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    public static final String VIBRATION_ENABLED = "vibration_enabled";
    public static final String REFRESH_DATA = "auto_refresh_data";

    TextView facebookControl, dataUsageSubtitle;
    private SwitchCompat notificationsVibrate, dataUsage;

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
        facebookControl.setOnClickListener(this);
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

        if (AccessToken.getCurrentAccessToken() == null)
            facebookControl.setText(com.facebook.R.string.com_facebook_loginview_log_in_button);
        else
            facebookControl.setText(com.facebook.R.string.com_facebook_loginview_logged_in_using_facebook);
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
        facebookLogout();
    }

    public void facebookLogout() {
        final LoginManager session = LoginManager.getInstance();
        if (session != null) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.com_facebook_loginview_logged_in_as, "user"))
                    .setNegativeButton(R.string.com_facebook_loginview_cancel_action, null)
                    .setPositiveButton(R.string.com_facebook_loginview_log_out_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i("Facebook", "Log out from Facebook");
                            session.logOut();
                            facebookControl.setText(R.string.com_facebook_loginview_log_in_button);
                        }
                    })
                    .show();
        } else {
            Log.i("Facebook", "Log in on Facebook");
        }
    }
}
