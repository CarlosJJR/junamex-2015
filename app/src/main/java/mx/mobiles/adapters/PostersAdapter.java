package mx.mobiles.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mx.mobiles.junamex.PeopleDetailActivity;
import mx.mobiles.junamex.R;
import mx.mobiles.model.FacebookPosts;
import mx.mobiles.model.People;
import mx.mobiles.ui.CircleProfilePicture;

/**
 * Created by carlosjimenez on 07/07/15.
 */
public class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.Holder> {

    private ArrayList<FacebookPosts> peopleList;

    public PostersAdapter(ArrayList<FacebookPosts> peopleList) {
        this.peopleList = peopleList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_people, parent, false);

        return new Holder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        FacebookPosts item = peopleList.get(position);

        holder.name.setText(item.getUserName());
        holder.counter.setText(String.format(holder.context.getString(R.string.total_posts_indicator), item.getPostCounter()));
        if (item.getFacebookId() != null)
            holder.photo.setProfileId(item.getFacebookId());
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        CircleProfilePicture photo;
        TextView name, counter;
        Context context;

        private Holder(View itemView, Context context) {
            super(itemView);

            this.context = context;
            photo = (CircleProfilePicture) itemView.findViewById(R.id.people_photo);
            name = (TextView) itemView.findViewById(R.id.people_name);
            counter = (TextView) itemView.findViewById(R.id.people_district);
        }
    }
}
