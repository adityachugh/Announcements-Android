package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

public class PostCardFullFragment extends Fragment implements Serializable {
    private static final String TAG = "PostCardFullFragment";

    private static final String ARG_POST = "post";
    private static final String ARG_LISTENER = "full_post_listener";

    private static final String SHARE_TAG = "Share_post_tag";

    private Post mPost;
    private FullPostInteractionListener mListener;
    private transient View mView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post Post.
     * @param fullPostListener FullPostInteractionListener.
     * @return A new instance of fragment PostCardFull.
     */
    // TODO: Rename and change types and number of parameters
    public static PostCardFullFragment newInstance(Post post, FullPostInteractionListener fullPostListener) {
        PostCardFullFragment fragment = new PostCardFullFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        args.putSerializable(ARG_LISTENER, fullPostListener);
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

            postTitle.setText(mPost.getmPostTitle());
            postDetail.setText(mPost.getmPostDetail());
            postTime.setText(mPost.getmPostTimeSince());
            postClubName.setText(mPost.getmPostClubUsername());

            if (!mPost.getmPostImageURL().equals("")){

                //load image
                postImage.setImageResource(R.drawable.landscape);

                //image height is 200dp - set layout params from dp to px
                float scale = getActivity().getResources().getDisplayMetrics().density;
                int imageHeightinPx = (int) (200 * scale + 0.5f);
                postImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeightinPx));
            }

            //sharing the post
            Button shareButton = (Button) mView.findViewById(R.id.post_share_button_full);
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

            //moving to comment frag
            Button commentButton = (Button) mView.findViewById(R.id.post_comment_button_full);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.CommentButtonClicked(mPost);
                }
            });
        }

        FrameLayout fullPostFramelayout = (FrameLayout) mView.findViewById(R.id.full_post_framelayout);

        //animation
        TranslateAnimation trans = new TranslateAnimation(0, 0, 1000, 0);
        trans.setDuration(500);
        trans.setInterpolator(new DecelerateInterpolator(1.0f));
        fullPostFramelayout.startAnimation(trans);

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

    public interface FullPostInteractionListener extends Serializable {
        void CommentButtonClicked(Post postComments);
    }

}
