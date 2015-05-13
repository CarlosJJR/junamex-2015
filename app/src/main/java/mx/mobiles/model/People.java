package mx.mobiles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class People {

    public static final String TABLE = "peopleMet";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DISTRICT = "district";
    public static final String NOTES = "notes";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String FACEBOOK = "facebook";
    public static final String FACEBOOK_ID = "facebook_id";

    public static final String CREATE_TABLE = "CREATE TABLE peopleMet ("
            +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            +"name varchar(250) NOT NULL,"
            +"district varchar(250),"
            +"notes text,"
            +"phone varchar(250),"
            +"email varchar(250),"
            +"facebook varchar(250),"
            +"facebook_id varchar(250),"
            +"twitter varchar(250))";

    private int id;
    private String name;
    private String notes;
    private String phone;
    private String email;
    private String facebook;
    private String facebookId;
    private String district;
    private int tempAvatar;

    public int getId() {
        return id;
    }

    public People setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public People setName(String name) {
        this.name = name;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public People setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public People setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public People setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFacebook() {
        return facebook;
    }

    public People setFacebook(String facebook) {
        this.facebook = facebook;
        return this;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public People setFacebookId(String facebookId) {
        this.facebookId = facebookId;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public People setDistrict(String district) {
        this.district = district;
        return this;
    }

    public int getTempAvatar() {
        return tempAvatar;
    }

    public People setTempAvatar(int tempAvatar) {
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
        cv.put(FACEBOOK_ID, this.facebookId);

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
        cv.put(FACEBOOK_ID, this.facebookId);

        return database.update(TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    public static ArrayList<People> getAll(SQLiteDatabase database) {

        ArrayList<People> peoples = new ArrayList<>();
        Cursor cursor = database.query(TABLE, null, ID +"!=?", new String[]{"1"}, null, null, People.NAME);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String district = cursor.getString(cursor.getColumnIndex(DISTRICT));
            String facebookId = cursor.getString(cursor.getColumnIndex(FACEBOOK_ID));

            People people = new People();
            people.setId(id)
                    .setName(name)
                    .setDistrict(district)
                    .setFacebookId(facebookId);

            peoples.add(people);
        }
        cursor.close();

        return peoples;
    }

    public static People getPeople(SQLiteDatabase database, int id) {

        People people = null;
        Cursor cursor = database.query(TABLE, null, ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {

            people = new People();

            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String notes = cursor.getString(cursor.getColumnIndex(NOTES));
            String phone = cursor.getString(cursor.getColumnIndex(PHONE));
            String email = cursor.getString(cursor.getColumnIndex(EMAIL));
            String facebook = cursor.getString(cursor.getColumnIndex(FACEBOOK));
            String facebookId = cursor.getString(cursor.getColumnIndex(FACEBOOK_ID));
            String district = cursor.getString(cursor.getColumnIndex(DISTRICT));

            people.setId(id)
                    .setName(name)
                    .setNotes(notes)
                    .setPhone(phone)
                    .setEmail(email)
                    .setFacebook(facebook)
                    .setFacebookId(facebookId)
                    .setDistrict(district);
        }
        cursor.close();

        return people;
    }
}
