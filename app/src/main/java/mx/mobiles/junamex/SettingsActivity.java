package mx.mobiles.junamex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import mx.mobiles.db.DatabaseHelper;
import mx.mobiles.model.Event;

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

    private static class RescheduleNotifications extends AsyncTask<Void, Void, Void> {

        private SQLiteDatabase database;
        private Context context;

        public RescheduleNotifications(Context context, SQLiteDatabase database) {
            this.context = context;
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<HashMap<String, String>> eventIDs = Event.getAllNotificationsEnabled(database);

            Log.i("AlarmManager", "Found " + eventIDs.size() + " events to reschedule");
            for (HashMap<String, String> map : eventIDs) {

                Event event = getEvent(map.get(Event.ID));
                if (event != null) {

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(event.convertFromUTC(event.getStartTime()));
//                    cal.add(Calendar.DAY_OF_YEAR, -78);

                    if (cal.getTime().after(new Date())) {
                        Log.i("AlarmManager", "Rescheduling " + event.getEventName());
                        event.setDatabaseId(Integer.parseInt(map.get(Event.DB_ID)));
                        event.rescheduleAlarm(context);
                    }
                }
            }
            return null;
        }

        private Event getEvent(String eventID) {

            ParseQuery<Event> query = new ParseQuery<> (Event.class);
            query.include(Event.LOCATION);
            try {
                return query.get(eventID);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            context = null;
            database.close();
        }
    }
}
