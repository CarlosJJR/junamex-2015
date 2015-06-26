package mx.mobiles.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.net.URI;
import java.util.Calendar;
import java.util.Objects;

import mx.mobiles.junamex.EventDetailActivity;
import mx.mobiles.junamex.MainActivity;
import mx.mobiles.junamex.MapActivity;
import mx.mobiles.junamex.MapFragment;
import mx.mobiles.junamex.R;
import mx.mobiles.junamex.SettingsActivity;
import mx.mobiles.model.Event;

/**
 * Created by desarrollo16 on 23/04/15.
 */
public class LocalNotificationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Get event data from the intent
        int eventTag = intent.getIntExtra(Event.DB_ID, 0);
        int paletteColor = intent.getIntExtra(Event.PALETTE_COLOR, context.getResources().getColor(R.color.color_primary));
        String eventId = intent.getStringExtra(Event.ID);
        String eventName = intent.getStringExtra(Event.NAME);
        String eventLocationId = intent.getStringExtra(Event.LOCATION);
        String eventAbstract = intent.getStringExtra(Event.ABSTRACT);

        //Create Bundle to add on the PendingIntents
        Bundle extras = new Bundle();
        extras.putString(MapFragment.MARKER_KEY, eventLocationId);
        extras.putString(Event.ID, eventId);
        if (paletteColor != 0)
            extras.putInt(Event.PALETTE_COLOR, paletteColor);

        //Set up a PendingIntent for the "Show map" action
        Intent mapIntent = new Intent(context, MapActivity.class);
        mapIntent.putExtras(extras);
        PendingIntent showInMap = TaskStackBuilder.create(context)
                .addParentStack(MapActivity.class)
                .addNextIntent(mapIntent)
                .getPendingIntent((int) System.currentTimeMillis(), 0);
//        PendingIntent showInMap = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), mapIntent, 0);

        //Set up the PendingIntent for when the user clicks the notification
        Intent showEventIntent = new Intent(context, EventDetailActivity.class);
        showEventIntent.putExtras(extras);
        PendingIntent showEvent = TaskStackBuilder.create(context)
                .addParentStack(EventDetailActivity.class)
                .addNextIntent(showEventIntent)
                .getPendingIntent((int) System.currentTimeMillis(), 0);

        //Set up the notification content and show it
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_nokia);

        //Make sure paletteColor has a value for the notification color
        if (paletteColor == 0)
            paletteColor = context.getResources().getColor(R.color.color_primary);

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(context);
        notBuilder.setContentTitle(context.getString(R.string.notification_title))
                .setContentText(eventName)
                .setContentIntent(showEvent)
                .setColor(paletteColor)
                .setSound(sound)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(eventName + "\n\n" + eventAbstract))
                .setSmallIcon(R.drawable.ic_notifications_on)
                .addAction(R.drawable.ic_map, context.getString(R.string.show_map), showInMap)
                .setAutoCancel(true);

        //Check user settings to see if notification should vibrate or not
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrateEnabled = prefs.getBoolean(SettingsActivity.VIBRATION_ENABLED, true);
        if (vibrateEnabled)
            notBuilder.setVibrate(new long[]{150, 150});

        Notification notification = notBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(eventTag, notification);
    }
}
