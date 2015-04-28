package mx.mobiles.junamex;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

import mx.mobiles.model.MuseumItem;

/**
 * Created by desarrollo16 on 29/01/15.
 */
public class MuseumDetailActivity extends BaseActivity {

    public static final String TRANSITION = "MuseumDetailActivity:imageView";

    TextView title, description;
    ParseImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        toolbar.setNavigationIcon(shouldBeFloatingWindow
//                ? R.drawable.ic_ab_close : R.drawable.ic_up);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setBackgroundResource(R.drawable.dark_gradient_top_to_bottom);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle(null);

        title = (TextView) findViewById(R.id.item_name);
        description = (TextView) findViewById(R.id.item_description);
        imageView = (ParseImageView) findViewById(R.id.image_holder);

        ViewCompat.setTransitionName(imageView, TRANSITION);

        String itemId = getIntent().getStringExtra("item_id");
        getData(itemId);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_museum_detail;
    }

    private void getData(String itemId) {

        ParseQuery<MuseumItem> query = new ParseQuery<>(MuseumItem.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground(itemId, new GetCallback<MuseumItem>() {
            @Override
            public void done(MuseumItem museumItem, ParseException e) {

                if (e == null)
                    updateUI(museumItem);
                else
                    Log.d("Parse", e.getLocalizedMessage());
            }
        });
    }

    private void updateUI(MuseumItem museumItem) {

        title.setText(museumItem.getName());
        description.setText(museumItem.getDescription());

        imageView.setParseFile(museumItem.getImage());
        imageView.loadInBackground();
    }
}
