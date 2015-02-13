package mx.mobile.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

import mx.mobile.junamex.EventDetailActivity;
import mx.mobile.junamex.R;
import mx.mobile.model.Event;

/**
 * Created by desarrollo16 on 20/01/15.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<Event> eventList;
    private Activity activity;
    private int lastPosition = -1;

    public ScheduleAdapter(Activity activity, List<Event> eventList) {
        this.eventList = eventList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Event event = eventList.get(position);

        holder.time.setText(event.getStartTimeString());
        holder.name.setText(event.getEventName());
        holder.location.setText(event.getLocation().getName());

        if (position > 0) {

            Event previousEvent = eventList.get(position - 1);

            if (event.getStartTime().after(previousEvent.getStartTime()))
                holder.time.setVisibility(View.VISIBLE);

            else holder.time.setVisibility(View.INVISIBLE);
        }

        setAnimation(holder.cardView, position);

        GetEventColor task = new GetEventColor();
        task.setOnEventColorObtainedListener(new GetEventColor.OnEventColorObtainedListener() {
            @Override
            public void onEventColorObtained(int eventColor) {

                StateListDrawable background;
                int titleColor, subtitleColor, cardBackgroundColor;

                if (eventColor != 0) {
                    background = setCustomBackground(eventColor);
                    titleColor = Color.WHITE;
                    subtitleColor =  Color.parseColor("#B2FFFFFF");
                    cardBackgroundColor = eventColor;
                } else {
                    background = (StateListDrawable) activity.getResources().getDrawable(R.drawable.schedule_item_selector);
                    titleColor = activity.getResources().getColor(R.color.primary_text_color);
                    subtitleColor = activity.getResources().getColor(R.color.secondary_text_color);
                    cardBackgroundColor = activity.getResources().getColor(R.color.cardview_light_background);
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    holder.cardContent.setBackgroundDrawable(background);
                else
                    holder.cardContent.setBackground(background);

                holder.cardView.setCardBackgroundColor(cardBackgroundColor);
                holder.name.setTextColor(titleColor);
                holder.location.setTextColor(subtitleColor);
            }
        });
        task.execute(event);

        holder.setOnEventClickListener(new ViewHolder.OnEventClickListener() {
            @Override
            public void onEventClicked(int position) {
                Intent intent = new Intent(activity, EventDetailActivity.class);

                Event event = eventList.get(position);
                intent.putExtra(EventDetailActivity.EVENT_KEY, event.getObjectId());

                if (event.getPaletteColor() != 0)
                    intent.putExtra(EventDetailActivity.PALETTE_KEY, event.getPaletteColor());

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private StateListDrawable setCustomBackground(int color) {

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(color + 0xFF080808));
        states.addState(new int[] {}, new ColorDrawable(Color.TRANSPARENT));

        return states;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position != lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
        }
        lastPosition = position;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //RecyclerView.ViewHolder implementation

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView time, name, location;
        CardView cardView;
        View cardContent;

        OnEventClickListener clickListener;

        private ViewHolder(View v) {
            super(v);

            cardView = (CardView) v.findViewById(R.id.header_session);
            time = (TextView) v.findViewById(R.id.schedule_time);
            name = (TextView) v.findViewById(R.id.schedule_name);
            location = (TextView) v.findViewById(R.id.schedule_location);
            cardContent = v.findViewById(R.id.card_content);

            v.setOnClickListener(this);
        }

        public interface OnEventClickListener {
            public void onEventClicked(int position);
        }

        public void setOnEventClickListener(OnEventClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onEventClicked(getPosition());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Get palette color in background

    private static class GetEventColor extends AsyncTask<Event, Void, Integer> {

        private OnEventColorObtainedListener colorObtainedListener;

        @Override
        protected Integer doInBackground(Event... params) {

            Event event = params[0];
            int color = event.getPaletteColor();

            if (color != 0)
                return color;

            ParseFile photo = event.getEventPhoto();
            if (photo != null) {

                try {
                    byte[] photoData = photo.getData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                    Palette palette = Palette.generate(bitmap);
                    Palette.Swatch vibrant = palette.getVibrantSwatch();

                    if (vibrant != null) {
                        color = vibrant.getRgb();
                        event.setPaletteColor(color);
                        event.saveInBackground();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return color;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            colorObtainedListener.onEventColorObtained(integer);
        }

        public interface OnEventColorObtainedListener {
            public void onEventColorObtained(int eventColor);
        }

        public void setOnEventColorObtainedListener(OnEventColorObtainedListener colorObtainedListener) {
            this.colorObtainedListener = colorObtainedListener;
        }
    }
}
