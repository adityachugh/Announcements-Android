package io.mindbend.android.announcements.reusableFrags;


import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;


public class PostOverlayFragment extends Fragment implements PostsFeedAdapter.PostInteractionListener {
    //in order to add frags to the backstack
    public static final String POSTS_FRAG = "posts_frag";
    public static final String COMMENTS_FRAG = "comments_frag";

    private static final String ARG_POSTS = "posts";
    private static final String ARG_LISTENER = "posts_overlay_listener";

    //saving the last post in order to get the club's name, as used in the "add comment" dialog
    private Post mLastPost;
    //to allow the activity to figure out if the today frag is on posts or comments
    private boolean isOnComments = false;

    private Fragment mCurrentComments;
    private PostsOverlayListener mListener;
    private ArrayList<Post> mPosts;

    public static PostOverlayFragment newInstance(ArrayList<Post> posts, PostsOverlayListener listener) {
        PostOverlayFragment fragment = new PostOverlayFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_POSTS, posts);
        args.putSerializable(ARG_LISTENER, listener);
        fragment.setArguments(args);
        return fragment;
    }

    public PostOverlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosts = getArguments().getParcelableArrayList(ARG_POSTS);
            mListener = (PostsOverlayListener)getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_overlay, container, false);

        android.support.v4.app.Fragment postsFragment = PostsCardsFragment.newInstance(mPosts, this);
        //set the listener for the posts feed adapter in order to open the comments feed for a post
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.posts_overlay_container, postsFragment).addToBackStack(POSTS_FRAG).commit();

        return v;
    }

    @Override
    public void pressedPost(Post postPressed) {
        mLastPost = postPressed;

        isOnComments = true;

        //to let the parent frags know that we returned to frags (ex. for today tab to update fab)
        mListener.onCommentsOpened(postPressed);

        //replace the current posts frag with the comments frag, while adding it to a backstack (in case user clicks a commenters profile in which case returning to the comments frag would be required)
        mCurrentComments = PostCommentsFragment.newInstance(postPressed);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.posts_overlay_container, mCurrentComments).addToBackStack(COMMENTS_FRAG).commit();
    }

    public void returnFromComments(){
        isOnComments = false;

        getChildFragmentManager()
                .beginTransaction()
                .remove(mCurrentComments)
                .commit();
        mCurrentComments = null;
        getChildFragmentManager().popBackStack();

        //to let the parent frags know that we returned to frags (ex. for today tab to update fab)
        mListener.onReturnToPosts();
    }

    public Post getmLastPost() {
        return mLastPost;
    }

    public boolean isOnComments() {
        return isOnComments;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (PostsOverlayListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement PostsOverlayListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface PostsOverlayListener extends Serializable {
        public void onCommentsOpened (Post postPressed);
        public void onReturnToPosts();
    }
}