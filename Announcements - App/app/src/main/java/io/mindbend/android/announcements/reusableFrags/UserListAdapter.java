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

import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final LinearLayout mUserLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mName = (TextView) itemView.findViewById(R.id.user_name);
            mUserLayout = (LinearLayout)itemView.findViewById(R.id.user_list);
        }
    }

    private List<User> mUsers;
    private Context mContext;
    private UserListInteractionListener mListener;

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
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public UserListAdapter (Context context, List<User> users, UserListInteractionListener listener) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mUsers = users;
        mListener = listener;
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
