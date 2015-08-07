package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class OrgsListAdapter extends RecyclerView.Adapter<OrgsListAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final LinearLayout mOrgLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mName = (TextView) itemView.findViewById(R.id.org_name);
            mOrgLayout = (LinearLayout)itemView.findViewById(R.id.org_list);
        }
    }

    private List<Organization> mOrgs;
    private Context mContext;
    private OrgListInteractionListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orgs_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Organization org = mOrgs.get(i);
        viewHolder.mName.setText(org.getmTitle());

        viewHolder.mOrgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.orgPressed(org);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrgs.size();
    }

    public OrgsListAdapter(Context context, List<Organization> orgs, OrgListInteractionListener listener) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mOrgs = orgs;
        mListener = listener;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mListener = null;
    }

    public interface OrgListInteractionListener extends Serializable {
        void orgPressed (Organization orgPressed);
    }
}
