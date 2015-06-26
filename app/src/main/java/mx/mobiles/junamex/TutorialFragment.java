package mx.mobiles.junamex;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by desarrollo16 on 24/02/15.
 */
public class TutorialFragment extends Fragment {

    public static final String PAGE_KEY = "page";

    private int pageNumber;

    public static TutorialFragment newInstance(int pageNumber) {

        Bundle bundle = new Bundle();
        bundle.putInt(PAGE_KEY, pageNumber);

        TutorialFragment fragment = new TutorialFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNumber = getArguments().getInt(PAGE_KEY, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View page = inflater.inflate(R.layout.fragment_tutorial, container, false);

        TextView title = (TextView) page.findViewById(R.id.page_title);
        TextView subtitle = (TextView) page.findViewById(R.id.page_subtitle);
        ImageView image = (ImageView) page.findViewById(R.id.page_illustration);
        int color;

        String[] titlesArray = getResources().getStringArray(R.array.tutorial_titles);
        String[] subtitlesArray = getResources().getStringArray(R.array.tutorial_subtitles);
        int[] colorsArray = getResources().getIntArray(R.array.tutorial_colors);
        TypedArray imagesArray = getResources().obtainTypedArray(R.array.tutorial_images);

        color = colorsArray[pageNumber];
        title.setText(titlesArray[pageNumber]);
        subtitle.setText(subtitlesArray[pageNumber]);
        image.setImageResource(imagesArray.getResourceId(pageNumber, -1));
        imagesArray.recycle();

        if (pageNumber > 0) {
            title.setTextColor(getResources().getColor(R.color.primary_text_color_inverse));
            subtitle.setTextColor(getResources().getColor(R.color.primary_text_color_inverse));
        }
        page.setBackgroundColor(color);

        return page;
    }
}
