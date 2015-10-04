package io.mindbend.android.announcements.reusableFrags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.PostsDataSource;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class PostsFeedAdapter extends RecyclerView.Adapter<PostsFeedAdapter.ViewHolder> implements Serializable{
    private static final String SHARE_TAG = "Share_post_tag";
    private float mScale;
    private boolean mIsApproving;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTitle;
        private final TextView mDetail;
        private final ImageView mPostImage;
        private final TextView mTimeSince;
        private final TextView mClubUsername;
        private final Button mCommentButton;
        private final Button mShareButton;
        private final CardView mPostCard;
        private final de.hdodenhof.circleimageview.CircleImageView mPosterImage;
        private final TextView mAnnouncementState;
        private final de.hdodenhof.circleimageview.CircleImageView mPriorityIndicator;

        //TODO: create private fields for the elements within a single feed item

        public ViewHolder(View itemView){
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mTitle = (TextView)itemView.findViewById(R.id.post_title);
            mDetail = (TextView)itemView.findViewById(R.id.post_detail);
            mPostImage = (ImageView) itemView.findViewById(R.id.post_image_attached);
            mTimeSince = (TextView)itemView.findViewById(R.id.post_time);
            mClubUsername = (TextView)itemView.findViewById(R.id.post_club_username);
            mCommentButton = (Button)itemView.findViewById(R.id.post_comment_button);
            mShareButton = (Button)itemView.findViewById(R.id.post_share_button);
            mPostCard = (CardView) itemView.findViewById(R.id.post_card);
            mPosterImage = (de.hdodenhof.circleimageview.CircleImageView)itemView.findViewById(R.id.post_club_image);
            mAnnouncementState = (TextView)itemView.findViewById(R.id.announcements_state_TV);
            mPriorityIndicator = (CircleImageView)itemView.findViewById(R.id.post_priority_indicator);
        }
    }

    //TODO: create private fields for the list
    private List<Post> mPosts;
    private Context mContext;
    private PostInteractionListener mListener;
    private de.hdodenhof.circleimageview.CircleImageView mPressedImage;
    private boolean mIsViewingState;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final Post post = mPosts.get(i);
        viewHolder.mTitle.setText(post.getmPostTitle());
        viewHolder.mDetail.setText(post.getmPostDetail());
        viewHolder.mTimeSince.setText(post.getmPostTimeSince());
        viewHolder.mClubUsername.setText(post.getmPostClubUsername());
        if(post.getmPriority() == PostsDataSource.HIGH_PRIORITY){
            viewHolder.mPriorityIndicator.setVisibility(View.VISIBLE);
        }

        //add image if present
        if (post != null && post.getmPostImageURL() != null && !post.getmPostImageURL().equals("")){

            //image height is 200dp
            int imageHeightinPx = (int) (200 * mScale + 0.5f);
            viewHolder.mPostImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeightinPx));

            Picasso.with(mContext).load(post.getmPostImageURL()).skipMemoryCache().into(viewHolder.mPostImage);
        }

        if (post != null && post.getmPosterOrg() != null && post.getmPosterOrg().getmProfileImageURL() != null && !post.getmPosterOrg().getmProfileImageURL().equals(""))
            Picasso.with(mContext).load(post.getmPosterOrg().getmProfileImageURL()).skipMemoryCache().resize(100,100).into(viewHolder.mPosterImage);

        viewHolder.mPostCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pressedPostCard(post);
            }
        });

        if (mIsApproving){
            //if approving posts (admin)

            //TODO: FIX! change positon of text to inside card
//            //time since tag must include notifying body
//            boolean notifyingParent = post.isNotifyingParent();
//            String notifyingWho = "";
//            if (notifyingParent){
//                notifyingWho = post.getmPosterOrg().getmParentLevel().getmLevelTitle();
//            } else {
//                //notifyingWho = post.getmPosterOrg().getmMainLevel().getmLevelTitle();
//            }
//            viewHolder.mTimeSince.setText(post.getmPostTimeSince() + "\nNotifying: " + notifyingWho);

            //comment button becomes accept
            viewHolder.mCommentButton.setText(mContext.getString(R.string.approve_button_text));
            viewHolder.mCommentButton.setTextColor(mContext.getResources().getColor(R.color.announcement_accepted));


            viewHolder.mCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //actOnApprovalRequest; true
                    actOnApprovalRequest(i, viewHolder.itemView, post.getmObjectId(), post.getmPosterOrg().getmObjectId(), true, null, post.getmPriority());
                }
            });

            //share button becomes decline
            viewHolder.mShareButton.setText(mContext.getString(R.string.decline_button_text));
            viewHolder.mShareButton.setTextColor(mContext.getResources().getColor(R.color.announcement_declined));

            viewHolder.mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //alert dialog to get reason
                    final EditText reason = new EditText(mContext);
                    reason.setHint(mContext.getString(R.string.declining_post_dialogbox_input_prompt));

                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getString(R.string.declining_post_dialogbox_title))
                            .setView(reason)
                            .setPositiveButton((mContext.getString(R.string.decline_button_text)), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    actOnApprovalRequest(i, viewHolder.itemView, post.getmObjectId(), post.getmPosterOrg().getmObjectId(), false, reason.getText().toString(), post.getmPriority());
                                }
                            })
                            .setNegativeButton(mContext.getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                }
            });
        }
        else if(mIsViewingState){
            viewHolder.mCommentButton.setVisibility(View.GONE);
            viewHolder.mShareButton.setVisibility(View.GONE);
            viewHolder.mAnnouncementState.setVisibility(View.VISIBLE);

            viewHolder.mPostCard.setLongClickable(true);
            viewHolder.mPostCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getString(R.string.delete_post_dialog_title))
                            .setMessage(mContext.getString(R.string.delete_post_dialog_text))
                            .setPositiveButton(mContext.getString(R.string.yes_button_text), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deletePost(viewHolder.itemView, post.getmPosterOrg().getmObjectId(), post.getmObjectId());
                                    mPosts.remove(i);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(mContext.getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                    return true;
                }
            });

            String status = post.getmStatus().toUpperCase(); //just in case - already uppercase in DB

            int textColor;
            final String typeString;
            final String alertMessage;

                if (status.equals("A")) {
                    textColor = mContext.getResources().getColor(R.color.announcement_accepted);
                    typeString = mContext.getResources().getString(R.string.announcement_approved);
                    alertMessage = "Your post \"" + post.getmPostTitle() + "\" was approved!\n\nIt will run from " + post.getStartDate() + " to " + post.getEndDate();
                } else if (status.equals("R")) {
                    textColor = mContext.getResources().getColor(R.color.announcement_declined);
                    typeString = mContext.getResources().getString(R.string.announcement_declined);

                    String rejectionReason = post.getmRejectionReason();
                    if (rejectionReason != null) //for safeproofing
                        rejectionReason = rejectionReason.trim();

                    String rejectionDialogText;
                    if (rejectionReason == null || rejectionReason.equals(""))
                        rejectionDialogText = "No reason given";
                    else
                        rejectionDialogText = rejectionReason;

                    alertMessage = "Your post \"" + post.getmPostTitle() + "\" was rejected.\n\nRejection reason: " + rejectionDialogText;
                } else /*if (status.equals("P"))*/ {
                    textColor = mContext.getResources().getColor(R.color.announcement_pending);
                    typeString = mContext.getResources().getString(R.string.announcement_pending);
                    alertMessage = "Your post \"" + post.getmPostTitle() + "\" is pending.\n\nAn administrator will review it soon.";
                }

            viewHolder.mAnnouncementState.setTextColor(textColor);
            viewHolder.mAnnouncementState.setText(typeString);

            //rejection reason alert dialog
            viewHolder.mAnnouncementState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle(typeString)
                            .setMessage(alertMessage)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //nothing
                                }
                            })
                            .show();
                }
            });


        }
        else {
            //normal view
            viewHolder.mCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.pressedPostComments(post);
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
                    } catch (Exception e){
                        Log.d(SHARE_TAG, "An error occured");
                    }
                }
            });
        }
    }

    //act on approval state
    private void actOnApprovalRequest (final int position, View view, String postObjectId, String organizationObjectId, boolean approvalState, String rejectionReason, int priority){
         AdminDataSource.actOnApprovalRequest(mContext, view, postObjectId, organizationObjectId, approvalState, rejectionReason, priority, new FunctionCallback<Boolean>() {
             @Override
             public void done(Boolean aBoolean, ParseException e) {
                 if (e == null) {
                     mPosts.remove(position);
                     notifyDataSetChanged();
                 }
             }
         });
    }

    private void deletePost (View view, String orgObjectId, String postObjectId){
        AdminDataSource.deletePost(mContext, view, orgObjectId, postObjectId, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean aBoolean, ParseException e) {
                if (e == null){
                    //nothing, snackbars handled in function
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public PostsFeedAdapter(Context context, List<Post> posts, PostInteractionListener listener, boolean isViewingState, float scale, boolean isApproving) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mPosts = posts;
        mScale = scale;
        mIsApproving = isApproving;

        /**
         * The listener should be NULL only if the @param isViewingState is TRUE
         */

        mListener = listener;

        mIsViewingState = isViewingState;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public interface PostInteractionListener extends Serializable{
        void pressedPostComments(Post postPressed);
        void pressedPostCard (Post post);
    }
}
