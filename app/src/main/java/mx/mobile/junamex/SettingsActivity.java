package mx.mobile.junamex;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by desarrollo16 on 06/03/15.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.preferences);

        Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
        actionbar.setTitle(getString(R.string.settings));
        actionbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
    }
}
