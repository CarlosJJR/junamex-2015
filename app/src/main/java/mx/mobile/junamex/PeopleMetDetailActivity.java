package mx.mobile.junamex;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import mx.mobile.model.PeopleMet;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 03/02/15.
 */
public class PeopleMetDetailActivity extends BaseActivity {

    private static final int REQUEST_CODE = 1111;
    private TextView name, emailText, facebookText, phoneText, districtText, notesText;
    private View email, phone, facebook, district, notes;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle(null);

        id = getIntent().getIntExtra(PeopleMet.TABLE, 1);

        name = (TextView) findViewById(R.id.people_name);
        phone = findViewById(R.id.people_phone);
        email =findViewById(R.id.people_email);
        facebook = findViewById(R.id.people_facebook);
        district = findViewById(R.id.people_district);
        notes = findViewById(R.id.people_notes);

        emailText = (TextView) findViewById(R.id.people_email_text);
        phoneText = (TextView) findViewById(R.id.people_phone_text);
        facebookText = (TextView) findViewById(R.id.people_facebook_text);
        districtText = (TextView) findViewById(R.id.people_district_text);
        notesText = (TextView) findViewById(R.id.people_notes_text);

        new LoadDetail().execute(id);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_people_detail;
    }

    private class LoadDetail extends AsyncTask<Integer, Void, PeopleMet> {
        @Override
        protected PeopleMet doInBackground(Integer... params) {
            return PeopleMet.getPeople(getDB(), params[0]);
        }

        @Override
        protected void onPostExecute(PeopleMet peopleMet) {
            super.onPostExecute(peopleMet);

            if (peopleMet != null) {

                if (peopleMet.getName() != null)
                    name.setText(peopleMet.getName());

                if (peopleMet.getPhone() != null) {
                    if (!peopleMet.getPhone().isEmpty()) {
                        phoneText.setText(peopleMet.getPhone());
                        phone.setVisibility(View.VISIBLE);
                    }
                }

                if (peopleMet.getEmail() != null) {
                    if (!peopleMet.getEmail().isEmpty()) {
                        emailText.setText(peopleMet.getEmail());
                        email.setVisibility(View.VISIBLE);
                    }
                }

                if (peopleMet.getFacebook() != null) {
                    if (!peopleMet.getFacebook().isEmpty()) {
                        facebookText.setText(peopleMet.getFacebook());
                        facebook.setVisibility(View.VISIBLE);
                    }
                }

                if (peopleMet.getDistrict() != null) {
                    if (!peopleMet.getDistrict().isEmpty()) {
                        districtText.setText(peopleMet.getDistrict());
                        district.setVisibility(View.VISIBLE);
                    }
                }

                if (peopleMet.getNotes() != null) {
                    if (!peopleMet.getNotes().isEmpty()) {
                        notesText.setText(peopleMet.getNotes());
                        notes.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.people_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.share_qr:
                String json = peopleToJSON(false);
                Bitmap qrCode = null;
                try {
                    qrCode = Utilities.encodeAsBitmap(json, BarcodeFormat.QR_CODE, 512, 512);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if (qrCode != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(PeopleMetDetailActivity.this);

                    ImageView code = new ImageView(PeopleMetDetailActivity.this);
                    code.setImageBitmap(qrCode);
                    builder.setView(code);
                    builder.show();

                } else {
                    Toast.makeText(PeopleMetDetailActivity.this, getString(R.string.common_google_play_services_network_error_text), Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.edit:
                Intent intent = new Intent(this, PeopleAddEditActivity.class);
                intent.putExtra(PeopleAddEditActivity.CONTACT_KEY, peopleToJSON(true));
                intent.putExtra(PeopleAddEditActivity.EDIT_KEY, true);
                startActivityForResult(intent, REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String peopleToJSON(boolean includeNotes) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PeopleMet.NAME, name.getText());
            jsonObject.put(PeopleMet.DISTRICT, districtText.getText());
            jsonObject.put(PeopleMet.EMAIL, emailText.getText());
            jsonObject.put(PeopleMet.PHONE, phoneText.getText());
            jsonObject.put(PeopleMet.FACEBOOK, "");

            if (includeNotes) {
                jsonObject.put(PeopleMet.ID, id);
                jsonObject.put(PeopleMet.NOTES, notesText.getText());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            new LoadDetail().execute(id);
        }
    }
}
