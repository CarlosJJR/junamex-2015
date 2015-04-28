package mx.mobiles.utils;

/**
 * Created by desarrollo16 on 24/04/15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleDividerDecorator extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public SimpleDividerDecorator(Context context) {
        int[] attrs = { android.R.attr.listDivider };
        TypedArray ta = context.obtainStyledAttributes(attrs);
        //Get Drawable and use as needed
        mDivider = ta.getDrawable(0);
        //Clean Up
        ta.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}