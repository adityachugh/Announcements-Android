package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class OrgsListAdapter extends RecyclerView.Adapter<OrgsListAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mName = (TextView) itemView.findViewById(R.id.org_name);
        }
    }

    //TODO: create private fields for the list
    private List<Organization> mOrgs;
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orgs_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Organization org = mOrgs.get(i);
        viewHolder.mName.setText(org.getmTitle());
    }

    @Override
    public int getItemCount() {
        return mOrgs.size();
    }

    public OrgsListAdapter(Context context, List<Organization> orgs) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mOrgs = orgs;
    }
}
