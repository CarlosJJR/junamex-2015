package mx.mobiles.junamex;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;

/**
 * Created by desarrollo16 on 06/03/15.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener{

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

        findViewById(R.id.show_website).setOnClickListener(this);
        findViewById(R.id.show_facebook).setOnClickListener(this);
        findViewById(R.id.share_app).setOnClickListener(this);
        findViewById(R.id.rate_app).setOnClickListener(this);
        findViewById(R.id.show_tutorial).setOnClickListener(this);

        TextView versionName = (TextView) findViewById(R.id.version_name);
        versionName.setText(getAppVersion());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about;
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {

            case R.id.show_website:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.junamex_website)));
                break;

            case R.id.show_facebook:
                intent = getOpenFacebookIntent();
                break;

            case R.id.share_app:
                intent = new Intent(Intent.ACTION_SEND);

                String text = getString(R.string.share_text) + " " + getString(R.string.junamex_website);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                break;

            case R.id.rate_app:
                intent = getOpenPlayStoreIntent();
                break;

            case R.id.show_tutorial:
                intent = new Intent(this, TutorialActivity.class);
                intent.putExtra(TutorialActivity.TELEPHONY_SERVICE, true);
                break;
        }

        if (intent != null)
            startActivity(intent);

    }

    private String getAppVersion() {

        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return getString(R.string.version) + versionName;
    }

    private Intent getOpenFacebookIntent() {

        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/pages/Comeback/322672031260012?fref=tsComeback" ));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/Comeback/322672031260012?fref=tsComeback"));
        }
    }

    private Intent getOpenPlayStoreIntent() {

//        String appPackageName = context.getPackageName();
        String appPackageName = "pl.idreams.skyforcehd";
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }
    }
}
