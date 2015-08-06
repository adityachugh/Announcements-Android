package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class OrgsGridAdapter extends RecyclerView.Adapter<OrgsGridAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTitle;
        private final TextView mDetail;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView){
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mTitle = (TextView)itemView.findViewById(R.id.org_title);
            mDetail = (TextView)itemView.findViewById(R.id.org_banner_detail);
        }
    }

    //TODO: create private fields for the list
    private List<Organization> mOrgs;
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orgs_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Organization org = mOrgs.get(i);
        viewHolder.mTitle.setText(org.getmTitle());
        String bannerDetail = org.getDescription();
        if (bannerDetail.equals("NEW")){
            viewHolder.mDetail.setTextColor(mContext.getResources().getColor(R.color.accent));
        }
        viewHolder.mDetail.setText(bannerDetail);
    }

    @Override
    public int getItemCount() {
        return mOrgs.size();
    }

    public OrgsGridAdapter(Context context, List<Organization> orgs){
        //save the mPosts private field as what is passed in
        mContext = context;
        mOrgs = orgs;
    }
}
