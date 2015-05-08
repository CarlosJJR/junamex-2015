package mx.mobiles.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import mx.mobiles.utils.LocalNotificationsReceiver;

/**
 * Created by desarrollo16 on 27/01/15.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String ID = "event_id";
    public static final String DB_ID = "id";
    public static final String NAME = "name";
    public static final String ABSTRACT = "abstract";
    public static final String START_TIME = "startTime";
    public static final String DURATION = "durationMinutes";
    public static final String PHOTO = "eventPhoto";
    public static final String PALETTE_COLOR = "paletteColor";
    public static final String SPEAKER = "speaker";
    public static final String LOCATION = "location";

    public static final String UPDATED_AT = "updatedAt";
    public static final String TAG = "tag";

    public static final String TABLE = "event";
    public static final String NOTIFICATION_STATUS = "notifications";

    public static final String CREATE_TABLE = "CREATE TABLE event ("
            +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            +"event_id varchar(250) NOT NULL,"
            +"notifications integer)";

    private int databaseId;

    public Event() {
        super();
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
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

    public boolean isNotificationEnabled(SQLiteDatabase database) {
        Cursor cursor = database.query(TABLE, null, ID + "=?", new String[]{getObjectId()}, null, null, null);
        boolean hasNotification = false;

        if (cursor.moveToFirst()) {
            hasNotification = cursor.getInt(cursor.getColumnIndex(NOTIFICATION_STATUS)) == 1;
            int dbId = cursor.getInt(cursor.getColumnIndex(DB_ID));
            setDatabaseId(dbId);
        }

        cursor.close();
        return hasNotification;
    }

    public void setNotificationEnabled(SQLiteDatabase database) {
        ContentValues cv = new ContentValues();

        cv.put(ID, getObjectId());
        cv.put(NOTIFICATION_STATUS, 1);

        Cursor cursor = database.query(TABLE, null, ID + "=?", new String[]{getObjectId()}, null, null, null);
        if (cursor.moveToFirst())
            database.update(TABLE, cv, ID + "=?", new String[]{getObjectId()});
        else {
            int newId = (int) database.insert(TABLE, null, cv);
            setDatabaseId(newId);
        }

        cursor.close();
    }

    public void setAlarm(Context context) {

        //Create a PendingIntent to wrap the intent
        PendingIntent alarmIntent = getIntentForAlarm(context);

        //Set the time when the notification will be fired
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertFromUTC(getStartTime()));

        //Adjust the time for 10 minutes before every event
        int notificationTime = 10;
        calendar.add(Calendar.MINUTE, -notificationTime);
//        calendar.add(Calendar.DAY_OF_YEAR, -78);
        Log.i("AlarmManager", "Event alarm set for : " + calendar.toString());

        //Set up the alarm for notification
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), alarmIntent);
    }

    public void cancelAlarm(Context context) {

        //Create a PendingIntent to wrap the intent
        PendingIntent alarmIntent = getIntentForAlarm(context);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(alarmIntent);
    }

    public void rescheduleAlarm(Context context) {

        this.cancelAlarm(context);
        this.setAlarm(context);
    }

    public static ArrayList<HashMap<String, String>> getAllNotificationsEnabled(SQLiteDatabase database) {

        ArrayList<HashMap<String, String>> eventIDs = new ArrayList<>();
        Cursor cursor = database.query(TABLE, null, NOTIFICATION_STATUS + "=?", new String[]{"1"}, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(ID));
            String dbId = cursor.getString(cursor.getColumnIndex(DB_ID));

            HashMap<String, String> databaseObjects = new HashMap<>(2);
            databaseObjects.put(ID, id);
            databaseObjects.put(DB_ID, dbId);
            eventIDs.add(databaseObjects);
        }
        cursor.close();
        return eventIDs;
    }

    private PendingIntent getIntentForAlarm(Context context) {

        //Create the intent for the Receiver that will catch the notification
        Intent intent = new Intent(context, LocalNotificationsReceiver.class);

        //Add event name and location to build the notification
        intent.putExtra(ID, getObjectId());
        intent.putExtra(DB_ID, getDatabaseId());
        intent.putExtra(NAME, getEventName());
        intent.putExtra(ABSTRACT, getEventAbstract());
        intent.putExtra(PALETTE_COLOR, getPaletteColor());
        intent.putExtra(LOCATION, getLocation().getObjectId());

        //Create a PendingIntent to wrap the intent
        return PendingIntent.getBroadcast(context, getDatabaseId(), intent, 0);
    }
}
