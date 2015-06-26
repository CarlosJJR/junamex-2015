package mx.mobiles.junamex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by desarrollo16 on 17/02/15.
 */
public class ScheduleHorizontalScrollFragment extends Fragment {

    public static final String TAG = ScheduleHorizontalScrollFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_scroll, container, false);

        ScheduleFragment wednesday = ScheduleFragment.newInstance(ScheduleFragment.WED);
        ScheduleFragment thursday = ScheduleFragment.newInstance(ScheduleFragment.THUR);
        ScheduleFragment friday = ScheduleFragment.newInstance(ScheduleFragment.FRI);
        ScheduleFragment saturday = ScheduleFragment.newInstance(ScheduleFragment.SAT);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.schedule_wed, wednesday)
                .add(R.id.schedule_thur, thursday)
                .add(R.id.schedule_fri, friday)
                .add(R.id.schedule_sat, saturday);

        transaction.commit();

        return view;
    }
}
