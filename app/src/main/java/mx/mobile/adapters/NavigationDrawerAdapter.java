package mx.mobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mx.mobile.junamex.R;
import mx.mobile.utils.TextViewFont;

/**
 * Created by desarrollo16 on 08/01/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    String[] items;
    LayoutInflater inflater;
    Context context;
    public NavigationDrawerAdapter(Context context) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

        items = context.getResources().getStringArray(R.array.navigation_drawer_items);
    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position == 4 ? 1 : 0;
//    }
//
//    @Override
//    public boolean isEnabled(int position) {
//        return position != 4;
//    }

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

//        if (position != 4) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);

                holder = new ViewHolder(convertView);

                convertView.setTag(holder);

            } else
                holder = (ViewHolder) convertView.getTag();

            holder.title.setText(items[position]);

//        } else {
//
//            TextView sectionTitle = (TextView) inflater.inflate(R.layout.list_section_header, parent, false);
//            sectionTitle.setTextColor(Color.GRAY);
//            sectionTitle.setText(items.get(position));
//
//            convertView = sectionTitle;
//
//        }

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
