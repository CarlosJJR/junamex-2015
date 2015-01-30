package mx.mobile.junamex;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.transition.Transition;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ListView;
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

    private View loadingView;
    private String day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventList = new ArrayList<>();
        adapter = new ScheduleAdapter(getActivity(), eventList);
        setListAdapter(adapter);

        Bundle args = getArguments();
        day = TUES;
        if (args != null)
            day = args.getString(DAY_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        loadingView = view.findViewById(android.R.id.progress);
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


        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.LOCATION);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereGreaterThan(Event.START_TIME, dayStart);
        query.whereLessThan(Event.START_TIME, dayEnd);

        loadingView.setVisibility(View.VISIBLE);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> queriedEventList, ParseException e) {
                if (e == null) {

                    eventList.clear();
                    for (Event event : queriedEventList) {
                        eventList.add(event);
                    }
                    loadingView.setVisibility(View.GONE);
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

        Intent intent = new Intent(getActivity(), EventDetailActivity.class);

        Event event = eventList.get(position);
        intent.putExtra(EventDetailActivity.EVENT_KEY, event.getObjectId());

        if (event.getPaletteColor() != 0)
            intent.putExtra(EventDetailActivity.PALETTE_KEY, event.getPaletteColor());

        startActivity(intent);
    }
}
