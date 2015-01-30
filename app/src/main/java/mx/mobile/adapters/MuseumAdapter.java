package mx.mobile.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;

import mx.mobile.junamex.MuseumDetailActivity;
import mx.mobile.junamex.R;
import mx.mobile.model.MuseumItem;

/**
 * Created by desarrollo16 on 29/01/15.
 */
public class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.Holder> {

    private ArrayList<MuseumItem> museumItems;
    private int lastPosition = -1;
    private Activity activity;

    public MuseumAdapter(Activity activity, ArrayList<MuseumItem> museumItems) {
        this.museumItems = museumItems;
        this.activity = activity;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_museum, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        MuseumItem item = museumItems.get(position);

        ParseFile imageFile = item.getImage();

        if (imageFile != null) {
            holder.imageHolder.setPlaceholder(activity.getResources().getDrawable(R.drawable.museum_placeholder));
            holder.imageHolder.setParseFile(imageFile);
            holder.imageHolder.loadInBackground();
        }

        holder.title.setText(item.getName());
        holder.description.setText(item.getDescription());

        setAnimation(holder.cardView, position);

        holder.setClickListener(new Holder.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                activity, v.findViewById(R.id.image_holder), MuseumDetailActivity.TRANSITION);

                Intent intent = new Intent(activity, MuseumDetailActivity.class);
                intent.putExtra("item_id", museumItems.get(position).getObjectId());
                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return museumItems.size();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ParseImageView imageHolder;
        TextView title, description;
        CardView cardView;

        private OnItemClickListener clickListener;

        public Holder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card);
            imageHolder = (ParseImageView) itemView.findViewById(R.id.image_holder);
            title = (TextView) itemView.findViewById(R.id.item_name);
            description = (TextView) itemView.findViewById(R.id.item_description);

            itemView.setOnClickListener(this);
        }

        public interface OnItemClickListener {

            public void onItemClick(View v, int position);

        }

        public void setClickListener(OnItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {

            clickListener.onItemClick(v, getPosition());
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
