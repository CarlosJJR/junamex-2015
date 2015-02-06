package mx.mobile.junamex;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import mx.mobile.model.PeopleMet;

/**
 * Created by desarrollo16 on 04/02/15.
 */
public class PeopleAddEditActivity extends BaseActivity implements View.OnClickListener{

    private EditText nameField, churchField, phoneField, emailField, notesField;
    private AutoCompleteTextView districtField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nameField = (EditText) findViewById(R.id.add_name);
        churchField = (EditText) findViewById(R.id.add_church);
        districtField = (AutoCompleteTextView) findViewById(R.id.add_district);
        phoneField = (EditText) findViewById(R.id.add_phone);
        emailField = (EditText) findViewById(R.id.add_email);
        notesField = (EditText) findViewById(R.id.add_notes);

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.save_button);
        actionButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String json = extras.getString("new_contact");

            try {
                JSONObject object = new JSONObject(json);

                String name = object.getString(PeopleMet.NAME);
                String church = object.getString(PeopleMet.CHURCH);
                String district = object.getString(PeopleMet.DISTRICT);
                String phone = object.getString(PeopleMet.PHONE);
                String email = object.getString(PeopleMet.EMAIL);

                nameField.setText(name);
                churchField.setText(church);
                districtField.setText(district);
                phoneField.setText(phone);
                emailField.setText(email);

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
        setResult(Activity.RESULT_OK);
        this.finish();
    }

    private void saveChanges() {

        String name = nameField.getText().toString();
        String church = churchField.getText().toString();
        String district = districtField.getText().toString();
        String email = emailField.getText().toString();
        String phone = phoneField.getText().toString();
        String notes = notesField.getText().toString();

        PeopleMet peopleMet = new PeopleMet();
        peopleMet.setName(name)
                .setEmail(email)
                .setPhone(phone)
                .setChurch(church)
                .setDistrict(district)
                .setNotes(notes);

        peopleMet.save(getDB());
    }
}
