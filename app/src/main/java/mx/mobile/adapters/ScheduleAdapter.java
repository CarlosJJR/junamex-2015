package mx.mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

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

        final ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_schedule, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        final Event event = eventList.get(position);

        holder.time.setText(event.getStartTimeString());
        holder.name.setText(event.getEventName());
        holder.location.setText(event.getLocation().getName());

        if (position > 0) {

            Event previousEvent = eventList.get(position - 1);

            if (event.getStartTime().after(previousEvent.getStartTime()))
                holder.time.setVisibility(View.VISIBLE);

            else holder.time.setVisibility(View.INVISIBLE);
        }

        ParseFile photo = event.getEventPhoto();
        if (photo != null) {

            photo.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Palette.generateAsync(bitmap,
                            new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch vibrant =
                                            palette.getVibrantSwatch();
                                    if (vibrant != null) {

                                        event.setPaletteColor(vibrant.getRgb());
                                        holder.cardView.setBackground(setCustomBackground(vibrant.getRgb()));
                                        holder.name.setTextColor(
                                                Color.WHITE);
                                        holder.location.setTextColor(
                                                Color.parseColor("#B2FFFFFF"));
                                    }
                                }
                            });
                }
            });
        }

        return convertView;
    }

    private StateListDrawable setCustomBackground(int color) {

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(color + 0xC0000000));
        states.addState(new int[] {}, new ColorDrawable(color));

        return states;
    }

    private class ViewHolder {

        TextViewFont time, name, location;
        View cardView;

        private ViewHolder(View v) {

            cardView = v.findViewById(R.id.header_session);
            time = (TextViewFont) v.findViewById(R.id.schedule_time);
            name = (TextViewFont) v.findViewById(R.id.schedule_name);
            location = (TextViewFont) v.findViewById(R.id.schedule_location);
        }
    }


}
