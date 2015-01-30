package mx.mobile.junamex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mx.mobile.adapters.PeopleAdapter;
import mx.mobile.model.PeopleMet;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleMetFragment extends Fragment {

    private ArrayList<PeopleMet> peopleList;
    private PeopleAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        peopleList = new ArrayList<>();
        adapter = new PeopleAdapter(getActivity(), peopleList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (Utilities.isPortrait(getActivity()) && Utilities.isHandset(getActivity()))
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        recyclerView.setAdapter(adapter);

        return view;
    }


}
