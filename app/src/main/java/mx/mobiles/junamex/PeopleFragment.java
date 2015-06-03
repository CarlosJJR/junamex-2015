package mx.mobiles.junamex;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import mx.mobiles.adapters.NavigationDrawerAdapter;
import mx.mobiles.adapters.PeopleAdapter;
import mx.mobiles.model.People;
import mx.mobiles.ui.VerticalQRScanner;
import mx.mobiles.utils.SimpleDividerDecorator;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleFragment extends BaseFragment {

    public static final int REQUEST_CODE = 1234;

    public static final String TAG = PeopleFragment.class.getSimpleName();

    private ArrayList<People> peopleList;
    private PeopleAdapter adapter;
    private View emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        peopleList = new ArrayList<>();
        adapter = new PeopleAdapter(getActivity(), peopleList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        emptyView = view.findViewById(R.id.no_people_view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerDecorator(getActivity()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator scanner = IntentIntegrator.forSupportFragment(PeopleFragment.this);
                scanner .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                        .setCaptureActivity(VerticalQRScanner.class)
                        .setOrientationLocked(false)
                        .setPrompt(getString(R.string.zxing_scanner_message))
                        .initiateScan();
            }
        });

        new LoadPeople().execute(((MainActivity) getActivity()).getDB());

        return view;
    }

    @Override
    public int getDrawerPosition() {
        return NavigationDrawerAdapter.PEOPLE;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE:
                    peopleList.clear();
                    new LoadPeople().execute(((BaseActivity) getActivity()).getDB());
                    break;

                case IntentIntegrator.REQUEST_CODE:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    onCodeScanned(result);
                    break;
            }
        }
    }

    public void onCodeScanned(IntentResult data) {

        String rawString = data.getContents();

        Intent intent = new Intent(getActivity(), PeopleAddEditActivity.class);
        intent.putExtra(PeopleAddEditActivity.CONTACT_KEY, rawString.replace("\\", ""));
        startActivityForResult(intent, REQUEST_CODE);
    }

    private class LoadPeople extends AsyncTask<SQLiteDatabase, Void, Void> {

        @Override
        protected Void doInBackground(SQLiteDatabase... params) {

            ArrayList<People> list = People.getAll(params[0]);

            for (People met : list) {
                peopleList.add(met);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int visibility = peopleList.isEmpty() ? View.VISIBLE : View.GONE;
                emptyView.setVisibility(visibility);

            adapter.notifyDataSetChanged();
        }
    }
}
