package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class OrgsGridAdapter extends RecyclerView.Adapter<OrgsGridAdapter.ViewHolder> implements Serializable {

    private static final String TAG = "OrgsGridAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mDetail;
        private final CardView mOrgCard;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView) {
            super(itemView);

            //getting all the elements part of the card, aside from the image
            mTitle = (TextView) itemView.findViewById(R.id.org_title);
            mDetail = (TextView) itemView.findViewById(R.id.org_banner_detail);

            //get entire card to make clickable
            mOrgCard = (CardView) itemView.findViewById(R.id.org_card);
        }
    }

    //TODO: create private fields for the list
    private List<Organization> mOrgs;
    private Context mContext;
    private OrgInteractionListener mOrgListener;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orgs_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Organization org = mOrgs.get(i);

        viewHolder.mTitle.setText(org.getTitle());

        int orgFollowers = org.getFollowers();
        if (org.isNewOrg()) {
            viewHolder.mDetail.setTextColor(mContext.getResources().getColor(R.color.accent));
            viewHolder.mDetail.setText("NEW");
        }
        else {
            viewHolder.mDetail.setText(orgFollowers + " Followers");
        }

        //set clicklistener on card
        viewHolder.mOrgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrgListener.pressedOrg(org);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrgs.size();
    }

    public OrgsGridAdapter(Context context, List<Organization> orgs, OrgInteractionListener listener) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mOrgs = orgs;
        mOrgListener = listener;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mOrgListener = null;
    }

    public interface OrgInteractionListener extends Serializable{
        void pressedOrg (Organization orgSelected);
    }
}
