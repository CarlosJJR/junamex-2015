package mx.mobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import mx.mobile.junamex.R;
import mx.mobile.model.PeopleMet;

/**
 * Created by desarrollo16 on 30/01/15.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.Holder> {

    private ArrayList<PeopleMet> peopleList;
    private Context context;

    public PeopleAdapter(Context context, ArrayList<PeopleMet> peopleList) {
        this.peopleList = peopleList;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        CircleImageView photo;
        TextView name;

        private Holder(View itemView) {
            super(itemView);

            photo = (CircleImageView) itemView.findViewById(R.id.people_photo);
            name = (TextView) itemView.findViewById(R.id.people_name);
        }
    }
}
