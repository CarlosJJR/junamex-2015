package mx.mobile.junamex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import mx.mobile.adapters.MuseumAdapter;
import mx.mobile.adapters.NavigationDrawerAdapter;
import mx.mobile.model.MuseumItem;
import mx.mobile.utils.RecyclerViewDividers;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 29/01/15.
 */
public class MuseumFragment extends BaseFragment {

    private ArrayList<MuseumItem> museumItems;
    private MuseumAdapter adapter;
    private View errorView, emptyView, loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        museumItems = new ArrayList<>();
        adapter = new MuseumAdapter(getActivity(), museumItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        loadingView = view.findViewById(android.R.id.progress);

        emptyView = view.findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);

        errorView = view.findViewById(R.id.error_view);
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorView.setVisibility(View.GONE);
                getData();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (Utilities.isPortrait(getActivity()) && Utilities.isHandset(getActivity())) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            if (Utilities.isLollipop())
                recyclerView.addItemDecoration(new RecyclerViewDividers(8, 8, 0, 0));
        }
        else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.museum_grid_columns), StaggeredGridLayoutManager.VERTICAL));

            if (Utilities.isLollipop())
                recyclerView.addItemDecoration(new RecyclerViewDividers(8, 8, 8, 8));
        }

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getData();
    }

    @Override
    public int getDrawerPosition() {
        return NavigationDrawerAdapter.MUSEUM;
    }

    public void getData() {

        loadingView.setVisibility(View.VISIBLE);
        ParseQuery<MuseumItem> query = new ParseQuery<>(MuseumItem.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<MuseumItem>() {
            @Override
            public void done(List<MuseumItem> newMuseumItems, ParseException e) {

                if (e == null)
                    updateUI(newMuseumItems);
                else {
                    if (isAdded())
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    if (e.getCode() != ParseException.CACHE_MISS) {
                        loadingView.setVisibility(View.GONE);
                        errorView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void updateUI(List<MuseumItem> newMuseumItems) {

        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);

        if (newMuseumItems.isEmpty())
            emptyView.setVisibility(View.VISIBLE);

        for (MuseumItem item : newMuseumItems) {
            if (!museumItems.contains(item)) {
                museumItems.add(item);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.people_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.edit);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share_qr) {

            IntentIntegrator scanner = IntentIntegrator.forSupportFragment(this);
            scanner.setLegacyCaptureLayout(R.layout.activity_barcode_scanner)
                    .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                    .setScanningRectangle(512, 512)
                    .initiateScan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            Intent intent = new Intent(getActivity(), MuseumDetailActivity.class);
            intent.putExtra("item_id", result.getContents());
            startActivity(intent);
        }
    }
}
