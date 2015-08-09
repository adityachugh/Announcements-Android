package io.mindbend.android.announcements.reusableFrags;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Comment;

import java.io.Serializable;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.ViewHolder> implements Serializable{
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mCommenterName;
        private final TextView mTimeSince;
        private final TextView mCommentText;
        private final de.hdodenhof.circleimageview.CircleImageView mPosterImage;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mPosterImage = (de.hdodenhof.circleimageview.CircleImageView)itemView.findViewById(R.id.comment_image);
            mCommenterName = (TextView)itemView.findViewById(R.id.comment_poster_name);
            mTimeSince = (TextView)itemView.findViewById(R.id.comment_time_since);
            mCommentText = (TextView)itemView.findViewById(R.id.comment_text);
        }
    }

    //TODO: create private fields for the list
    private List<io.mindbend.android.announcements.Comment> mComments;
    private Context mContext;
    private UserInteractionListener mUserListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final io.mindbend.android.announcements.Comment comment = mComments.get(i);

        //get user from comment
        final User user = comment.getUser();

        //fill fields with user + comment data
        viewHolder.mCommenterName.setText(user.getName());
        viewHolder.mCommentText.setText(comment.getmText());
        viewHolder.mTimeSince.setText(comment.getmTimeSince());

        //TODO: fill imageview with user profile photo
        //viewHolder.mPosterImage.setImageResource(* IMAGE FROM PARSE *);

        //set on click listener on user image
        viewHolder.mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserListener.pressedUserImage(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public PostCommentsAdapter(Context context, List<io.mindbend.android.announcements.Comment> comments, UserInteractionListener listener) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mComments = comments;
        mUserListener = listener;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mUserListener = null;
    }

    public interface UserInteractionListener extends Serializable{
        void pressedUserImage (User userPressed);
    }
}

