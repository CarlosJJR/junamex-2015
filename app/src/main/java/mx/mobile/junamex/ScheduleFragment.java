package mx.mobile.junamex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.mobile.adapters.ScheduleAdapter;
import mx.mobile.model.Event;

/**
 * Created by desarrollo16 on 20/01/15.
 */
public class ScheduleFragment extends ListFragment {

    public static final int DAYS_OF_JUNAMEX = 5;

    public static final String TUES = "14/07/2015 00:00:00";
    public static final String WED = "15/07/2015 00:00:00";
    public static final String THUR = "16/07/2015 00:00:00";
    public static final String FRI = "17/07/2015 00:00:00";
    public static final String SAT = "18/07/2015 00:00:00";
    public static final String DAY_KEY = "day";

    private ScheduleAdapter adapter;
    private ArrayList<Event> eventList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventList = new ArrayList<>();
        adapter = new ScheduleAdapter(getActivity(), eventList);
        setListAdapter(adapter);

        Bundle args = getArguments();
        String day = TUES;
        if (args != null)
            day = args.getString(DAY_KEY);

        loadDataForDay(day);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getListView().setDivider(null);
        super.onViewCreated(view, savedInstanceState);
    }

    public static ScheduleFragment newInstance(String day) {

        Bundle args = new Bundle();
        args.putString(DAY_KEY, day);

        ScheduleFragment scheduleFragment = new ScheduleFragment();
        scheduleFragment.setArguments(args);

        return scheduleFragment;
    }

    private void loadDataForDay(String day) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        Date dayStart = null;
        try {
            dayStart = dateFormat.parse(day);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(dayStart);
        c.add(Calendar.DATE, 1);
        Date dayEnd = c.getTime();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereGreaterThan("time", dayStart);
        query.whereLessThan("time", dayEnd);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {

                    for (ParseObject object : parseObjects) {
                        Event event = new Event();

                        String id = object.getObjectId();
                        String eventName = object.getString("name");
                        String eventAbstract = object.getString("description");
                        Date startTime = object.getDate("time");
                        ParseGeoPoint locationRaw = object.getParseGeoPoint("location");
                        LatLng location = new LatLng(locationRaw.getLatitude(), locationRaw.getLongitude());
                        String locationName = object.getString("locationName");

                        event.setId(id)
                                .setEventName(eventName)
                                .setEventAbstract(eventAbstract)
                                .setStartTime(startTime)
                                .setLocation(location)
                                .setLocationName(locationName);

                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    //Error
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(getActivity(), ScrollActivity.class);
        intent.putExtra("event_id", eventList.get(position).getId());
        startActivity(intent);
    }
}
