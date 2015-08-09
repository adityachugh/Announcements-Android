package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.ViewHolder> implements View.OnClickListener {
    private CommenterInteractionListener mListener;
    private io.mindbend.android.announcements.Comment mCurrentComment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mCommenterName;
        private final TextView mTimeSince;
        private final TextView mCommentText;
        private final CircleImageView mPosterImage;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mPosterImage = (CircleImageView)itemView.findViewById(R.id.comment_image);
            mCommenterName = (TextView)itemView.findViewById(R.id.comment_poster_name);
            mTimeSince = (TextView)itemView.findViewById(R.id.comment_time_since);
            mCommentText = (TextView)itemView.findViewById(R.id.comment_text);
        }
    }

    //TODO: create private fields for the list
    private List<io.mindbend.android.announcements.Comment> mComments;
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        mCurrentComment = mComments.get(i);
        viewHolder.mCommenterName.setText(mCurrentComment.getmUserId());
        viewHolder.mCommentText.setText(mCurrentComment.getmText());
        viewHolder.mTimeSince.setText(mCurrentComment.getmTimeSince());

        //setting up the onClick name or image of commenter in order to open a profile frag
        viewHolder.mCommenterName.setOnClickListener(this);
        viewHolder.mPosterImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //FAKE USER FOR TESTING
        User testUser = new User("Aditya", "Chugh", "getting paper", "node.js", "#Grade12", 9);

        //TODO: get user from the object id of the user saved in the comment

        mListener.commenterProfilePressed(testUser);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public PostCommentsAdapter(Context context, List<io.mindbend.android.announcements.Comment> comments, CommenterInteractionListener commentListener) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mComments = comments;
        mListener = commentListener;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mListener = null;
    }

    public interface CommenterInteractionListener {
        void commenterProfilePressed (User commenterPressed);
    }
}

