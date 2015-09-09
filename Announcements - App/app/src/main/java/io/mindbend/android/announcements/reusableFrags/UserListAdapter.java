package io.mindbend.android.announcements.reusableFrags;

import android.app.ActionBar;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> implements Serializable {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final LinearLayout mUserLayout;
        private final ImageView mUserIcon;
        private final CircleImageView mUserImage;

        //for the accept and decline buttons, if a pending user
        private final ImageButton mAcceptUser;
        private final ImageButton mDenyUser;

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mName = (TextView) itemView.findViewById(R.id.user_name);
            mUserLayout = (LinearLayout)itemView.findViewById(R.id.user_list);
            mUserIcon = (ImageView)itemView.findViewById(R.id.user_list_right_icon);
            mAcceptUser = (ImageButton)itemView.findViewById(R.id.pending_user_accept_button);
            mDenyUser = (ImageButton)itemView.findViewById(R.id.pending_user_deny_button);
            mUserImage = (CircleImageView)itemView.findViewById(R.id.user_list_image);
        }
    }

    public static final int USERS_MEMBERS = 0;
    public static final int USERS_ADMINS = 1;
    public static final int USERS_PENDING = 2;
    public static final int USERS_REJECTED = 3;
    public static final int USERS_NOT_FOLLOWING = 4;


    private List<User> mUsers;
    private Context mContext;
    private UserListInteractionListener mListener;
    private HashMap<User, Integer> mTypeOfUsers;
    private boolean mIsSearching;
    private Organization mOrg;

    //for acting on a follow request
    private View mView;
    private ProgressBar mLoading;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        viewHolder.mName.setText(user.getName());

        if (user.getmProfilePictureURL() != null && !user.getmProfilePictureURL().equals(""))
            Picasso.with(mContext).load(user.getmProfilePictureURL()).skipMemoryCache().resize(100,100).into(viewHolder.mUserImage);

        viewHolder.mUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.userSelected(user);
            }
        });

        if(mIsSearching){
            viewHolder.mUserIcon.setImageResource(R.drawable.ic_accept);
            viewHolder.mUserIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.selectedUserToBeAdmin(user, mOrg);
                }
            });
        }

        if (mTypeOfUsers != null){
            if (mTypeOfUsers.get(user) == USERS_ADMINS)
                viewHolder.mUserIcon.setImageResource(R.drawable.ic_admin);
            else if (mTypeOfUsers.get(user) == USERS_PENDING){
                viewHolder.mAcceptUser.setVisibility(View.VISIBLE);
                viewHolder.mDenyUser.setVisibility(View.VISIBLE);

                viewHolder.mAcceptUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.wtf("pending user", "accept");
                        actOnFollowRequestCompleted(user, true);
                    }
                });

                viewHolder.mDenyUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.wtf("pending user", "deny");
                        actOnFollowRequestCompleted(user, false);
                    }
                });
            }
        }
    }

    public void actOnFollowRequestCompleted(final User user, final boolean isApproved) {
        AdminDataSource.actOnFollowRequest(mView, mLoading, mOrg.getmObjectId(), user.getmFollowObjectId(), isApproved, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                if (success && e == null){
                    mTypeOfUsers.remove(user);
                    if (isApproved){ //working
                        mTypeOfUsers.put(user, USERS_MEMBERS);
                    }
                    for (int i = 0; i < mUsers.size(); i++){
                        if (mUsers.get(i).equals(user)){
                            if (isApproved)
                                notifyItemChanged(i);
                            else {
                                mUsers.remove(user);
                                notifyItemRemoved(i);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public UserListAdapter (Context context, List<User> users, UserListInteractionListener listener,
                            HashMap<User, Integer> typeOfUsers, boolean isSearching,
                            Organization parentOrgIfSearching, View view, ProgressBar loading) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mUsers = users;
        mListener = listener;
        mTypeOfUsers = typeOfUsers;
        mIsSearching = isSearching;
        mOrg = parentOrgIfSearching;


        mView = view;
        mLoading = loading;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public interface UserListInteractionListener extends Serializable {
        void userSelected (User userPressed);
        void selectedUserToBeAdmin (User user, Organization nullableOrg);
    }
}
