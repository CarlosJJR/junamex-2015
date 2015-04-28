package mx.mobiles.junamex;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.parse.ParseFacebookUtils;

import mx.mobiles.utils.Utilities;

import static mx.mobiles.adapters.NavigationDrawerAdapter.*;


public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean requestingMapPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ArrayList<String> permissions = new ArrayList<>();
//        permissions.add("email");
//        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException err) {
//                if (user == null) {
//                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
//                } else if (user.isNew()) {
//                    Log.d("MyApp", "User signed up and logged in through Facebook!");
//                } else {
//                    Log.d("MyApp", "User logged in through Facebook!");
//                }
//            }
//        });

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {

            case SCHEDULE:
                if (Utilities.isHandset(this))
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new ScheduleViewPagerFragment(), ScheduleViewPagerFragment.TAG)
                            .commit();
                else
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new ScheduleHorizontalScrollFragment(), ScheduleHorizontalScrollFragment.TAG)
                            .commit();
                break;

            case MAP:
                if (requestingMapPermission) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new MapFragment(), MapFragment.TAG)
                            .commit();
                    requestingMapPermission = false;
                } else
                    requestingMapPermission = true;
                break;

            case PEOPLE:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PeopleFragment(), PeopleFragment.TAG)
                        .commit();
                break;

            case MUSEUM:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new MuseumFragment(), MuseumFragment.TAG)
                        .commit();
                break;

            case SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case ABOUT:
                startActivity(new Intent(this, AboutActivity.class));
                break;

            case COUNTDOWN:
                startActivity(new Intent(this, CountdownActivity.class));
                break;

            default:
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position))
                    .commit();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void onSectionAttached(int number) {

        mTitle = getResources().getStringArray(R.array.navigation_drawer_items)[number];
    }

    public void addMap() {
        if (requestingMapPermission)
            onNavigationDrawerItemSelected(MAP);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
