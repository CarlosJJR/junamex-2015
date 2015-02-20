package mx.mobile.junamex;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.parse.ParseQuery;

import mx.mobile.db.DatabaseHelper;

/**
 * Created by desarrollo16 on 29/01/15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    public Toolbar toolbar;
    public SQLiteDatabase database;
    public DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getLayoutResource();

    public SQLiteDatabase getDB() {
        if (database == null || !database.isOpen())
            openDB(this);
        return database;
    }

    public boolean openDB(Context context) {
        if (mDbHelper != null)
            mDbHelper.close();

        mDbHelper = new DatabaseHelper(context);
        try {
            database = mDbHelper.getWritableDatabase();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public ParseQuery.CachePolicy getCachePolicy() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("auto_refresh_data", true) ?
                ParseQuery.CachePolicy.CACHE_THEN_NETWORK : ParseQuery.CachePolicy.CACHE_ELSE_NETWORK;
    }
}
