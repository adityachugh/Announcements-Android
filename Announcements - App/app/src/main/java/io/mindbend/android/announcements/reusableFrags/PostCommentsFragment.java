package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Comment;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

public class PostCommentsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST = "post";

    private Post mPost;
    private PostCommentsAdapter mCommentsAdapter;
    private List<Comment> mComments;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postClicked POST
     * @return A new instance of fragment PostCommentsFragment.
     */
    public static PostCommentsFragment newInstance(Post postClicked) {
        PostCommentsFragment fragment = new PostCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, postClicked);
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
            mPost = (Post)getArguments().getSerializable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_comments, container, false);

        setupPost(v);

        //TODO: setup recycler view adapter for comments, along with the recycler view feed item layout (comment).
        //TODO: ensure that the feed item is NOT a card -> the enture comment list will be enclosed in one card (already set up)
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mComments = new ArrayList<>();

        //the following is fake data
        Comment testComment1 = new Comment("ID- NeedNameHere", "Wow what a great announcement!", "1 minute ago");
        mComments.add(testComment1);

//        Comment testComment2 = new Comment("ID- NeedNameHere", "This is a test comment with a long string of text to see how comments look when stretched. This is super cool wow much happiness.", "Now");
//        mComments.add(testComment2);
//
//        Comment testComment3 = new Comment("ID- NeedNameHere", "Wow what a great announcement!This is a test comment with a long string of text to see how comments look when stretched. This is super cool wow much happiness.", "Now");
//        mComments.add(testComment3);

        //instantiate and set the adapter
        mCommentsAdapter = new PostCommentsAdapter(getActivity(), mComments);
        recyclerView.setAdapter(mCommentsAdapter);

        return v;
    }

    private void setupPost(View v) {
        //retrieving all the
        TextView postTitle = (TextView)v.findViewById(R.id.post_title);
        TextView postDetail = (TextView)v.findViewById(R.id.post_detail);
        TextView postTime = (TextView)v.findViewById(R.id.post_time);
        TextView postClubName = (TextView)v.findViewById(R.id.post_club_username);
        ImageView postClubPic = (ImageView)v.findViewById(R.id.post_club_image);
        ImageView postImage = (ImageView)v.findViewById(R.id.post_image_attached);

        postTitle.setText(mPost.getmPostTitle());
        postDetail.setText(mPost.getmPostDetail());
        postTime.setText(mPost.getmPostTimeSince());
        postClubName.setText(mPost.getmPostClubUsername());
        //TODO: SET UP IMAGES AS WELL
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
