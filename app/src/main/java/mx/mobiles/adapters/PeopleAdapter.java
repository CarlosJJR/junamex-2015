package mx.mobiles.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mx.mobiles.junamex.PeopleDetailActivity;
import mx.mobiles.junamex.R;
import mx.mobiles.model.People;
import mx.mobiles.utils.Utilities;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.Holder> {

    private ArrayList<People> peopleList;
    private Activity activity;

    public PeopleAdapter(Activity activity, ArrayList<People> peopleList) {
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

        People item = peopleList.get(position);
        holder.name.setText(item.getName());
        holder.district.setText(R.string.district_list);
        String districtTextComplete = holder.district.getText() + item.getDistrict();
        holder.district.setText(districtTextComplete);
        holder.photo.setImageResource(Utilities.getRandomAvatar(activity));

        holder.setClickListener(new Holder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

//                ActivityOptionsCompat options =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                activity, v.findViewById(R.id.image_holder), MuseumDetailActivity.TRANSITION);

                Intent intent = new Intent(activity, PeopleDetailActivity.class);
                intent.putExtra(People.TABLE, peopleList.get(position).getId());
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
        TextView name, district;
        OnItemClickListener clickListener;

        private Holder(View itemView) {
            super(itemView);

            photo = (CircleImageView) itemView.findViewById(R.id.people_photo);
            name = (TextView) itemView.findViewById(R.id.people_name);
            district = (TextView) itemView.findViewById(R.id.people_district);

            itemView.setOnClickListener(this);
        }

        public interface OnItemClickListener {
            public void onItemClick(int position);

        }

        public void setClickListener(OnItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getPosition());
        }
    }
}
