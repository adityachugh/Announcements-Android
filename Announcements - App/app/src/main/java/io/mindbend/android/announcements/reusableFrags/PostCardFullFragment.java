package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.cloudCode.PostsDataSource;

public class PostCardFullFragment extends Fragment implements Serializable, View.OnClickListener {
    private static final String TAG = "PostCardFullFragment";

    private static final String ARG_POST = "post";
    private static final String ARG_LISTENER = "full_post_listener";
    private static final String ARG_VIEW_ONLY = "view_only";

    private static final String SHARE_TAG = "Share_post_tag";

    private Post mPost;
    private FullPostInteractionListener mListener;
    private transient View mView;
    private boolean mViewOnly;

    public static PostCardFullFragment newInstance(Post post, FullPostInteractionListener fullPostListener, boolean viewOnly) {
        PostCardFullFragment fragment = new PostCardFullFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        args.putSerializable(ARG_LISTENER, fullPostListener);
        args.putBoolean(ARG_VIEW_ONLY, viewOnly);
        fragment.setArguments(args);
        return fragment;
    }

    public PostCardFullFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = (Post) getArguments().getSerializable(ARG_POST);
            mListener = (FullPostInteractionListener) getArguments().getSerializable(ARG_LISTENER);
            mViewOnly = getArguments().getBoolean(ARG_VIEW_ONLY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_post_card_full, container, false);

            TextView postTitle = (TextView) mView.findViewById(R.id.post_title_full);
            TextView postDetail = (TextView) mView.findViewById(R.id.post_detail_full);
            TextView postTime = (TextView) mView.findViewById(R.id.post_time_full);
            TextView postClubName = (TextView) mView.findViewById(R.id.post_club_username_full);
            ImageView postClubPic = (ImageView) mView.findViewById(R.id.post_club_image_full);
            ImageView postImage = (ImageView) mView.findViewById(R.id.post_image_attached_full);
            CircleImageView priorityIndicator = (CircleImageView)mView.findViewById(R.id.full_post_priority_indicator);

            postTitle.setText(mPost.getmPostTitle());
            postDetail.setText(mPost.getmPostDetail());
            postTime.setText(mPost.getmPostTimeSince());
            postClubName.setText(mPost.getmPostClubUsername());

            if(mPost.getmPriority() == PostsDataSource.HIGH_PRIORITY){
                priorityIndicator.setVisibility(View.VISIBLE);
            }

            if (!mPost.getmPostImageURL().equals("")){
                //load image

                //image height is 200dp - set layout params from dp to px
                float scale = getActivity().getResources().getDisplayMetrics().density;
                int imageHeightinPx = (int) (200 * scale + 0.5f);
                postImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeightinPx));

                Picasso.with(getActivity()).load(mPost.getmPostImageURL()).into(postImage);

                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog nagDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        nagDialog.setCancelable(false);
                        nagDialog.setContentView(R.layout.preview_image);
                        Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
                        ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);

                        Picasso.with(getActivity()).load(mPost.getmPostImageURL()).into(ivPreview);
                        ivPreview.setBackgroundDrawable(null);

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nagDialog.dismiss();
                            }
                        });
                        nagDialog.show();
                    }
                });

                postClubName.setOnClickListener(this);
                postClubPic.setOnClickListener(this);
                postTitle.setOnClickListener(this);
            }

            if (!mPost.getmPosterOrg().getmProfileImageURL().equals(""))
                Picasso.with(getActivity()).load(mPost.getmPosterOrg().getmProfileImageURL()).into(postClubPic);

            //post buttons
            Button commentButton = (Button) mView.findViewById(R.id.post_comment_button_full);
            Button shareButton = (Button) mView.findViewById(R.id.post_share_button_full);

            if (mViewOnly){
                shareButton.setVisibility(View.GONE);
                commentButton.setText(getActivity().getResources().getString(R.string.back_button_text));
            } else {
                //sharing the post IF NOT VIEW ONLY
                final String sharingPostText = getActivity().getResources().getString(R.string.sharing_post);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String toShareString = String.format(sharingPostText, mPost.getmPostClubUsername(), mPost.getmPostDetail());
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, toShareString);
                        sendIntent.setType("text/plain");
                        try {
                            getActivity().startActivity(Intent.createChooser(sendIntent, getActivity().getResources().getText(R.string.send_to)));
                        } catch (Exception e) {
                            Log.d(SHARE_TAG, "An error occured");
                        }
                    }
                });
            }

            //moving to comment frag OR pressing back button
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewOnly){
                        getParentFragment().getChildFragmentManager().popBackStack();
                    } else{
                        mListener.CommentButtonClicked(mPost);
                    }
                }
            });

            FrameLayout fullPostFramelayout = (FrameLayout) mView.findViewById(R.id.full_post_framelayout);

            //animation
            TranslateAnimation trans = new TranslateAnimation(0, 0, 1000, 0);
            trans.setDuration(500);
            trans.setInterpolator(new DecelerateInterpolator(1.0f));
            fullPostFramelayout.startAnimation(trans);
        }
        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        mListener.openPosterOrgProfile(mPost.getmPosterOrg());
    }

    public interface FullPostInteractionListener extends Serializable {
        void CommentButtonClicked(Post postComments);
        void openPosterOrgProfile(Organization organization);
    }

}