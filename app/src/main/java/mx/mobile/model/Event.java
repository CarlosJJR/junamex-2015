package mx.mobile.model;

import android.content.Context;
import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by desarrollo16 on 27/01/15.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String NAME = "name";
    public static final String ABSTRACT = "description";
    public static final String START_TIME = "startTime";
    public static final String DURATION = "durationMinutes";
    public static final String PHOTO = "eventPhoto";
    public static final String PALETTE_COLOR = "paletteColor";
    public static final String SPEAKER = "speaker";
    public static final String LOCATION = "location";

    public static final String UPDATED_AT = "updatedAt";

    public Event() {
        super();
    }

    public String getEventName() {
        return getString(NAME);
    }

    public void setEventName(String eventName) {
        put(NAME, eventName);
    }

    public String getEventAbstract() {
        return getString(ABSTRACT);
    }

    public void setEventAbstract(String eventAbstract) {
        put(ABSTRACT, eventAbstract);
    }

    public Date getStartTime() {
        return getDate(START_TIME);
    }

    public void setStartTime(Date startTime) {
        put(START_TIME, startTime);
    }

    public int getDurationMinutes() {
        return getInt(DURATION);
    }

    public void setDurationMinutes(int durationMinutes) {
        put(DURATION, durationMinutes);
    }

    public ParseFile getEventPhoto() {
        return getParseFile(PHOTO);
    }

    public int getPaletteColor() {
        return getInt(PALETTE_COLOR);
    }

    public void setPaletteColor(int paletteColor) {
        put(PALETTE_COLOR, paletteColor);
    }

    public Speaker getSpeaker() {
        return (Speaker) getParseObject(SPEAKER);
    }

    public Location getLocation() {
        return (Location) getParseObject(LOCATION);
    }

    public boolean hasPhoto() {
        return getEventPhoto() != null;
    }

    public boolean hasSpeaker() {
        return getSpeaker() != null;
    }

    public String getStartTimeString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(getStartTime());
    }

    public String getFormattedTime(Context context) {

        Calendar c = Calendar.getInstance();
        c.setTime(convertFromUTC(getStartTime()));
        Date start = c.getTime();

        c.add(Calendar.MINUTE, getDurationMinutes());
        Date end = c.getTime();

        return DateUtils.formatDateRange(
                context,
                start.getTime(),
                end.getTime(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_TIME | DateUtils.FORMAT_12HOUR);
    }


    public Date convertFromUTC(Date dt) {

        TimeZone fromTimezone = TimeZone.getDefault();
        TimeZone toTimezone = TimeZone.getTimeZone("UTC");

        long fromOffset = fromTimezone.getOffset(dt.getTime());
        long toOffset = toTimezone.getOffset(dt.getTime());

        long convertedTime = dt.getTime() - (fromOffset - toOffset);
        return new Date(convertedTime);
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass().isInstance(this) && ((Event) o).getUpdatedAt() == this.getUpdatedAt();
    }
}
