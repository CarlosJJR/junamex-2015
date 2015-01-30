package mx.mobile.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleMet {

    public static final String TABLE = "peopleMet";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NOTES = "notes";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String PLUS = "plus";
    public static final String PHOTO = "photo";

    public static final String CREATE_TABLE = "CREATE TABLE peopleMet ("
            +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
            +"name varchar(250) NOT NULL,"
            +"notes text,"
            +"phone varchar(250),"
            +"email varchar(250),"
            +"photo text,"
            +"facebook varchar(250),"
            +"twitter varchar(250),"
            +"plus varchar(250))";

    private int id;
    private String name;
    private String notes;
    private String phone;
    private String email;
    private Bitmap photo;
    private String encodedPhoto;
    private String facebook;
    private String twitter;
    private String gPlus;

    public int getId() {
        return id;
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

    public Bitmap getPhoto() {
        return photo;
    }

    public PeopleMet setPhoto(Bitmap photo) {
        this.photo = photo;
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

    public String getgPlus() {
        return gPlus;
    }

    public PeopleMet setgPlus(String gPlus) {
        this.gPlus = gPlus;
        return this;
    }

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }

    public int save(SQLiteDatabase database) {

        ContentValues cv = new ContentValues();

        cv.put(NAME, this.name);
        cv.put(NOTES, this.notes);
        cv.put(PHONE, this.phone);
        cv.put(EMAIL, this.email);
        cv.put(FACEBOOK, this.facebook);
        cv.put(TWITTER, this.twitter);
        cv.put(PLUS, this.gPlus);
        cv.put(PHOTO, this.encodedPhoto);

        return (int) database.insert(TABLE, null, cv);
    }
}
