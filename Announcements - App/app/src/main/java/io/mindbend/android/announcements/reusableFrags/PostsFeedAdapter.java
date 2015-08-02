package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class PostsFeedAdapter extends RecyclerView.Adapter<PostsFeedAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTitle;
        private final TextView mDetail;
        private final TextView mTimeSince;
        private final TextView mClubUsername;
        private final Button mCommentButton;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView){
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mTitle = (TextView)itemView.findViewById(R.id.post_title);
            mDetail = (TextView)itemView.findViewById(R.id.post_detail);
            mTimeSince = (TextView)itemView.findViewById(R.id.post_time);
            mClubUsername = (TextView)itemView.findViewById(R.id.post_club_username);
            mCommentButton = (Button)itemView.findViewById(R.id.post_comment_button);
        }
    }

    //TODO: create private fields for the list
    private List<Post> mPosts;
    private Context mContext;
    private static PostInteractionListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Post post = mPosts.get(i);
        viewHolder.mTitle.setText(post.getmPostTitle());
        viewHolder.mDetail.setText(post.getmPostDetail());
        viewHolder.mTimeSince.setText(post.getmPostTimeSince());
        viewHolder.mClubUsername.setText(post.getmPostClubUsername());

        viewHolder.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pressedPost(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public PostsFeedAdapter(Context context, List<Post> posts){
        //save the mPosts private field as what is passed in
        mContext = context;
        mPosts = posts;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
//        try {
//            mListener = (PostInteractionListener)holder;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(holder.toString()
//                    + " must implement PostInteractionListener");
//        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mListener = null;
    }

    public interface PostInteractionListener {
        public void pressedPost(Post postPressed);
    }

    public static void setListener(PostInteractionListener mListener) {
        PostsFeedAdapter.mListener = mListener;
    }
}
