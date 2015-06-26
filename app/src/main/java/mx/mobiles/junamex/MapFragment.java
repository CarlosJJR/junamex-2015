package mx.mobiles.junamex;

import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.List;

import mx.mobiles.adapters.NavigationDrawerAdapter;
import mx.mobiles.model.Location;
import mx.mobiles.utils.Utilities;

/**
 * Created by desarrollo16 on 28/01/15.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, SearchView.OnQueryTextListener, GoogleMap.OnMapClickListener {

    public static final String MARKER_KEY = "marker";
    public static final String TAG = MapFragment.class.getSimpleName();

    private CursorAdapter suggestionsAdapter;
    private List<Location> locationsList;
    private GoogleMap map;
    private String searchMarkerId;

    public static MapFragment newInstance(String markerId) {

        Bundle params = new Bundle();
        params.putString(MARKER_KEY, markerId);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(params);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            searchMarkerId = args.getString(MARKER_KEY);
        }


        String[] usedColumns = new String[] {Location.NAME};
        int[] ids = new int[] {android.R.id.text1};
        suggestionsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_dropdown, getNewCursor(), usedColumns, ids, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getMapAsync(this);
    }

    private void getData() {
        ParseQuery<Location> query = new ParseQuery<>(Location.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> locations, ParseException e) {

                if (e == null) {
                    updateMarkers(locations);
                    locationsList = locations;

                    if (searchMarkerId != null)
                        highlightSearchResult(searchMarkerId);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(18.902072, -98.980203), 17));

        map.setOnMapClickListener(this);
        this.map = map;
        getData();
    }

    private void updateMarkers(List<Location> locations) {

        if (map != null) {
            map.clear();
            for (Location location : locations) {

                ParseGeoPoint coordinates = location.getCoordinates();
                Marker marker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2))
                        .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                        .title(location.getName()));

                location.setMarker(marker);
            }
        }
    }

    private void updateSuggestions(String query) {

        if (locationsList == null)
            return;

        MatrixCursor suggestionCursor = (MatrixCursor) getNewCursor();
        for (int i = 0; i < locationsList.size(); i++) {
            Location location = locationsList.get(i);

            if (location.getName().toLowerCase().contains(query.toLowerCase()))
                suggestionCursor.addRow(new String[]{String.valueOf(i), location.getName(), location.getObjectId()});
        }
        suggestionsAdapter.swapCursor(suggestionCursor);
        suggestionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            ((MainActivity) activity).onSectionAttached(NavigationDrawerAdapter.MAP);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        ((AppCompatActivity)activity).supportInvalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Utilities.hideKeyboard(getActivity(), searchView);
                Cursor cursor = suggestionsAdapter.getCursor();

                if (cursor.moveToPosition(i)) {
                    String id = cursor.getString(cursor.getColumnIndex(Location.OBJECT_ID));
                    highlightSearchResult(id);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        updateSuggestions(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        updateSuggestions(s);
        return true;
    }

    private Cursor getNewCursor() {

        String[] columns = new String[] {Location.ID, Location.NAME, Location.OBJECT_ID};
        return new MatrixCursor(columns);
    }

    private void highlightSearchResult(String markerId) {

        for (Location location : locationsList) {

            float alpha = 0.5f;
            if (location.getObjectId().equals(markerId)) {
                alpha = 1;
                getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(location.getMarker().getPosition(), 18), 750, null);
                location.getMarker().showInfoWindow();
            }
            location.getMarker().setAlpha(alpha);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        for (Location location : locationsList) {
            float alpha = 1;
            location.getMarker().setAlpha(alpha);
        }
    }

}
