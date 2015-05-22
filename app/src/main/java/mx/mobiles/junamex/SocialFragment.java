package mx.mobiles.junamex;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;


import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import mx.mobiles.adapters.NavigationDrawerAdapter;

/**
 * Created by carlosjimenez on 14/05/15.
 */
public class SocialFragment extends BaseFragment implements DialogInterface.OnClickListener, View.OnClickListener {

    public static final String TAG = SocialFragment.class.getSimpleName();
    public static final int REQUEST_CODE = 9876;

    private Button button;
    private TextView tag2;
    private LinearLayout panel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        button = (Button) view.findViewById(R.id.button2);
        tag2 = (TextView) view.findViewWithTag(R.id.tag2);
        panel = (LinearLayout) view.findViewById(R.id.options_panel);

        button.setOnClickListener(this);
        return view;
    }

    @Override
    public int getDrawerPosition() {
        return NavigationDrawerAdapter.SOCIAL_FEED;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.social_feed, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showPostOptions();
        return true;
    }

    private void showPostOptions() {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.share_options_dialog_title)
                .setItems(R.array.share_options, this)
                .show();
    }

    //View onClickListener
    @Override
    public void onClick(View view) {

        if (panel.getVisibility() == View.VISIBLE) {
            panel.setVisibility(View.INVISIBLE);
        } else {

            int[] center = new int[2];
            button.getLocationOnScreen(center);

            // get the center for the clipping circle
            int cx = panel.getRight() - 16;
            int cy = panel.getTop();

            // get the final radius for the clipping circle
            int finalRadius = Math.max(panel.getWidth(), panel.getHeight());

            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(panel, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(250);
            animator.start();

            panel.setVisibility(View.VISIBLE);
        }
    }

    //Dialog onClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int index) {

        ShareContent content = null;
        switch (index) {

            //Status
            default:
            case 0:
                content = new ShareLinkContent.Builder().build();
                break;

            //Choose from gallery
            case 1:
                break;

            //Take photo
            case 2:
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_CODE);//zero can be replaced with any action code
//                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.countdown_background);
//                SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
//                content = new SharePhotoContent.Builder()
//                        .addPhoto(photo)
//                        .build();
                break;

            //Take video
            case 3:

                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                pickPhoto.setType("video/*");
                startActivityForResult(pickPhoto , REQUEST_CODE);//one can be replaced with any action code
                break;
        }
        if (content != null)
            ShareDialog.show(this, content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
