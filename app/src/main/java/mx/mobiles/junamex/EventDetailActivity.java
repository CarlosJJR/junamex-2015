package mx.mobiles.junamex;

import android.os.Bundle;

import mx.mobiles.utils.Utilities;

/**
 * Created by desarrollo16 on 13/01/15.
 */
public class EventDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EventDetailFragment detailFragment = EventDetailFragment.newInstance(getIntent().getExtras());
        if (Utilities.isHandset(this)) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, detailFragment)
                    .commit();
        } else {
            detailFragment.show(getSupportFragmentManager(), "dialog");
        }


        detailFragment.setOnFragmentDismissedListener(new EventDetailFragment.OnFragmentDismissedListener() {
            @Override
            public void onFragmentDismissed() {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_container_frame;
    }
}
