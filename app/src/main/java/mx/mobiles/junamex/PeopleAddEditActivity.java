package mx.mobiles.junamex;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import mx.mobiles.model.PeopleMet;

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

        nameField = (EditText) findViewById(R.id.add_name);
        districtField = (AutoCompleteTextView) findViewById(R.id.add_district);
        phoneField = (EditText) findViewById(R.id.add_phone);
        emailField = (EditText) findViewById(R.id.add_email);
        notesField = (EditText) findViewById(R.id.add_notes);

        String[] districts = getResources().getStringArray(R.array.districts);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_dropdown, districts);
        districtField.setAdapter(adapter);

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.save_button);
        actionButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String json = extras.getString(CONTACT_KEY);
            isEditing = extras.getBoolean(EDIT_KEY, false);

            try {
                JSONObject object = new JSONObject(json);

                String name = object.getString(PeopleMet.NAME);
                String district = object.getString(PeopleMet.DISTRICT);
                String phone = object.getString(PeopleMet.PHONE);
                String email = object.getString(PeopleMet.EMAIL);


                if (name.equals(getString(R.string.default_name)))
                    nameField.setHint(name);
                else
                    nameField.setText(name);

                districtField.setText(district);
                phoneField.setText(phone);
                emailField.setText(email);

                if (isEditing) {

                    int peopleId = object.getInt(PeopleMet.ID);
                    String notes = object.getString(PeopleMet.NOTES);

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

        PeopleMet peopleMet = new PeopleMet();
        peopleMet.setName(name)
                .setEmail(email)
                .setPhone(phone)
                .setDistrict(district)
                .setNotes(notes);

        if (isEditing)
            changesSaved = peopleMet.update(getDB(), (int) nameField.getTag()) == 1;
        else
            changesSaved = peopleMet.save(getDB()) != -1;

        if (changesSaved)
            setResult(Activity.RESULT_OK);
        else
            setResult(Activity.RESULT_CANCELED);
    }
}
