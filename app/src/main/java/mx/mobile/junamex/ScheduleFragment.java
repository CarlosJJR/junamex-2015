package mx.mobile.junamex;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by desarrollo16 on 20/01/15.
 */
public class ScheduleFragment extends ListActivity {

    ArrayAdapter<String> adapter;
    ArrayList<String> eventList;
    ArrayList<String> idList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idList = new ArrayList<>();
        eventList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventList);
        setListAdapter(adapter);
        initializeSystem();
    }

    private void initializeSystem() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {

                    for (ParseObject object : parseObjects) {
                        String id = object.getObjectId();
                        String phoneName = object.getString("name");
                        eventList.add(phoneName);
                        idList.add(id);
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    //Error
                    Toast.makeText(ScheduleFragment.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(ScheduleFragment.this, ScrollActivity.class);
        intent.putExtra("event_id", idList.get(position));
        startActivity(intent);
    }
}
