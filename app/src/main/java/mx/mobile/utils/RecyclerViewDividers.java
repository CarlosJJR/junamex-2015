package mx.mobile.utils;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by desarrollo16 on 13/02/15.
 */
public class RecyclerViewDividers extends RecyclerView.ItemDecoration {

    private int dividerTop, dividerBottom, dividerLeft, dividerRight;

    public RecyclerViewDividers(int dividerTop, int dividerBottom, int dividerLeft, int dividerRight) {
        this.dividerTop = dividerTop;
        this.dividerBottom = dividerBottom;
        this.dividerLeft = dividerLeft;
        this.dividerRight = dividerRight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(dividerLeft, dividerTop, dividerRight, dividerBottom);
    }
}
