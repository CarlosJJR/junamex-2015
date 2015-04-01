package mx.mobiles.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.text.DateFormatSymbols;

import mx.mobiles.junamex.ScheduleFragment;

/**
 * Created by desarrollo16 on 22/01/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    String[] namesOfDays;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        namesOfDays = DateFormatSymbols.getInstance().getWeekdays();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ScheduleFragment.newInstance(ScheduleFragment.TUES);

            case 1:
                return ScheduleFragment.newInstance(ScheduleFragment.WED);

            case 2:
                return ScheduleFragment.newInstance(ScheduleFragment.THUR);

            case 3:
                return ScheduleFragment.newInstance(ScheduleFragment.FRI);

            case 4:
                return ScheduleFragment.newInstance(ScheduleFragment.SAT);

            default:
                return ScheduleFragment.newInstance(ScheduleFragment.TUES);
        }
    }

    @Override
    public int getCount() {
        return ScheduleFragment.DAYS_OF_JUNAMEX;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return namesOfDays[position + 3];
    }
}
