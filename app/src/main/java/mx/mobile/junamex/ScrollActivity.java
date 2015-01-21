package mx.mobile.junamex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import mx.mobile.utils.ObservableScrollView;

/**
 * Created by desarrollo16 on 13/01/15.
 */
public class ScrollActivity extends ActionBarActivity implements ObservableScrollView.Callbacks {

    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

    public static final String TRANSITION_NAME_PHOTO = "photo";

    private View mScrollViewChild;
    private TextView mTitle;
    private TextView mSubtitle;

    private ObservableScrollView mScrollView;

    private TextView mAbstract;
    private View mHeaderBox;
    private View mDetailsContainer;

    private boolean toolbarTransparent = true;
    private boolean mHasPhoto;


    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private View mPhotoViewContainer;
    private ParseImageView mPhotoView;

    private Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_scroll);

        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        mScrollViewChild = findViewById(R.id.scroll_view_child);

        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mTitle = (TextView) findViewById(R.id.session_title);
        mSubtitle = (TextView) findViewById(R.id.session_subtitle);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        mPhotoView = (ParseImageView) findViewById(R.id.session_photo);

        mAbstract = (TextView) findViewById(R.id.session_abstract);

        String eventId = getIntent().getStringExtra("event_id");
        getData(eventId);

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);
    }

    private void getData(String eventId) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {

                    String name = parseObject.getString("name");
                    String description = parseObject.getString("description");
                    mTitle.setText(name);
                    mAbstract.setText(description);
                    ParseFile image = parseObject.getParseFile("eventPhoto");

                    mHasPhoto = image != null;

                    if (mHasPhoto) {
                        mPhotoView.setParseFile(image);
                        mPhotoView.loadInBackground();
                    }

                } else {
                    //Error
                    Toast.makeText(ScrollActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;

        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScrollView == null) {
            return;
        }

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            recomputePhotoAndScrollingMetrics();
        }
    };

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();
        int topMargin = scrollY + mActionBarToolbar.getHeight();

        float newTop = Math.max(mPhotoHeightPixels, topMargin);
        mHeaderBox.setTranslationY(newTop);
        mActionBarToolbar.setTranslationY(scrollY);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);

        if (topMargin >= mPhotoHeightPixels) {
            if (toolbarTransparent) {
                mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                toolbarTransparent = false;
            }
        } else {
            if (!toolbarTransparent) {
                mActionBarToolbar.setBackgroundColor(Color.TRANSPARENT);
                toolbarTransparent = true;
            }
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }
}
