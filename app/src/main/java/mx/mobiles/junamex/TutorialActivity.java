package mx.mobiles.junamex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import me.relex.circleindicator.CircleIndicator;
import mx.mobiles.adapters.TutorialAdapter;
import mx.mobiles.utils.Utilities;


public class TutorialActivity extends ActionBarActivity implements ViewPager.PageTransformer, ViewPager.OnPageChangeListener, View.OnClickListener{

    private ImageButton doneButton;
    private boolean fromInfoPage;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tutorialSeen = sp.getBoolean("tutorial_seen", false);

        fromInfoPage = getIntent().getBooleanExtra(TELEPHONY_SERVICE, false);

        if (tutorialSeen && !fromInfoPage) {
            launchMainActivity();
        }

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));
        pager.setPageTransformer(false, this);

        CircleIndicator pageIndicator = (CircleIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(pager);
        pageIndicator.setOnPageChangeListener(this);

        doneButton = (ImageButton) findViewById(R.id.done_button);
        doneButton.setOnClickListener(this);
    }

    @Override
    public void transformPage(View view, float position) {

        View content = view.findViewById(R.id.page_content);

        ViewCompat.setTranslationX(view, view.getWidth() * -position);

        if(position <= -1.0f || position >= 1.0f) {
            ViewCompat.setAlpha(view, 0.0f);
        } else if (position < -0.5 || position > 0.5) {
            ViewCompat.setAlpha(view, 1.0f - Math.abs(position));
            ViewCompat.setAlpha(content, 0.0f);
            ViewCompat.setRotationY(content, 0.0f);
        } else if( position == 0.0f) {
            ViewCompat.setAlpha(view, 1.0f);
            ViewCompat.setAlpha(content, 1.0f);
            ViewCompat.setRotationY(content, 0);
        } else {
            ViewCompat.setAlpha(view, 1.0f - Math.abs(position));
            ViewCompat.setAlpha(content,  1.0f);
            ViewCompat.setRotationY(content, position * 180);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (Utilities.isLollipop()) {
            int paletteColor;

            switch (position) {

                case 1:
                    paletteColor = getResources().getColor(R.color.tutorial_red);
                    break;

                case 2:
                    paletteColor = getResources().getColor(R.color.tutorial_yellow);
                    break;

                case 3:
                    paletteColor = getResources().getColor(R.color.tutorial_green);
                    break;

                case 4:
                    paletteColor = getResources().getColor(R.color.tutorial_blue);
                    break;

                default:
                    paletteColor = getResources().getColor(R.color.color_primary);
            }
            getWindow().setStatusBarColor(Utilities.getSecondaryColor(paletteColor));
        }

        if (position == 5) {
            doneButton.setImageResource(R.drawable.ic_done);
        } else {
            doneButton.setImageResource(R.drawable.ic_next);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void launchMainActivity() {

        if (!fromInfoPage) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putBoolean("tutorial_seen", true).apply();
        }
        finish();
    }

    @Override
    public void onClick(View v) {

        int position = this.pager.getCurrentItem();
        if (position < 5) {
            this.pager.setCurrentItem(position + 1, true);
        } else {
            launchMainActivity();
        }
    }
}
