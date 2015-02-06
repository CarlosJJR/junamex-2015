package mx.mobile.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mx.mobile.junamex.PeopleMetDetailActivity;
import mx.mobile.junamex.R;
import mx.mobile.model.PeopleMet;
import mx.mobile.utils.Utilities;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.Holder> {

    private ArrayList<PeopleMet> peopleList;
    private Activity activity;

    public PeopleAdapter(Activity activity, ArrayList<PeopleMet> peopleList) {
        this.peopleList = peopleList;
        this.activity = activity;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_people, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        PeopleMet item = peopleList.get(position);
        holder.name.setText(item.getName());
        holder.photo.setImageResource(Utilities.getRandomAvatar(activity));

        holder.setClickListener(new Holder.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

//                ActivityOptionsCompat options =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                activity, v.findViewById(R.id.image_holder), MuseumDetailActivity.TRANSITION);

                Intent intent = new Intent(activity, PeopleMetDetailActivity.class);
                intent.putExtra(PeopleMet.TABLE, peopleList.get(position).getId());
                activity.startActivity(intent);
//                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView photo;
        TextView name;
        OnItemClickListener clickListener;

        private Holder(View itemView) {
            super(itemView);

            photo = (CircleImageView) itemView.findViewById(R.id.people_photo);
            name = (TextView) itemView.findViewById(R.id.people_name);

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
}
