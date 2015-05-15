package mx.mobiles.junamex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

import mx.mobiles.adapters.NavigationDrawerAdapter;

/**
 * Created by carlosjimenez on 14/05/15.
 */
public class SocialFragment extends BaseFragment {

    public static final String TAG = SocialFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        return view;
    }

    @Override
    public int getDrawerPosition() {
        return NavigationDrawerAdapter.SOCIAL_FEED;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.social_feed, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.countdown_background);
        SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareButton shareButton = new ShareButton(getActivity());
        shareButton.setShareContent(content);

        shareButton.callOnClick();

        return true;
    }
}
