package mx.mobiles.junamex;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by desarrollo16 on 23/04/15.
 */
public class CountdownActivity extends BaseActivity {

    TextView daysCounter, hoursCounter, minutesCounter, secondsCounter;
    TextView daysLabel, hoursLabel, minutesLabel, secondsLabel;

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

        daysCounter = (TextView) findViewById(R.id.days_counter);
        hoursCounter = (TextView) findViewById(R.id.hours_counter);
        minutesCounter = (TextView) findViewById(R.id.minutes_counter);
        secondsCounter = (TextView) findViewById(R.id.seconds_counter);

        daysLabel = (TextView) findViewById(R.id.days_indicator);
        hoursLabel = (TextView) findViewById(R.id.hours_indicator);
        minutesLabel = (TextView) findViewById(R.id.minutes_indicator);
        secondsLabel = (TextView) findViewById(R.id.seconds_indicator);

        calculateDaysToJunamex();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_countdown;
    }

    private void calculateDaysToJunamex() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);
        try {
            Date startDate = dateFormat.parse(JUNAMEX_START);
            long offset = startDate.getTime() - new Date().getTime();

            new CountDownTimer(offset, 1000) {

                @Override
                public void onTick(long difference) {

                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long daysInMilli = hoursInMilli * 24;

                    long days = difference / daysInMilli;
                    difference = difference % daysInMilli;

                    long hours = difference / hoursInMilli;
                    difference = difference % hoursInMilli;

                    long minutes = difference / minutesInMilli;
                    difference = difference % minutesInMilli;

                    long seconds = difference / secondsInMilli;

                    daysCounter.setText(String.valueOf(days));
                    hoursCounter.setText(String.valueOf(hours));
                    minutesCounter.setText(String.valueOf(minutes));
                    secondsCounter.setText(String.valueOf(seconds));

                    daysLabel.setText(getResources().getQuantityString(R.plurals.days, (int)days));
                    hoursLabel.setText(getResources().getQuantityString(R.plurals.hours, (int)hours));
                    minutesLabel.setText(getResources().getQuantityString(R.plurals.minutes, (int)minutes));
                    secondsLabel.setText(getResources().getQuantityString(R.plurals.seconds, (int)seconds));
                }

                @Override
                public void onFinish() {

                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
