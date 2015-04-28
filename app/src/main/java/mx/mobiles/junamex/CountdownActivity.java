package mx.mobiles.junamex;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by desarrollo16 on 23/04/15.
 */
public class CountdownActivity extends BaseActivity {

    TextView counterLabel;

    private static final String JUNAMEX_START = "15/07/15 10:00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        counterLabel = (TextView) findViewById(R.id.countdown_label);

        calculateDaysToJunamex();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_countdown;
    }

    private void calculateDaysToJunamex() {

        String counterMessage = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

        try {
            Date startDate = dateFormat.parse(JUNAMEX_START);
            Date today = new Date();

            //milliseconds
            long difference = startDate.getTime() - today.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long days = difference / daysInMilli;
            difference = difference % daysInMilli;

            long hours = difference / hoursInMilli;
            difference = difference % hoursInMilli;

            long minutes = difference / minutesInMilli;

            if (days > 0) {
                counterMessage += getResources().getQuantityString(R.plurals.days, (int)days, (int)days);
            }
            if (days < 5) {
                if (hours > 0) {
                    if (!counterMessage.isEmpty())
                        counterMessage += ",\n";

                    counterMessage += getResources().getQuantityString(R.plurals.hours, (int) hours, (int)hours);
                }
            }

            if (days <= 1) {
                if (!counterMessage.isEmpty())
                    counterMessage += ",\n";

                counterMessage += getResources().getQuantityString(R.plurals.minutes, (int) minutes, (int)minutes);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        counterLabel.setText(counterMessage);
    }
}
