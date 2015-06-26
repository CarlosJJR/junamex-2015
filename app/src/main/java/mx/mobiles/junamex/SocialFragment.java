package mx.mobiles.junamex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;


import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import mx.mobiles.adapters.NavigationDrawerAdapter;

/**
 * Created by carlosjimenez on 14/05/15.
 */
public class SocialFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = SocialFragment.class.getSimpleName();
    public static final int REQUEST_CODE = 9876;

    private FloatingActionButton showMenu;
    private ImageButton closeMenu;
    private TextView tag2;
    private LinearLayout panel;

    private int fabCenterX;

    private enum AnimationDirection{
        NORMAL,
        REVERSE
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_view, container, false);

//        panel = (LinearLayout) view.findViewById(R.id.options_menu);
//
//        showMenu = (FloatingActionButton) view.findViewById(R.id.show_menu_button);
//        closeMenu = (ImageButton) view.findViewById(R.id.close_menu);
//
//        showMenu.setOnClickListener(this);
//        closeMenu.setOnClickListener(this);
//
//        ViewTreeObserver observer = showMenu.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //in here, place the code that requires you to know the dimensions.
//
//                fabCenterX = (showMenu.getLeft() + showMenu.getRight()) / 2;
//                //this will be called as the layout is finished, prior to displaying.
//            }
//        });
        return view;
    }

    @Override
    public int getDrawerPosition() {
        return NavigationDrawerAdapter.SOCIAL_FEED;
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

                        getButtonAnimatorReverse(AnimationDirection.REVERSE);
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
        }


//        ShareContent content = null;
//        switch (index) {
//
//            //Status
//            default:
//            case 0:
//                content = new ShareLinkContent.Builder().build();
//                break;
//
//            //Choose from gallery
//            case 1:
//                break;
//
//            //Take photo
//            case 2:
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(takePicture, REQUEST_CODE);//zero can be replaced with any action code
//                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.countdown_background);
//                SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
//                content = new SharePhotoContent.Builder()
//                        .addPhoto(photo)
//                        .build();
//                break;
//
//            //Take video
//            case 3:
//
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
//                pickPhoto.setType("video/*");
//                startActivityForResult(pickPhoto , REQUEST_CODE);//one can be replaced with any action code
//                break;
//        }
//        if (content != null)
//            ShareDialog.show(this, content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private AnimatorSet getButtonAnimator(AnimationDirection direction) {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(showMenu, "translationX", 0, -150).setDuration(250),
                ObjectAnimator.ofFloat(showMenu, "translationY", 0, 20, 30).setDuration(250));
        return animatorSet;
    }

    private AnimatorSet getButtonAnimatorReverse(AnimationDirection direction) {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(showMenu, "translationX", 0, 30).setDuration(250),
                ObjectAnimator.ofFloat(showMenu, "translationY", 0, -30).setDuration(250));
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
