package io.mindbend.android.announcements.reusableFrags;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class PostsFeedAdapter extends RecyclerView.Adapter<PostsFeedAdapter.ViewHolder> implements Serializable {
    private static final String SHARE_TAG = "Share_post_tag";

    private static final String TAG = "PostsFeedAdapter";
    private float mScale;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mDetail;
        private final ImageView mPostImage;
        private final TextView mTimeSince;
        private final TextView mClubUsername;
        private final Button mCommentButton;
        private final Button mShareButton;
        private final CardView mPostCard;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mTitle = (TextView) itemView.findViewById(R.id.post_title);
            mDetail = (TextView) itemView.findViewById(R.id.post_detail);
            mPostImage = (ImageView) itemView.findViewById(R.id.post_image_attached);
            mTimeSince = (TextView) itemView.findViewById(R.id.post_time);
            mClubUsername = (TextView) itemView.findViewById(R.id.post_club_username);
            mCommentButton = (Button) itemView.findViewById(R.id.post_comment_button);
            mShareButton = (Button) itemView.findViewById(R.id.post_share_button);
            mPostCard = (CardView) itemView.findViewById(R.id.post_card);
        }
    }

    //TODO: create private fields for the list
    private List<Post> mPosts;
    private Context mContext;
    private PostInteractionListener mListener;

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

        //add image if present
        if (!post.getmPostImageURL().equals("")){
            viewHolder.mPostImage.setImageResource(R.drawable.landscape);

            //image height is 200dp
            int imageHeightinPx = (int) (200 * mScale + 0.5f);
            viewHolder.mPostImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeightinPx));

            //TODO: click image to open in full screen
        }

        viewHolder.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pressedPostComments(post);
            }
        });

        viewHolder.mPostCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pressedPostCard(post);
                Log.d(TAG, "Post card pressed");
            }
        });

        final String sharingPostText = mContext.getResources().getString(R.string.sharing_post);
        viewHolder.mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toShareString = String.format(sharingPostText, post.getmPostClubUsername(), post.getmPostDetail());
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, toShareString);
                sendIntent.setType("text/plain");
                try {
                    mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.send_to)));
                } catch (Exception e) {
                    Log.d(SHARE_TAG, "An error occured");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public PostsFeedAdapter(Context context, List<Post> posts, PostInteractionListener listener, float scale) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mPosts = posts;
        mListener = listener;
        mScale = scale;
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

    public interface PostInteractionListener extends Serializable {
        void pressedPostComments(Post postPressed);
        void pressedPostCard (Post post);
    }
}
