package io.mindbend.android.announcements.reusableFrags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.onboardingAndSignupin.OrgsToFollow;
import io.mindbend.android.announcements.onboardingAndSignupin.SignUpOrgsActivity;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class OrgsGridAdapter extends RecyclerView.Adapter<OrgsGridAdapter.ViewHolder> implements Serializable {

    private static final String TAG = "OrgsGridAdapter";
    private boolean mIsInArray;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mDetail;
        private final CardView mOrgCard;
        private final ImageView mBackgroundImage;
        private final ImageView mDarkOverlayImage;
        private final Button mFollowButton;

        public ViewHolder(View itemView) {
            super(itemView);

            if (!mSignUp){
                //getting all the elements part of the card, aside from the image
                mTitle = (TextView) itemView.findViewById(R.id.org_title);
                mDetail = (TextView) itemView.findViewById(R.id.org_banner_detail);
                mBackgroundImage = (ImageView)itemView.findViewById(R.id.org_background_image);
                mDarkOverlayImage = (ImageView) itemView.findViewById(R.id.org_dark_overlay);
                mFollowButton = null;
                //get entire card to make clickable
                mOrgCard = (CardView) itemView.findViewById(R.id.org_card);
            }
            else{
                //getting all the elements part of the card, aside from the image
                mTitle = (TextView) itemView.findViewById(R.id.org_title_signup);
                mBackgroundImage = (ImageView)itemView.findViewById(R.id.org_background_image_signup);
                mDarkOverlayImage = (ImageView) itemView.findViewById(R.id.org_dark_overlay_signup);
                mDetail = null;
                mFollowButton = (Button) itemView.findViewById(R.id.org_follow_signup);
                //get entire card to make clickable
                mOrgCard = (CardView) itemView.findViewById(R.id.org_card_signup);
            }
        }
    }


    private List<Organization> mOrgs;
    private Context mContext;
    private OrgInteractionListener mOrgListener;
    private boolean mSignUp;
    private int mObjectPosition;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View vSignUp = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orgs_feed_item_signup, viewGroup, false);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orgs_feed_item, viewGroup, false);
        if (mSignUp)
            return new ViewHolder(vSignUp);
        else
            return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Organization org = mOrgs.get(i);

        viewHolder.mTitle.setText(org.getTitle());

        int orgFollowers = org.getFollowers();
        if (!mSignUp){
            if (org.isNewOrg()) {
                viewHolder.mDetail.setTextColor(mContext.getResources().getColor(R.color.accent));
                viewHolder.mDetail.setText("NEW");
            }
            else {
                viewHolder.mDetail.setText(orgFollowers + " Followers");
            }
        }

        if (org.getmCoverImageURL() != null && !org.getmCoverImageURL().equals("")){
            Picasso.with(mContext).load(org.getmCoverImageURL()).resize(500,500).skipMemoryCache().into(viewHolder.mBackgroundImage);
            viewHolder.mDarkOverlayImage.setVisibility(View.VISIBLE); //so the darkening of the card only occurs when an image is loaded, not on the green color
        }

        if (mSignUp){
            ArrayList<String> followedOrgs = OrgsToFollow.getInstance();
            if (followedOrgs != null){
                //if arraylist isnt empty
                for (int j = 0; j < followedOrgs.size(); j++) {
                    String orgId = followedOrgs.get(j);
                    if (org.getmObjectId().equals(orgId)){
                        //following button
                        viewHolder.mFollowButton.setText("Following");
                        viewHolder.mFollowButton.setTextColor(mContext.getResources().getColor(R.color.white));
                        viewHolder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.accent));
                    }
                }
            }


            viewHolder.mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> followedOrgs = OrgsToFollow.getInstance(); //get newest updated instance each time button is clicked
                    mIsInArray = false; //always reset value to false on click

                    for (int j = 0; j < followedOrgs.size(); j++) {
                        String orgId = followedOrgs.get(j);
                        if (orgId.equals(org.getmObjectId())) {
                            //entire loop to iterate without taking action.
                            mIsInArray = true;
                            mObjectPosition = j;
                        }
                    }

                    if (mIsInArray){
                        //already in array, therefore unfollow
                        Log.wtf(TAG, "unfollowed");
                        OrgsToFollow.getInstance().remove(followedOrgs.get(mObjectPosition));
                        viewHolder.mFollowButton.setText("Follow");
                        viewHolder.mFollowButton.setTextColor(mContext.getResources().getColor(R.color.accent));
                        viewHolder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
                    } else {
                        if (!org.hasAccessCode()){
                            //not in array, therefore follow
                            Log.wtf(TAG, "followed!");
                            OrgsToFollow.getInstance().add(org.getmObjectId());
                            viewHolder.mFollowButton.setText("Following");
                            viewHolder.mFollowButton.setTextColor(mContext.getResources().getColor(R.color.white));
                            viewHolder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.accent));
                        }
                        else if (org.hasAccessCode()){
                            //access code; pending follower
                            //alert dialog to get reason
                            LinearLayout layout = new LinearLayout(mContext);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(16, 0, 16, 0);

                            final EditText accessCode = new EditText(mContext);
                            accessCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                            accessCode.setHint(mContext.getString(R.string.enter_access_code_for_private_org_message));
                            layout.addView(accessCode);

                            new AlertDialog.Builder(mContext, R.style.DialogTheme)
                                    .setTitle(mContext.getString(R.string.access_code_dialog_title))
                                    .setView(layout)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            int enteredCode = Integer.parseInt(accessCode.getText().toString());
                                            if (enteredCode == org.getmAccessCode()){
                                                //correct code entered
                                                Log.wtf(TAG, "pending!");
                                                OrgsToFollow.getInstance().add(org.getmObjectId());
                                                viewHolder.mFollowButton.setText("Pending");
                                                viewHolder.mFollowButton.setTextColor(mContext.getResources().getColor(R.color.white));
                                                viewHolder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.post_priority_medium));

                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
                                                builder.setTitle("Follow request sent")
                                                        .setPositiveButton("OK", null)
                                                        .show();
                                            } else {
                                                //incorrect code
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
                                                builder.setTitle("Incorrect access code entered.")
                                                        .setPositiveButton("OK", null)
                                                        .show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(mContext.getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    })
                                    .show();

                        }

                    }
                }
            });
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

    public OrgsGridAdapter(Context context, List<Organization> orgs, OrgInteractionListener listener, boolean signUp) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mOrgs = orgs;
        mOrgListener = listener;
        mSignUp = signUp;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public interface OrgInteractionListener extends Serializable{
        void pressedOrg (Organization orgSelected);
    }
}
