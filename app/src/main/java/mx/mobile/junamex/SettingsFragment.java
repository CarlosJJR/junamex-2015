package mx.mobile.junamex;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import mx.mobile.adapters.NavigationDrawerAdapter;

/**
 * Created by desarrollo16 on 10/02/15.
 */
public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerAdapter.SETTINGS);
    }
}
