package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Comment;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

public class PostCommentsFragment extends Fragment implements Serializable, PostCommentsAdapter.CommenterInteractionListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST = "post";
    private static final String ARG_LISTENER = "comment_listener";

    private static final String SHARE_TAG = "Share_post_tag";

    private Post mPost;
    private PostCommentsAdapter mCommentsAdapter;
    private List<Comment> mComments;
    private transient ImageButton mFab;
    private CommentsInteractionListener mListener;
    private transient View mView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postClicked POST
     * @param commentListener CommentsInteractionListener
     * @return A new instance of fragment PostCommentsFragment.
     */
    public static PostCommentsFragment newInstance(Post postClicked, CommentsInteractionListener commentListener) {
        PostCommentsFragment fragment = new PostCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, postClicked);
        args.putSerializable(ARG_LISTENER, commentListener);
        fragment.setArguments(args);
        return fragment;
    }

    public PostCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = (Post) getArguments().getSerializable(ARG_POST);
            mListener = (CommentsInteractionListener) getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null){
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_post_comments, container, false);

            setupPost(mView);

            //TODO: setup recycler view adapter for comments, along with the recycler view feed item layout (comment).
            //TODO: ensure that the feed item is NOT a card -> the enture comment list will be enclosed in one card (already set up)
            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.comments_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mComments = new ArrayList<>();

            //the following is fake data
            Comment testComment1 = new Comment("ID- NeedNameHere", "Wow what a great announcement!", "1 minute ago");
            mComments.add(testComment1);

            Comment testComment2 = new Comment("ID- NeedNameHere", "This is a test comment with a long string of text to see how comments look when stretched. This is super cool wow much happiness.", "Now");
            mComments.add(testComment2);

            Comment testComment3 = new Comment("ID- NeedNameHere", "Wow what a great announcement!This is a test comment with a long string of text to see how comments look when stretched. This is super cool wow much happiness.", "Now");
            mComments.add(testComment3);

            //instantiate and set the adapter
            mCommentsAdapter = new PostCommentsAdapter(getActivity(), mComments, this);
            recyclerView.setAdapter(mCommentsAdapter);

            //the animation for the recycler view to slide in from the bottom of the view
            TranslateAnimation trans = new TranslateAnimation(0, 0, 1000, 0);
            trans.setDuration(500);
            trans.setInterpolator(new DecelerateInterpolator(1.0f));
            recyclerView.startAnimation(trans);

            mFab = (ImageButton) mView.findViewById(R.id.comments_fab);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: dialogue box to add a comment
                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View addCommentView = li.inflate(R.layout.add_comment_dialog, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    // set the dialog's view to alertdialog builder
                    alertDialogBuilder.setView(addCommentView);

                    //work with all the elements in the dialog
                    EditText userInput = (EditText) addCommentView.findViewById(R.id.add_comment_edittext);

                    //set the subtext to notify the user on WHOSE post they are commenting on
                    TextView subText = (TextView) addCommentView.findViewById(R.id.add_comment_subtext);
                    subText.setText(getString(R.string.add_comment_dialog_subtext, mPost.getmPostClubUsername()));

                    //setting up the spinner(dropdown) to select which user/club to post the comment from
                    Spinner spinner = (Spinner) addCommentView.findViewById(R.id.user_spinner);
                    //TODO: dynamically create the user list
                    String[] userArray = {"User 1", "User 2"};
                    ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userArray);
                    spinner.setAdapter(userAdapter);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Post",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //TODO: add a comment to the comments post
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });

            //sharing the post
            Button shareButton = (Button) mView.findViewById(R.id.post_share_button);
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
                    } catch (Exception e){
                        Log.d(SHARE_TAG, "An error occured");
                    }
                }
            });
        }

        return mView;
    }

    private void setupPost(View v) {
        //retrieving all the
        TextView postTitle = (TextView) v.findViewById(R.id.post_title);
        TextView postDetail = (TextView) v.findViewById(R.id.post_detail);
        TextView postTime = (TextView) v.findViewById(R.id.post_time);
        TextView postClubName = (TextView) v.findViewById(R.id.post_club_username);
        ImageView postClubPic = (ImageView) v.findViewById(R.id.post_club_image);
        ImageView postImage = (ImageView) v.findViewById(R.id.post_image_attached);

        postTitle.setText(mPost.getmPostTitle());
        postDetail.setText(mPost.getmPostDetail());
        postTime.setText(mPost.getmPostTimeSince());
        postClubName.setText(mPost.getmPostClubUsername());
        //TODO: SET UP IMAGES AS WELL

        //the back button to return to the posts list from the comments
        Button backButton = (Button)v.findViewById(R.id.back_to_posts_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pressedBackToPosts();
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface CommentsInteractionListener  extends Serializable{
        void pressedBackToPosts();
        void pressedCommenterProfile(User commenterPressed);
    }

    @Override
    public void commenterProfilePressed(User commenterPressed) {
        mListener.pressedCommenterProfile(commenterPressed);
    }
}
