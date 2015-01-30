package mx.mobile.junamex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import mx.mobile.adapters.MuseumAdapter;
import mx.mobile.model.MuseumItem;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 29/01/15.
 */
public class MuseumFragment extends Fragment {

    private ArrayList<MuseumItem> museumItems;
    private MuseumAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        museumItems = new ArrayList<>();
        adapter = new MuseumAdapter(getActivity(), museumItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (Utilities.isPortrait(getActivity()) && Utilities.isHandset(getActivity()))
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        else
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.museum_grid_columns), StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    public void getData() {

        ParseQuery<MuseumItem> query = new ParseQuery<>(MuseumItem.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<MuseumItem>() {
            @Override
            public void done(List<MuseumItem> newMuseumItems, ParseException e) {

                if (e == null)
                    updateUI(newMuseumItems);
                else
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<MuseumItem> newMuseumItems) {

        for (MuseumItem item : newMuseumItems) {
            if (!museumItems.contains(item)) {
                museumItems.add(item);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
