package mx.mobiles.junamex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import mx.mobiles.model.People;
import mx.mobiles.ui.CircleProfilePicture;
import mx.mobiles.utils.Utilities;

/**
 * Created by desarrollo16 on 05/03/15.
 */
public class PeopleDetailFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener{

    private static final int REQUEST_CODE = 1111;
    private TextView name, emailText, facebookText, phoneText, districtText, notesText;
    private View email, phone, facebook, district, notes;
    private CircleProfilePicture profilePicture;
    private int id;
    private People contact;
    private Toolbar toolbar;

    public static PeopleDetailFragment newInstance(int peopleId) {

        Bundle args = new Bundle();
        args.putInt(People.TABLE, peopleId);

        PeopleDetailFragment fragment = new PeopleDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt(People.TABLE, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_detail, container, false);

        name = (TextView) view.findViewById(R.id.people_name);
        phone = view.findViewById(R.id.people_phone);
        email = view.findViewById(R.id.people_email);
        facebook = view.findViewById(R.id.people_facebook);
        district = view.findViewById(R.id.people_district);
        notes = view.findViewById(R.id.people_notes);

        profilePicture = (CircleProfilePicture) view.findViewById(R.id.profile_picture);
        emailText = (TextView) view.findViewById(R.id.people_email_text);
        phoneText = (TextView) view.findViewById(R.id.people_phone_text);
        facebookText = (TextView) view.findViewById(R.id.people_facebook_text);
        districtText = (TextView) view.findViewById(R.id.people_district_text);
        notesText = (TextView) view.findViewById(R.id.people_notes_text);

        facebookText.setOnClickListener(this);

        setUpToolbar((Toolbar) view.findViewById(R.id.toolbar));
        new LoadDetail().execute(id);

        return view;
    }

    public void setUpToolbar(Toolbar toolbar) {

        if (toolbar != null) {

            this.toolbar = toolbar;

            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.inflateMenu(R.menu.people_detail);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            toolbar.setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.share_qr:
                String json = peopleToJSON(false);
                Bitmap qrCode = null;
                Log.d("Share contact", json);
                try {
                    qrCode = Utilities.encodeAsBitmap(json, BarcodeFormat.QR_CODE, 512, 512);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if (qrCode != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    ImageView code = new ImageView(getActivity());
                    code.setImageBitmap(qrCode);
                    builder.setView(code);
                    builder.show();

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.edit:
                Intent intent = new Intent(getActivity(), PeopleAddEditActivity.class);
                intent.putExtra(PeopleAddEditActivity.CONTACT_KEY, peopleToJSON(true));
                intent.putExtra(PeopleAddEditActivity.EDIT_KEY, true);
                startActivityForResult(intent, REQUEST_CODE);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.people_facebook_text:
                Intent intent;
                try {
                    getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getFacebook()));
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact.getFacebook()));
                }
                getActivity().startActivity(intent);
                break;
        }
    }

    private class LoadDetail extends AsyncTask<Integer, Void, People> {
        @Override
        protected People doInBackground(Integer... params) {
            contact = People.getPeople(((BaseActivity) getActivity()).getDB(), params[0]);
            return contact;
        }

        @Override
        protected void onPostExecute(People people) {
            super.onPostExecute(people);

            if (people != null) {

                if (people.getFacebookId() != null)
                    profilePicture.setProfileId(people.getFacebookId());

                if (people.getName() != null)
                    name.setText(people.getName());

                if (people.getPhone() != null) {
                    if (!people.getPhone().isEmpty()) {
                        phoneText.setText(people.getPhone());
                        phone.setVisibility(View.VISIBLE);
                    }
                }

                if (people.getEmail() != null) {
                    if (!people.getEmail().isEmpty()) {
                        emailText.setText(people.getEmail());
                        email.setVisibility(View.VISIBLE);
                    }
                }

                if (people.getFacebook() != null) {
                    if (!people.getFacebook().isEmpty()) {
                        facebookText.setText(R.string.see_facebook_timeline);
                        facebook.setVisibility(View.VISIBLE);
                    }
                }

                if (people.getDistrict() != null) {
                    if (!people.getDistrict().isEmpty()) {
                        districtText.setText(people.getDistrict());
                        district.setVisibility(View.VISIBLE);
                    }
                }

                if (people.getNotes() != null) {
                    if (!people.getNotes().isEmpty()) {
                        notesText.setText(people.getNotes());
                        notes.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private String peopleToJSON(boolean includeNotes) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(People.NAME, name.getText());
            jsonObject.put(People.DISTRICT, districtText.getText());
            jsonObject.put(People.EMAIL, emailText.getText());
            jsonObject.put(People.PHONE, phoneText.getText());
            jsonObject.put(People.FACEBOOK, contact.getFacebookId());

            if (includeNotes) {
                jsonObject.put(People.ID, id);
                jsonObject.put(People.NOTES, notesText.getText());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity().setResult(resultCode);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            new LoadDetail().execute(id);
        }
    }
}
