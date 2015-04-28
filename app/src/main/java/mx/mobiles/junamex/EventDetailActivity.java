package mx.mobiles.junamex;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by desarrollo16 on 13/01/15.
 */
public class EventDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EventDetailFragment detailFragment = EventDetailFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, detailFragment)
                .commit();

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
