package mx.mobile.junamex;

import android.app.AlertDialog;
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

    TextView name, email, phone, facebook, church, district, notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle(null);

        int id = getIntent().getIntExtra(PeopleMet.TABLE, 1);

        name = (TextView) findViewById(R.id.people_name);
        phone = (TextView) findViewById(R.id.people_phone);
        email = (TextView) findViewById(R.id.people_email);
        facebook = (TextView) findViewById(R.id.people_facebook);
        church = (TextView) findViewById(R.id.people_church);
        district = (TextView) findViewById(R.id.people_district);
        notes = (TextView) findViewById(R.id.people_notes);

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
                    phone.setText(peopleMet.getPhone());
                    phone.setVisibility(View.VISIBLE);
                }

                if (peopleMet.getEmail() != null) {
                    email.setText(peopleMet.getEmail());
                    email.setVisibility(View.VISIBLE);
                }

                if (peopleMet.getFacebook() != null) {
                    facebook.setText(peopleMet.getFacebook());
                    facebook.setVisibility(View.VISIBLE);
                }

                if (peopleMet.getChurch() != null) {
                    church.setText(peopleMet.getChurch());
                    church.setVisibility(View.VISIBLE);
                }

                if (peopleMet.getDistrict() != null) {
                    district.setText(peopleMet.getDistrict());
                    district.setVisibility(View.VISIBLE);
                }

                if (peopleMet.getNotes() != null) {
                    notes.setText(peopleMet.getNotes());
                    notes.setVisibility(View.VISIBLE);
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

        if (item.getItemId() == R.id.share_qr) {

            String json = peopleToJSON();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private String peopleToJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PeopleMet.NAME, name.getText());
            jsonObject.put(PeopleMet.CHURCH, church.getText());
            jsonObject.put(PeopleMet.DISTRICT, district.getText());
            jsonObject.put(PeopleMet.EMAIL, email.getText());
            jsonObject.put(PeopleMet.PHONE, phone.getText());
            jsonObject.put(PeopleMet.FACEBOOK, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
