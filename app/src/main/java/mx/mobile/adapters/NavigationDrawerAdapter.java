package mx.mobile.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mx.mobile.junamex.R;

/**
 * Created by desarrollo16 on 08/01/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    public static final int SCHEDULE = 0;
    public static final int MAP = 1;
    public static final int SOCIAL_FEED = 2;
    public static final int PEOPLE = 3;
    public static final int MUSEUM = 4;
    public static final int DIVIDER_POSITION = 5;
    public static final int SETTINGS = 6;

    private String[] items;
    private LayoutInflater inflater;
    private Resources resources;

    public NavigationDrawerAdapter(Context context) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        items = context.getResources().getStringArray(R.array.navigation_drawer_items);
        resources = context.getResources();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == DIVIDER_POSITION ? 1 : 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != DIVIDER_POSITION;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position != DIVIDER_POSITION) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else
                holder = (ViewHolder) convertView.getTag();

            TypedArray icons = resources.obtainTypedArray(R.array.navigation_drawer_icons);
            holder.title.setText(items[position]);
            holder.icon.setImageResource(icons.getResourceId(position, -1));

            icons.recycle();

        } else {
            convertView = inflater.inflate(R.layout.navigation_drawer_divider, parent, false);
        }

        return convertView;
    }

    private class ViewHolder {

        TextView title;
        ImageView icon;

        private ViewHolder(View v) {

            title = (TextView) v.findViewById(R.id.drawer_title);
            icon = (ImageView) v.findViewById(R.id.drawer_icon);
        }
    }
}
