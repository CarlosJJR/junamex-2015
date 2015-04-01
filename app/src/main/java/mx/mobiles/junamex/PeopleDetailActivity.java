package mx.mobiles.junamex;

import android.os.Bundle;

import mx.mobiles.model.PeopleMet;

/**
 * Created by desarrollo16 on 05/03/15.
 */
public class PeopleDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        PeopleDetailFragment detailFragment = PeopleDetailFragment.newInstance(getIntent().getExtras().getInt(PeopleMet.TABLE, 1));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, detailFragment)
                .commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_container_frame;
    }
}
