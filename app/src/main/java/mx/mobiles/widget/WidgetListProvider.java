package mx.mobiles.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.mobiles.junamex.EventDetailActivity;
import mx.mobiles.junamex.R;
import mx.mobiles.junamex.ScheduleFragment;
import mx.mobiles.model.Event;

/**
 * Created by desarrollo16 on 25/05/15.
 */
public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<Event> eventList = new ArrayList<>();
    private Context context = null;

    public WidgetListProvider(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.eventList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteView;
        if (position == 0 || position == eventList.size() - 1) {
            remoteView = new RemoteViews(context.getPackageName(), R.layout.list_header_8dp);
        } else {
            remoteView = new RemoteViews(context.getPackageName(), R.layout.item_widget);
            Event event = eventList.get(position);

            remoteView.setTextViewText(R.id.schedule_time, event.getStartTimeString());
            remoteView.setTextViewText(R.id.schedule_name, event.getEventName());
            remoteView.setTextViewText(R.id.schedule_location, event.getLocation().getName());

            int titleColor, subtitleColor;

            if (event.getPaletteColor() != 0) {
                remoteView.setInt(R.id.card_content, "setBackgroundColor", event.getPaletteColor());
                titleColor = Color.WHITE;
                subtitleColor = Color.parseColor("#B2FFFFFF");
            } else {
                remoteView.setInt(R.id.card_content, "setBackgroundResource", R.drawable.schedule_item_selector);
                titleColor = context.getResources().getColor(R.color.primary_text_color);
                subtitleColor = context.getResources().getColor(R.color.secondary_text_color);
            }

            remoteView.setTextColor(R.id.schedule_name, titleColor);
            remoteView.setTextColor(R.id.schedule_location, subtitleColor);

            if (position > 1) {

                Event previousEvent = eventList.get(position - 1);

                if (event.getStartTime().after(previousEvent.getStartTime()))
                    remoteView.setViewVisibility(R.id.schedule_time, View.VISIBLE);

                else remoteView.setViewVisibility(R.id.schedule_time, View.INVISIBLE);
            }

            Intent showEventIntent = new Intent();
            showEventIntent.putExtra(Event.ID, event.getObjectId());
            if (event.getPaletteColor() != 0)
                showEventIntent.putExtra(Event.PALETTE_COLOR, event.getPaletteColor());

            remoteView.setOnClickFillInIntent(R.id.card_content, showEventIntent);
        }

        return remoteView;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void onCreate() {
        initList();
    }

    @Override
    public void onDataSetChanged() {
        initList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initList() {

        this.eventList.clear();
        this.eventList.add(null);
        this.eventList.addAll(loadDataForDay());
        this.eventList.add(null);
    }

    private List<Event> loadDataForDay() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String today;
        Date dateForDay = null;

        switch (dayOfWeek) {

            default:
            case Calendar.SUNDAY:
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
                today = ScheduleFragment.WED;
                break;
            case Calendar.THURSDAY:
                today = ScheduleFragment.THUR;
                break;
            case Calendar.FRIDAY:
                today = ScheduleFragment.FRI;
                break;
            case Calendar.SATURDAY:
                today = ScheduleFragment.SAT;
                break;
        }

        try {
            dateForDay = dateFormat.parse(today);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

//        if (new Date().before(dateForDay))
//            return new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(dateForDay);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date dayStart = c.getTime();

        c.add(Calendar.DATE, 1);
        Date dayEnd = c.getTime();


        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.LOCATION);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.whereGreaterThan(Event.START_TIME, dayStart);
        query.whereLessThan(Event.START_TIME, dayEnd);
        query.orderByAscending(Event.START_TIME);

        List<Event> queriedEventList = new ArrayList<>();
        try {
            queriedEventList = query.find();
        } catch (ParseException e) {
            Log.e("ParseQuery<Event>", e.getLocalizedMessage());
        }

        return queriedEventList;
    }
}
