package mx.mobiles.utils;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import mx.mobiles.junamex.MainActivity;

/**
 * Created by desarrollo16 on 21/01/15.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
