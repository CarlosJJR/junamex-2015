package mx.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class DatabaseAdapter {

    private static DatabaseHelper mDbHelper = null;
    private static SQLiteDatabase mDb = null;

    public static SQLiteDatabase getDB(Context _context) {
        if (mDb == null)
            openDB(_context);
        return mDb;
    }

    public static boolean openDB(Context _Context) {
        if (mDbHelper != null)
            mDbHelper.close();

        mDbHelper = new DatabaseHelper(_Context);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
