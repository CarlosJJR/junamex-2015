package mx.mobile.junamex;

import android.os.Bundle;
import android.view.View;

/**
 * Created by desarrollo16 on 10/02/15.
 */
public class MapActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle("");

        String markerId = getIntent().getStringExtra(MapFragment.MARKER_KEY);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, MapFragment.newInstance(markerId))
                .commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_map;
    }
}
