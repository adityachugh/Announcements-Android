package io.mindbend.android.announcements.tabbedFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment implements PostsFeedAdapter.PostInteractionListener, View.OnClickListener {

    //in order to add frags to the backstack
    public static final String TODAY_POSTS_FRAG = "today_posts_frag";
    public static final String COMMENTS_FRAG = "comments_frag";

    //to allow the activity to figure out if the today frag is on posts or comments
    public boolean isOnComments = false;
    private Fragment mCurrentComments;
    private ImageButton mFab;


    public TodayFragment() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        setRetainInstance(true);

        //instantiate the fab so that you can change its onClick method and src logo when switching between posts and comments fragments
        mFab = (ImageButton)v.findViewById(R.id.today_fab);
        mFab.setOnClickListener(this);

        //set the listener for the posts feed adapter in order to open the comments feed for a post
        PostsFeedAdapter.setListener(this);

        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        Fragment postsFragment = PostsCardsFragment.newInstance("test", "test");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.today_framelayout, postsFragment).addToBackStack(TODAY_POSTS_FRAG).commit();

        return v;
    }

    @Override
    public void onClick(View v) {
        if (isOnComments){
            //TODO: dialogue box to add a comment
        }
        else {
            //TODO: dialogue box to change today date
        }
    }

    @Override
    public void pressedPost(Post postPressed) {
        isOnComments = true;

        //set the fab's src/icon to the "add a new comment" image
        mFab.setImageResource(R.drawable.ic_comment);

        //replace the current posts frag with the comments frag, while adding it to a backstack (in case user clicks a commenters profile in which case returning to the comments frag would be required)
        mCurrentComments = PostCommentsFragment.newInstance(postPressed);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.today_framelayout, mCurrentComments).addToBackStack(COMMENTS_FRAG).commit();
    }

    public void returnFromComments(){
        isOnComments = false;

        //set the fab's src/icon back to the "change date" image
        mFab.setImageResource(R.drawable.ic_date);

        getChildFragmentManager()
                .beginTransaction()
                .remove(mCurrentComments)
                .commit();
        mCurrentComments = null;
        getChildFragmentManager().popBackStack();
    }
}
