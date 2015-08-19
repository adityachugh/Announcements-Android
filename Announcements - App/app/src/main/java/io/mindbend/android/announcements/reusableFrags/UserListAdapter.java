package io.mindbend.android.announcements.reusableFrags;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> implements Serializable {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final LinearLayout mUserLayout;
        private final ImageView mUserIcon;

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
        }
    }

    public static final int USERS_MEMBERS = 0;
    public static final int USERS_ADMINS = 1;
    public static final int USERS_PENDING = 2;


    private List<User> mUsers;
    private Context mContext;
    private UserListInteractionListener mListener;
    private HashMap<User, Integer> mTypeOfUsers;
    private boolean mIsSearching;
    private Organization mOrg;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //TODO: pass data from notif to the elements of the feed item
        final User user = mUsers.get(i);
        viewHolder.mName.setText(user.getName());

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
                    //TODO: add user as admin
                    Toast.makeText(mContext, user.getName()+" is now an admin", Toast.LENGTH_LONG).show();
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
                        //TODO: interface for acceptance here
                        Log.wtf("pending user", "accept");
                    }
                });

                viewHolder.mDenyUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: interface for decline here
                        Log.wtf("pending user", "deny");
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public UserListAdapter (Context context, List<User> users, UserListInteractionListener listener, HashMap<User, Integer> typeOfUsers, boolean isSearching, Organization parentOrgIfSearching) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mUsers = users;
        mListener = listener;
        mTypeOfUsers = typeOfUsers;
        mIsSearching = isSearching;
        mOrg = parentOrgIfSearching;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mListener = null;
    }

    public interface UserListInteractionListener extends Serializable {
        void userSelected (User userPressed);
    }
}
