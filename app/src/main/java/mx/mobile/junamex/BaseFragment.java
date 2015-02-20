package mx.mobile.junamex;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by desarrollo16 on 20/02/15.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getDrawerPosition());
    }

    public abstract int getDrawerPosition();
}
