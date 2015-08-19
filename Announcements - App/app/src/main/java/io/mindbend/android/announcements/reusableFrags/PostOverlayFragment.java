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
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;


public class PostOverlayFragment extends Fragment implements Serializable, PostsFeedAdapter.PostInteractionListener, PostCommentsFragment.CommentsInteractionListener, PostCardFullFragment.FullPostInteractionListener {
    //in order to add frags to the backstack
    public static final String POSTS_FRAG = "posts_frag";
    public static final String COMMENTS_FRAG = "comments_frag";
    public static final String FULL_POST_FRAG = "full_post_frag";

    private static final String ARG_POSTS = "posts";
    private static final String ARG_LISTENER = "posts_overlay_listener";

    //saving the last post in order to get the club's name, as used in the "add comment" dialog
    private Post mLastPost;
    //to allow the activity to figure out if the today frag is on posts or comments
    private boolean isOnComments = false;

    private Fragment mCurrentComments;
    private Fragment mFullPost;
    private PostsOverlayListener mListener;
    private ArrayList<Post> mPosts;
    private transient View mView;
    private View mTodayView;

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
       if (mView == null){
           // Inflate the layout for this fragment
           mView = inflater.inflate(R.layout.fragment_post_overlay, container, false);

           android.support.v4.app.Fragment postsFragment = PostsCardsFragment.newInstance(mPosts, this);
           //set the listener for the posts feed adapter in order to open the comments feed for a post
           FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
           if (transaction.isEmpty())
               transaction.add(R.id.posts_overlay_container, postsFragment).addToBackStack(POSTS_FRAG).commit();
       }
        return mView;
    }

    @Override
    public void pressedPost(Post postPressed) {
        mLastPost = postPressed;

        isOnComments = true;

        //to let the parent frags know that we returned to frags (ex. for today tab to update fab)
        mListener.onCommentsOpened(postPressed);

        //replace the current posts frag with the comments frag, while adding it to a backstack (in case user clicks a commenters profile in which case returning to the comments frag would be required)
        mCurrentComments = PostCommentsFragment.newInstance(postPressed, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.posts_overlay_container, mCurrentComments).addToBackStack(COMMENTS_FRAG).commit();
    }

    @Override
    public void pressedPostCard(Post post) {
        mLastPost = post;

        //to let the parent frags know that we returned to frags (ex. for today tab to update fab)
        mListener.onCommentsOpened(post);

        mFullPost = PostCardFullFragment.newInstance(post, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.posts_overlay_container, mFullPost).addToBackStack(FULL_POST_FRAG).commit();
    }

    @Override
    public void pressedBackToPosts() {
        returnFromComments();
    }

    @Override
    public void pressedCommenterProfile(User commenterPressed) {
        mListener.visitCommentersProfile(commenterPressed);
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

    @Override
    public void CommentButtonClicked(Post postComments) {
        pressedPost(postComments);
    }


    public Post getmLastPost() {
        return mLastPost;
    }

    public boolean getIsOnComments() {
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
        void onCommentsOpened (Post postPressed);
        void onReturnToPosts();
        void visitCommentersProfile(User commenterToBeVisited);
    }
}