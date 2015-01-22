package mx.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import mx.mobile.junamex.R;
import mx.mobile.model.Event;
import mx.mobile.utils.TextViewFont;

/**
 * Created by desarrollo16 on 20/01/15.
 */
public class ScheduleAdapter extends BaseAdapter {

    private List<Event> eventList;
    private LayoutInflater inflater;

    public ScheduleAdapter(Context context, List<Event> eventList) {
        this.eventList = eventList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_schedule, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        Event event = eventList.get(position);

        holder.time.setText(event.getStartTimeString());
        holder.name.setText(event.getEventName());
        holder.location.setText(event.getLocationName());

        return convertView;
    }

    private class ViewHolder {

        TextViewFont time, name, location;

        private ViewHolder(View v) {

            time = (TextViewFont) v.findViewById(R.id.schedule_time);
            name = (TextViewFont) v.findViewById(R.id.schedule_name);
            location = (TextViewFont) v.findViewById(R.id.schedule_location);
        }
    }


}
