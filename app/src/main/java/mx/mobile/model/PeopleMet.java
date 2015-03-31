package mx.mobile.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Currency;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleMet {

    public static final String TABLE = "peopleMet";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DISTRICT = "district";
    public static final String NOTES = "notes";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";

    public static final String CREATE_TABLE = "CREATE TABLE peopleMet ("
            +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            +"name varchar(250) NOT NULL,"
            +"district varchar(250),"
            +"notes text,"
            +"phone varchar(250),"
            +"email varchar(250),"
            +"facebook varchar(250),"
            +"twitter varchar(250))";

    private int id;
    private String name;
    private String notes;
    private String phone;
    private String email;
    private String facebook;
    private String twitter;
    private String district;
    private int tempAvatar;

    public int getId() {
        return id;
    }

    public PeopleMet setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PeopleMet setName(String name) {
        this.name = name;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public PeopleMet setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public PeopleMet setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PeopleMet setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFacebook() {
        return facebook;
    }

    public PeopleMet setFacebook(String facebook) {
        this.facebook = facebook;
        return this;
    }

    public String getTwitter() {
        return twitter;
    }

    public PeopleMet setTwitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public PeopleMet setDistrict(String district) {
        this.district = district;
        return this;
    }

    public int getTempAvatar() {
        return tempAvatar;
    }

    public PeopleMet setTempAvatar(int tempAvatar) {
        this.tempAvatar = tempAvatar;
        return this;
    }

    public int save(SQLiteDatabase database) {

        ContentValues cv = new ContentValues();

        cv.put(NAME, this.name);
        cv.put(DISTRICT, this.district);
        cv.put(NOTES, this.notes);
        cv.put(PHONE, this.phone);
        cv.put(EMAIL, this.email);
        cv.put(FACEBOOK, this.facebook);

        return (int) database.insert(TABLE, null, cv);
    }

    public int update(SQLiteDatabase database, int id) {

        ContentValues cv = new ContentValues();

        cv.put(NAME, this.name);
        cv.put(DISTRICT, this.district);
        cv.put(NOTES, this.notes);
        cv.put(PHONE, this.phone);
        cv.put(EMAIL, this.email);
        cv.put(FACEBOOK, this.facebook);

        return database.update(TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    public static ArrayList<PeopleMet> getAll(SQLiteDatabase database) {

        ArrayList<PeopleMet> peopleMets = new ArrayList<>();
        Cursor cursor = database.query(TABLE, null, ID +"!=?", new String[]{"1"}, null, null, PeopleMet.NAME);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String notes = cursor.getString(cursor.getColumnIndex(NOTES));
            String facebook = cursor.getString(cursor.getColumnIndex(FACEBOOK));

            PeopleMet people = new PeopleMet();
            people.setId(id)
                    .setName(name)
                    .setNotes(notes)
                    .setFacebook(facebook);

            peopleMets.add(people);
        }
        cursor.close();

        return peopleMets;
    }

    public static PeopleMet getPeople(SQLiteDatabase database, int id) {

        PeopleMet peopleMet = null;
        Cursor cursor = database.query(TABLE, null, ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {

            peopleMet = new PeopleMet();

            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String notes = cursor.getString(cursor.getColumnIndex(NOTES));
            String phone = cursor.getString(cursor.getColumnIndex(PHONE));
            String email = cursor.getString(cursor.getColumnIndex(EMAIL));
            String facebook = cursor.getString(cursor.getColumnIndex(FACEBOOK));
            String district = cursor.getString(cursor.getColumnIndex(DISTRICT));

            peopleMet.setId(id)
                    .setName(name)
                    .setNotes(notes)
                    .setPhone(phone)
                    .setEmail(email)
                    .setFacebook(facebook)
                    .setDistrict(district);
        }
        cursor.close();

        return peopleMet;
    }
}
