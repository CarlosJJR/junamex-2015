package mx.mobiles.junamex;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import mx.mobiles.model.People;

/**
 * Created by desarrollo16 on 04/02/15.
 */
public class PeopleAddEditActivity extends BaseActivity implements View.OnClickListener{

    public static final String CONTACT_KEY = "contact";
    public static final String EDIT_KEY = "edit";

    private EditText nameField, phoneField, emailField, notesField;
    private AutoCompleteTextView districtField;
    boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.edit_contact);
        toolbar.setDisplayHomeAsUpEnabled(true);

        nameField = (EditText) findViewById(R.id.add_name);
        districtField = (AutoCompleteTextView) findViewById(R.id.add_district);
        phoneField = (EditText) findViewById(R.id.add_phone);
        emailField = (EditText) findViewById(R.id.add_email);
        notesField = (EditText) findViewById(R.id.add_notes);

        String[] districts = getResources().getStringArray(R.array.districts);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_dropdown, districts);
        districtField.setAdapter(adapter);

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.save_button);
        actionButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String json = extras.getString(CONTACT_KEY);
            isEditing = extras.getBoolean(EDIT_KEY, false);

            try {
                JSONObject object = new JSONObject(json);

                String name = object.getString(People.NAME);
                String district = object.getString(People.DISTRICT);
                String phone = object.getString(People.PHONE);
                String email = object.getString(People.EMAIL);
                String facebook = object.getString(People.FACEBOOK);
                String facebookId = object.getString(People.FACEBOOK_ID);

                if (name.equals(getString(R.string.default_name)))
                    nameField.setHint(name);
                else
                    nameField.setText(name);

                districtField.setText(district);
                phoneField.setText(phone);
                emailField.setText(email);
                emailField.setTag(facebook);
                phoneField.setTag(facebookId);

                if (isEditing) {

                    int peopleId = object.getInt(People.ID);
                    String notes = object.getString(People.NOTES);

                    notesField.setText(notes);
                    nameField.setTag(peopleId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_people_add_edit;
    }

    @Override
    public void onClick(View v) {

        saveChanges();
        this.finish();
    }

    private void saveChanges() {

        boolean changesSaved;

        String name = nameField.getText().toString();
        String district = districtField.getText().toString();
        String email = emailField.getText().toString();
        String phone = phoneField.getText().toString();
        String notes = notesField.getText().toString();
        String facebook = emailField.getTag().toString();
        String facebookId = phoneField.getTag().toString();

        People people = new People();
        people.setName(name)
                .setEmail(email)
                .setPhone(phone)
                .setDistrict(district)
                .setFacebookId(facebookId)
                .setFacebook(facebook)
                .setNotes(notes);

        if (isEditing)
            changesSaved = people.update(getDB(), (int) nameField.getTag()) == 1;
        else
            changesSaved = people.save(getDB()) != -1;

        if (changesSaved)
            setResult(Activity.RESULT_OK);
        else
            setResult(Activity.RESULT_CANCELED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }
}
