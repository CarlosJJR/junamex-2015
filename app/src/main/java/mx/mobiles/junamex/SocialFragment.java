package mx.mobiles.junamex;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import mx.mobiles.adapters.NavigationDrawerAdapter;
import mx.mobiles.adapters.PeopleAdapter;
import mx.mobiles.adapters.PostersAdapter;
import mx.mobiles.model.FacebookPosts;
import mx.mobiles.model.People;
import mx.mobiles.utils.SimpleDividerDecorator;

/**
 * Created by carlosjimenez on 14/05/15.
 */
public class SocialFragment extends BaseFragment implements View.OnClickListener, FacebookCallback<Sharer.Result> {

    public static final String TAG = SocialFragment.class.getSimpleName();
    public static final int REQUEST_CODE_CAMERA = 3838;
    public static final int REQUEST_CODE_GALLERY = 8383;

    private FloatingActionButton showMenu;
    private CallbackManager callbackManager;
    private LinearLayout panel;

    private ArrayList<FacebookPosts> postsArrayList;
    private PostersAdapter adapter;

    private int fabCenterX;

    private enum AnimationDirection{
        NORMAL,
        REVERSE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postsArrayList = new ArrayList<>();
        adapter = new PostersAdapter(postsArrayList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        panel = (LinearLayout) view.findViewById(R.id.options_menu);

        showMenu = (FloatingActionButton) view.findViewById(R.id.show_menu_button);
        ImageButton shareStatusButton = (ImageButton) view.findViewById(R.id.publish_status);
        ImageButton sharePhotoMakeButton = (ImageButton) view.findViewById(R.id.publish_photo_make);
        ImageButton sharePhotoGalleryButton = (ImageButton) view.findViewById(R.id.publish_photo_gallery);
        ImageButton closeMenu = (ImageButton) view.findViewById(R.id.close_menu);

        showMenu.setOnClickListener(this);
        closeMenu.setOnClickListener(this);
        shareStatusButton.setOnClickListener(this);
        sharePhotoMakeButton.setOnClickListener(this);
        sharePhotoGalleryButton.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.posters_leader_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerDecorator(getActivity()));
        recyclerView.setAdapter(adapter);

        ViewTreeObserver observer = showMenu.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //in here, place the code that requires you to know the dimensions.

                fabCenterX = (showMenu.getLeft() + showMenu.getRight()) / 2;
                //this will be called as the layout is finished, prior to displaying.
            }
        });

        getLeaders();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public int getDrawerPosition() {
        return NavigationDrawerAdapter.SOCIAL_FEED;
    }

    private void getLeaders() {

        ParseQuery<FacebookPosts> query = new ParseQuery<>(FacebookPosts.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.orderByDescending(FacebookPosts.COUNTER);
        query.setLimit(10);
        query.findInBackground(new FindCallback<FacebookPosts>() {
            @Override
            public void done(List<FacebookPosts> list, ParseException e) {
                if (e == null) {
                    postsArrayList.clear();
                    postsArrayList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void transformButtonInToolbar() {

        AnimatorSet animatorSet = getButtonAnimator(AnimationDirection.NORMAL);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showMenu.setVisibility(View.INVISIBLE);
                showOptionsToolbar();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showOptionsToolbar() {

        getMenuAnimator(AnimationDirection.NORMAL).start();
        panel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.close_menu:
                SupportAnimator revealMenu = getMenuAnimator(AnimationDirection.REVERSE);
                revealMenu.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd() {

                        getButtonAnimatorReverse(AnimationDirection.REVERSE).start();
                        panel.setVisibility(View.INVISIBLE);
                        showMenu.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                revealMenu.start();
                break;

            case R.id.show_menu_button:
                transformButtonInToolbar();
                break;

            case R.id.publish_status:
                ShareContent shareStatus = new ShareLinkContent.Builder().build();
                showShareDialog(shareStatus);
                break;

            case R.id.publish_photo_make:
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_CODE_CAMERA);
                break;

            case R.id.publish_photo_gallery:
                Intent pickPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                pickPhoto.setType("video/*, image/*");
                startActivityForResult(pickPhoto , REQUEST_CODE_GALLERY);
                break;
        }
    }

    private void showShareDialog(ShareContent content) {
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, this);
        shareDialog.show(content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CODE_GALLERY) {

                Uri selectedFile = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedFile, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap image = BitmapFactory.decodeFile(picturePath);

                if (image != null) {
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    ShareContent sharePhoto = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    showShareDialog(sharePhoto);
                } else {
                    ShareVideo video = new ShareVideo.Builder()
                            .setLocalUrl(selectedFile)
                            .build();
                    ShareVideoContent shareVideo = new ShareVideoContent.Builder()
                            .setVideo(video)
                            .build();

                    showShareDialog(shareVideo);
                }

            } else if (requestCode == REQUEST_CODE_CAMERA) {

                Bitmap image = (Bitmap) data.getExtras().get("data");
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                ShareContent sharePhoto = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                showShareDialog(sharePhoto);
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(Sharer.Result result) {

        final People user = People.getPeople(((BaseActivity) getActivity()).getDB(), 1);

        ParseQuery<FacebookPosts> query = new ParseQuery<>(FacebookPosts.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.whereEqualTo(FacebookPosts.FACEBOOK_ID, user.getFacebookId());
        query.findInBackground(new FindCallback<FacebookPosts>() {
            @Override
            public void done(List<FacebookPosts> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 1) {
                        FacebookPosts posts = list.get(0);
                        posts
                                .setPostCounter(posts.getPostCounter() + 1)
                                .saveInBackground();
                    } else if (list.size() == 0) {
                        new FacebookPosts()
                                .setFacebookId(user.getFacebookId())
                                .setUserName(user.getName())
                                .setPostCounter(1)
                                .saveInBackground();
                    }
                }
            }
        });
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException e) {

    }

    private AnimatorSet getButtonAnimator(AnimationDirection direction) {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(showMenu, "translationX", 0, -100).setDuration(250),
                ObjectAnimator.ofFloat(showMenu, "translationY", 0, 20, 30).setDuration(250));
        return animatorSet;
    }

    private AnimatorSet getButtonAnimatorReverse(AnimationDirection direction) {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(showMenu, "translationX", -100, 0).setDuration(250),
                ObjectAnimator.ofFloat(showMenu, "translationY", 30, 20, 0).setDuration(250));
        return animatorSet;
    }

    private SupportAnimator getMenuAnimator(AnimationDirection direction) {
        // get the center for the clipping circle
        int cx = fabCenterX - 150;
        int cy = panel.getHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(panel.getWidth(), panel.getHeight());

        SupportAnimator circularReveal =
                ViewAnimationUtils.createCircularReveal(panel, cx, cy, 0, finalRadius);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        circularReveal.setDuration(250);

        if (direction == AnimationDirection.REVERSE)
            circularReveal = circularReveal.reverse();

        return circularReveal;
    }
}
