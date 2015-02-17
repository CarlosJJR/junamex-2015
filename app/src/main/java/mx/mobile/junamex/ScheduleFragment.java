package mx.mobile.junamex;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import mx.mobile.adapters.NavigationDrawerAdapter;
import mx.mobile.adapters.ScheduleAdapter;
import mx.mobile.model.Event;
import mx.mobile.utils.RecyclerViewDividers;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 20/01/15.
 */
public class ScheduleFragment extends Fragment {

    public static final int DAYS_OF_JUNAMEX = 5;

    public static final String TUES = "14/07/2015 00:00:00";
    public static final String WED = "15/07/2015 00:00:00";
    public static final String THUR = "16/07/2015 00:00:00";
    public static final String FRI = "17/07/2015 00:00:00";
    public static final String SAT = "18/07/2015 00:00:00";
    public static final String DAY_KEY = "day";

    private ScheduleAdapter adapter;
    private ArrayList<Event> eventList;

    private View loadingView, emptyView, errorView;
    private String day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventList = new ArrayList<>();
        adapter = new ScheduleAdapter(getActivity(), eventList);

        Bundle args = getArguments();
        day = TUES;
        if (args != null)
            day = args.getString(DAY_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (Utilities.isLollipop())
            recyclerView.addItemDecoration(new RecyclerViewDividers(8, 8, 0, 0));

        emptyView = view.findViewById(R.id.empty_view);
        errorView = view.findViewById(R.id.error_view);
        loadingView = view.findViewById(android.R.id.progress);

        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                loadDataForDay(day);
            }
        });
        loadDataForDay(day);
        return view;
    }

    public static ScheduleFragment newInstance(String day) {

        Bundle args = new Bundle();
        args.putString(DAY_KEY, day);

        ScheduleFragment scheduleFragment = new ScheduleFragment();
        scheduleFragment.setArguments(args);

        return scheduleFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerAdapter.SCHEDULE);
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ParseQuery.CachePolicy cachePolicy = prefs.getBoolean("auto_refresh_data", true) ? ParseQuery.CachePolicy.CACHE_THEN_NETWORK : ParseQuery.CachePolicy.CACHE_ELSE_NETWORK;

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.LOCATION);
        query.setCachePolicy(cachePolicy);
        query.whereGreaterThan(Event.START_TIME, dayStart);
        query.whereLessThan(Event.START_TIME, dayEnd);
        query.orderByAscending(Event.START_TIME);

        loadingView.setVisibility(View.VISIBLE);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> queriedEventList, ParseException e) {

                if (e == null) {
                    new ParseData().execute(queriedEventList);
                } else {

                    Log.e("ParseQuery<Event>", e.getLocalizedMessage());

                    if (e.getCode() != ParseException.CACHE_MISS) {
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private class ParseData extends AsyncTask<List<Event>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(List<Event>... params) {

            List<Event> incoming = params[0];
            Boolean listUpdated = false;

            if (eventList.isEmpty()) {
                eventList.addAll(incoming);
                listUpdated = true;
            } else {

                Date currentDate = getLastUpdated(eventList);
                Date newestDate = getLastUpdated(incoming);

                if (newestDate.after(currentDate)) {
                    eventList.clear();
                    eventList.addAll(incoming);
                    listUpdated = true;
                }
            }
            return listUpdated;
        }

        @Override
        protected void onPostExecute(Boolean listUpdated) {
            super.onPostExecute(listUpdated);

            if (listUpdated)
                adapter.notifyDataSetChanged();

            errorView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);

            if (eventList.isEmpty())
                emptyView.setVisibility(View.VISIBLE);

        }

        private Date getLastUpdated(List<Event> list) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, 2014);
            Date lastUpdate = c.getTime();

            for (Event event : list) {

                if (event.getUpdatedAt().after(lastUpdate))
                    lastUpdate = event.getUpdatedAt();
            }
            return lastUpdate;
        }
    }
}
