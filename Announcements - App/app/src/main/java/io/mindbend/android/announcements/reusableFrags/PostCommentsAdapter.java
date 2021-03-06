package io.mindbend.android.announcements.reusableFrags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.Comment;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.CommentsDataSource;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.ViewHolder> implements Serializable {

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mCommenterName;
        private final TextView mTimeSince;
        private final TextView mCommentText;
        private final CircleImageView mPosterImage;
        private final ImageView mAdminImageTag;
        private final LinearLayout mEntireLayout; //for long clicking to delete a comment

        public ViewHolder(View itemView) {
            super(itemView);
            //getting all the elements part of the card, aside from the image
            mPosterImage = (CircleImageView)itemView.findViewById(R.id.comment_image);
            mCommenterName = (TextView)itemView.findViewById(R.id.comment_poster_name);
            mTimeSince = (TextView)itemView.findViewById(R.id.comment_time_since);
            mCommentText = (TextView)itemView.findViewById(R.id.comment_text);
            mAdminImageTag = (ImageView)itemView.findViewById(R.id.comment_is_admin_image_tag);
            mEntireLayout = (LinearLayout)itemView.findViewById(R.id.comment_layout);
        }
    }

    private List<io.mindbend.android.announcements.Comment> mComments;
    private ArrayList<User> mAdmins;
    private transient RelativeLayout mLoadingLayout;
    private Context mContext;
    private CommenterInteractionListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Comment currentComment = mComments.get(i);

        int k = 0;
        boolean commentorIsAdmin = false;
        while(!commentorIsAdmin && k < mAdmins.size()) {
           if (mAdmins.get(k).getmObjectId().equals(currentComment.getmUser().getmObjectId())){
               commentorIsAdmin = true;
               viewHolder.mAdminImageTag.setVisibility(View.VISIBLE);
            }
            k++;
        }

        //viewHolder.mCommenterName.setText(currentComment.getmUserId());
        viewHolder.mCommentText.setText(currentComment.getmText());
        viewHolder.mTimeSince.setText(currentComment.getmTimeSince());
        viewHolder.mCommenterName.setText(currentComment.getmUser().getName());

        //setting up the onClick name or image of commenter in order to open a profile frag
        viewHolder.mCommenterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commenterClicked(currentComment.getmUser());
            }
        });
        viewHolder.mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commenterClicked(currentComment.getmUser());
            }
        });

        String posterImageUrl = currentComment.getmUser().getmProfilePictureURL();
        if (!posterImageUrl.equals(""))
            Picasso.with(mContext).load(posterImageUrl).into(viewHolder.mPosterImage);

        viewHolder.mEntireLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                if (currentComment.getmUser().getmObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
                    builder.setTitle("Delete Comment?")
                            .setMessage("Do you want to delete the comment: \""+currentComment.getmText()+"\"?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    CommentsDataSource.deleteComment(v, mContext, mLoadingLayout, currentComment.getmObjectId(), new FunctionCallback<Boolean>() {
                                        @Override
                                        public void done(Boolean deleted, ParseException e) {
                                            if (e == null && deleted){
                                                mListener.deletedComment(currentComment);
                                            }
                                        }
                                    });
                                }
                            })
                            .show();
                }
                return true;
            }
        });
    }

    private void commenterClicked(User poster) {
        mListener.commenterProfilePressed(poster);
}

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public PostCommentsAdapter(Context context, List<io.mindbend.android.announcements.Comment> comments, ArrayList<User> admins, CommenterInteractionListener commentListener, RelativeLayout loadinglayout) {
        //save the mPosts private field as what is passed in
        mContext = context;
        mComments = comments;
        mListener = commentListener;
        mLoadingLayout = loadinglayout;
        mAdmins = admins;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public interface CommenterInteractionListener extends Serializable {
        void commenterProfilePressed (User commenterPressed);
        void deletedComment(Comment comment);
    }
}