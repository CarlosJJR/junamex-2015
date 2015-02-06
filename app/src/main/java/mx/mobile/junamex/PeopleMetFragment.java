package mx.mobile.junamex;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.mobile.adapters.PeopleAdapter;
import mx.mobile.model.PeopleMet;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleMetFragment extends Fragment {

    public static final int REQUEST_CODE = 1234;

    private ArrayList<PeopleMet> peopleList;
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
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        view.findViewById(R.id.empty_view).setVisibility(View.GONE);
        view.findViewById(R.id.error_view).setVisibility(View.GONE);

        emptyView = view.findViewById(R.id.no_people_view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (Utilities.isPortrait(getActivity()) && Utilities.isHandset(getActivity()))
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator scanner = IntentIntegrator.forSupportFragment(PeopleMetFragment.this);
                scanner.setLegacyCaptureLayout(R.layout.activity_barcode_scanner)
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                        .setScanningRectangle(500, 500)
                        .initiateScan();
            }
        });

        new LoadPeople().execute(((MainActivity) getActivity()).getDB());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE:
                    peopleList.clear();
                    new LoadPeople().execute(((MainActivity) getActivity()).getDB());
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
        intent.putExtra("new_contact", rawString.replace("\\", ""));
        startActivityForResult(intent, REQUEST_CODE);
    }

    private class LoadPeople extends AsyncTask<SQLiteDatabase, Void, Void> {

        @Override
        protected Void doInBackground(SQLiteDatabase... params) {

            ArrayList<PeopleMet> list = PeopleMet.getAll(params[0]);

            for (PeopleMet met : list) {
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
