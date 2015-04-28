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
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.net.URI;
import java.util.Calendar;

import mx.mobiles.junamex.EventDetailActivity;
import mx.mobiles.junamex.MapActivity;
import mx.mobiles.junamex.MapFragment;
import mx.mobiles.junamex.R;
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

        //Set up a PendingIntent for the "Show map" action
        Intent mapIntent = new Intent(context, MapActivity.class);
        mapIntent.putExtra(MapFragment.MARKER_KEY, eventLocationId);
        PendingIntent showInMap = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), mapIntent, 0);

        //Set up the PendingIntent for when the user clicks the notification
        Intent showEventIntent = new Intent(context, EventDetailActivity.class);
        showEventIntent.putExtra(Event.ID, eventId);
        if (paletteColor != 0)
            showEventIntent.putExtra(Event.PALETTE_COLOR, paletteColor);
        PendingIntent showEvent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), showEventIntent, 0);

        //Set up the notification content and show it
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_nokia);

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(context);
        notBuilder.setContentTitle("Don't miss your event:")
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
        boolean vibrateEnabled = prefs.getBoolean("vibration_enabled", true);
        if (vibrateEnabled)
            notBuilder.setVibrate(new long[]{150});

        Notification notification = notBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(eventTag, notification);
    }
}
