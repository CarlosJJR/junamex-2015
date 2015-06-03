package mx.mobiles.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import mx.mobiles.junamex.EventDetailActivity;
import mx.mobiles.junamex.R;

/**
 * Created by desarrollo16 on 18/05/15.
 */
public class WidgetController extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            // Specify the service to provide data for the collection widget.  Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            Intent intent = new Intent(context, WidgetListService.class)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_schedule);
            rv.setRemoteAdapter(appWidgetId, R.id.widget_list_view, intent);

            // Set the empty view to be displayed if the collection is empty.  It's a sibling
            // view of the collection view.
            rv.setEmptyView(R.id.widget_list_view, R.id.empty_view);

            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek < Calendar.WEDNESDAY)
                dayOfWeek = Calendar.WEDNESDAY;

            String day = context.getString(R.string.day) + " " + (dayOfWeek - 3);

            rv.setTextViewText(R.id.widget_title, day);

            Intent showEventIntent = new Intent(context, EventDetailActivity.class);

            PendingIntent showEvent = TaskStackBuilder.create(context)
                    .addParentStack(EventDetailActivity.class)
                    .addNextIntent(showEventIntent)
                    .getPendingIntent((int) System.currentTimeMillis(), 0);
            rv.setPendingIntentTemplate(R.id.widget_list_view, showEvent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
