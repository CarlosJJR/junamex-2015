package mx.mobiles.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mx.mobiles.junamex.TutorialFragment;

/**
 * Created by desarrollo16 on 24/02/15.
 */
public class TutorialAdapter extends FragmentPagerAdapter {

    public TutorialAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return TutorialFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
