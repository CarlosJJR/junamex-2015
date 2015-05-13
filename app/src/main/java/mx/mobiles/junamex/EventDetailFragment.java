package mx.mobiles.junamex;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

import de.hdodenhof.circleimageview.CircleImageView;
import mx.mobiles.model.Event;
import mx.mobiles.utils.ObservableScrollView;
import mx.mobiles.utils.Utilities;

/**
 * Created by desarrollo16 on 17/02/15.
 */
public class EventDetailFragment extends DialogFragment implements ObservableScrollView.Callbacks{

    private static final float PHOTO_ASPECT_RATIO = 1.3333333f;

    public static final String TRANSITION_NAME_PHOTO = "photo";

    private TextView mTitle;
    private TextView mSubtitle;
    private View loadingView;
    private Toolbar toolbar;

    private ObservableScrollView mScrollView;
    private Event cachedEvent;
    private OnFragmentDismissedListener dismissedListener;

    private TextView mAbstract;
    private View mHeaderBox;
    private View mDetailsContainer;

    private boolean toolbarTransparent = true;
    private boolean mHasPhoto;
    private boolean interfaceVisible;
    private boolean isDialog;

    private int mPhotoHeightPixels;
    private View mPhotoViewContainer;
    private ParseImageView mPhotoView;
    private int paletteColor;

    private LinearLayout mSpeakersBlock;
    private TextView mSpeakersHeader;

    public static EventDetailFragment newInstance(Bundle args) {

        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isDialog = true;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        setUpToolbar((Toolbar) view.findViewById(R.id.toolbar));

        if (Utilities.isVersionOrOlder(Build.VERSION_CODES.HONEYCOMB)) {
            mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll_view);
            mScrollView.addCallbacks(this);
            ViewTreeObserver vto = mScrollView.getViewTreeObserver();
            if (vto.isAlive()) {
                vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
            }
        }

        mDetailsContainer = view.findViewById(R.id.details_container);
        mHeaderBox = view.findViewById(R.id.header_session);
        mTitle = (TextView) view.findViewById(R.id.session_title);
        mSubtitle = (TextView) view.findViewById(R.id.session_subtitle);
        mPhotoViewContainer = view.findViewById(R.id.session_photo_container);
        mPhotoView = (ParseImageView) view.findViewById(R.id.session_photo);
        mSpeakersBlock = (LinearLayout) view.findViewById(R.id.session_speakers_block);
        mSpeakersHeader = (TextView) view.findViewById(R.id.session_speakers_header);
        loadingView = view.findViewById(android.R.id.progress);

        mAbstract = (TextView) view.findViewById(R.id.session_abstract);

        String eventId = getArguments().getString(Event.ID);
        paletteColor = getArguments().getInt(Event.PALETTE_COLOR, getResources().getColor(R.color.color_primary));
        getData(eventId);

        if (Utilities.isLollipop())
            getActivity()
                    .getWindow()
                    .setStatusBarColor(Utilities.getSecondaryColor(paletteColor));

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);
        return view;
    }

    private void getData(String eventID) {

        ParseQuery<Event> query = new ParseQuery<> (Event.class);
        query.include(Event.SPEAKER);
        query.include(Event.LOCATION);
        query.setCachePolicy(((BaseActivity) getActivity()).getCachePolicy());
        query.getInBackground(eventID, new GetCallback<Event>() {
            @Override
            public void done(Event mEvent, ParseException e) {
                if (e == null)
                    updateLayout(mEvent);
                else
                    Log.d("Parse", e.getLocalizedMessage());
            }
        });
    }

    private void recomputePhotoAndScrollingMetrics() {
        int mHeaderHeightPixels = mHeaderBox.getHeight();

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
    public void onDestroy() {
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

    public void setUpToolbar(Toolbar toolbar) {

        if (toolbar != null) {

            this.toolbar = toolbar;

            toolbar.setNavigationIcon(isDialog
                ? R.drawable.ic_clear : R.drawable.ic_back);
            toolbar.inflateMenu(R.menu.session_detail);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dismissedListener != null)
                        dismissedListener.onFragmentDismissed();
                    else
                        dismiss();
                }
            });

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.show_map:
                            showMap();
                            return true;

                        default: return false;
                    }
                }
            });
        }
    }

    private void showMap() {

        Intent intent = new Intent(getActivity(), MapActivity.class);
        intent.putExtra(MapFragment.MARKER_KEY, cachedEvent.getLocation().getObjectId());
        startActivity(intent);
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

        if (!isAdded())
            return;

        if (cachedEvent != null) {
            if (!mEvent.getUpdatedAt().after(cachedEvent.getUpdatedAt()))
                return;
        }

        String subtitle = getString(R.string.event_detail_subtitle, mEvent.getFormattedTime(getActivity()), mEvent.getLocation().getName());

        mHeaderBox.setBackgroundColor(paletteColor);
        mTitle.setText(mEvent.getEventName());
        mSubtitle.setText(subtitle);
        mAbstract.setText(mEvent.getEventAbstract());
        ParseFile image = mEvent.getEventPhoto();

        if (!Utilities.isVersionOrOlder(Build.VERSION_CODES.HONEYCOMB))
            toolbar.setBackgroundColor(paletteColor);

        mHasPhoto = mEvent.hasPhoto();

        if (mHasPhoto) {
            mPhotoView.setParseFile(image);
            mPhotoView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {

                    if (!isAdded())
                        return;

                    if (bytes != null && paletteColor == getResources().getColor(R.color.color_primary)) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        Palette.from(bitmap).generate(
                                new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch vibrant =
                                                palette.getVibrantSwatch();
                                        if (vibrant != null) {
                                            paletteColor = vibrant.getRgb();
                                            mHeaderBox.setBackgroundColor(paletteColor);
                                            mSpeakersHeader.setTextColor(paletteColor);

                                            if (Utilities.isLollipop())
                                                getActivity()
                                                        .getWindow()
                                                        .setStatusBarColor(Utilities.getSecondaryColor(paletteColor));

                                        }
                                    }
                                });
                    }
                }
            });
        }

        if (mEvent.hasSpeaker()) {
            mSpeakersHeader.setTextColor(paletteColor);

            View speaker = View.inflate(getActivity(), R.layout.item_speaker, mSpeakersBlock);
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

    public interface OnFragmentDismissedListener {
        void onFragmentDismissed();
    }

    public void setOnFragmentDismissedListener(OnFragmentDismissedListener dismissedListener) {
        this.dismissedListener = dismissedListener;
    }
}
