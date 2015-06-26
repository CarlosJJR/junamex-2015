package mx.mobiles.junamex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import mx.mobiles.adapters.ViewPagerAdapter;
import mx.mobiles.ui.PagerSlidingTabStrip;

/**
 * Created by desarrollo16 on 22/01/15.
 */
public class ScheduleViewPagerFragment extends Fragment {

    public static final String TAG = ScheduleViewPagerFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_WEEK) - 4;

        if (currentDay < 0)
            currentDay = 0;

        viewPager.setCurrentItem(currentDay);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        return view;
    }
}
