package mx.mobiles.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by desarrollo16 on 25/05/15.
 */
public class WidgetListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return (new WidgetListProvider(this.getApplicationContext()));
    }
}
