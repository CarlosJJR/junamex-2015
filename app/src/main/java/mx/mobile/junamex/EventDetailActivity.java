package mx.mobile.junamex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

import de.hdodenhof.circleimageview.CircleImageView;
import mx.mobile.model.Event;
import mx.mobile.utils.ObservableScrollView;

/**
 * Created by desarrollo16 on 13/01/15.
 */
public class EventDetailActivity extends BaseActivity implements ObservableScrollView.Callbacks {

    private static final float PHOTO_ASPECT_RATIO = 1.3333333f;

    public static final String TRANSITION_NAME_PHOTO = "photo";
    public static final String EVENT_KEY = "event_id";
    public static final String PALETTE_KEY = "palette";

    private TextView mTitle;
    private TextView mSubtitle;
    private View loadingView;

    private ObservableScrollView mScrollView;
    private Event cachedEvent;

    private TextView mAbstract;
    private View mHeaderBox;
    private View mDetailsContainer;

    private boolean toolbarTransparent = true;
    private boolean mHasPhoto;
    private boolean interfaceVisible;

    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private View mPhotoViewContainer;
    private ParseImageView mPhotoView;
    private int paletteColor;

    private LinearLayout mSpeakersBlock;
    private TextView mSpeakersHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        toolbar.setNavigationIcon(shouldBeFloatingWindow
//                ? R.drawable.ic_ab_close : R.drawable.ic_up);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setTitle(null);

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mTitle = (TextView) findViewById(R.id.session_title);
        mSubtitle = (TextView) findViewById(R.id.session_subtitle);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        mPhotoView = (ParseImageView) findViewById(R.id.session_photo);
        mSpeakersBlock = (LinearLayout) findViewById(R.id.session_speakers_block);
        mSpeakersHeader = (TextView) findViewById(R.id.session_speakers_header);
        loadingView = findViewById(android.R.id.progress);

        mAbstract = (TextView) findViewById(R.id.session_abstract);

        String eventId = getIntent().getStringExtra(EVENT_KEY);
        paletteColor = getIntent().getIntExtra(PALETTE_KEY, getResources().getColor(R.color.color_primary));
        getData(eventId);

        mHeaderBox.setBackgroundColor(paletteColor);

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_event_detail;
    }

    private void getData(String eventID) {

        ParseQuery<Event> query = new ParseQuery<> (Event.class);
        query.include(Event.SPEAKER);
        query.include(Event.LOCATION);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground(eventID, new GetCallback<Event>() {
            @Override
            public void done(Event mEvent, ParseException e) {
                if (e == null)
                    updateLayout(mEvent);
                else
                    Toast.makeText(EventDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = toolbar.getHeight();

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
        int topMargin = scrollY + toolbar.getHeight();

        float newTop = Math.max(mPhotoHeightPixels, topMargin);
        ViewCompat.setTranslationY(mHeaderBox, newTop);
        ViewCompat.setTranslationY(toolbar, scrollY);

        // Move background photo (parallax effect)
        ViewCompat.setTranslationY(mPhotoViewContainer, scrollY * 0.5f);

        if (topMargin >= mPhotoHeightPixels) {
            if (toolbarTransparent) {
                toolbar.setBackgroundColor(paletteColor);
                toolbarTransparent = false;
            }
        } else {
            if (!toolbarTransparent) {
                toolbar.setBackgroundResource(R.drawable.dark_gradient_top_to_bottom);
                toolbarTransparent = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.session_detail, menu);
        return true;
    }

    public void showMainLayout() {

        loadingView.setVisibility(View.GONE);
        mPhotoViewContainer.setVisibility(View.VISIBLE);
        mHeaderBox.setVisibility(View.VISIBLE);
        mDetailsContainer.setVisibility(View.VISIBLE);

        interfaceVisible = true;
    }

    public void showMainLayoutAnimated() {

        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(400);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showMainLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mPhotoViewContainer.startAnimation(animation);
        mHeaderBox.startAnimation(animation);
        mDetailsContainer.startAnimation(animation);
    }

    private void updateLayout(Event mEvent) {

        if (cachedEvent != null) {
            if (!mEvent.getUpdatedAt().after(cachedEvent.getUpdatedAt()))
                return;
        }

        String subtitle = getString(R.string.event_detail_subtitle, mEvent.getFormattedTime(EventDetailActivity.this), mEvent.getLocation().getName());

        mTitle.setText(mEvent.getEventName());
        mSubtitle.setText(subtitle);
        mAbstract.setText(mEvent.getEventAbstract());
        ParseFile image = mEvent.getEventPhoto();

        mHasPhoto = mEvent.hasPhoto();

        if (mHasPhoto) {
            mPhotoView.setParseFile(image);
            mPhotoView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Palette.generateAsync(bitmap,
                            new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch vibrant =
                                            palette.getVibrantSwatch();
                                    if (vibrant != null) {
                                        paletteColor = vibrant.getRgb();
                                        mHeaderBox.setBackgroundColor(paletteColor);
                                        mSpeakersHeader.setTextColor(paletteColor);
                                    }
                                }
                            });
                }
            });
        }

        if (mEvent.hasSpeaker()) {
            mSpeakersHeader.setTextColor(paletteColor);

            View speaker = View.inflate(EventDetailActivity.this, R.layout.item_speaker, mSpeakersBlock);
            TextView name = (TextView) speaker.findViewById(R.id.speaker_name);
            TextView bio = (TextView) speaker.findViewById(R.id.speaker_bio);
            final CircleImageView speakerPhoto = (CircleImageView) speaker.findViewById(R.id.speaker_photo);

            name.setText(mEvent.getSpeaker().getName());
            bio.setText(mEvent.getSpeaker().getBiography());

            ParseFile file = mEvent.getSpeaker().getPhoto();
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    speakerPhoto.setImageBitmap(photo);
                }
            });

            mSpeakersBlock.setVisibility(View.VISIBLE);
        }

        cachedEvent = mEvent;

        if (!interfaceVisible)
            showMainLayoutAnimated();
    }
}
