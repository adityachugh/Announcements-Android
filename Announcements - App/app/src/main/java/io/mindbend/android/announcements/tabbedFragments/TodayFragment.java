package io.mindbend.android.announcements.tabbedFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment implements PostsFeedAdapter.PostInteractionListener {


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

        //set the listener for the posts feed adapter in order to open the comments feed for a post
        PostsFeedAdapter.setListener(this);

        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        Fragment postsFragment = PostsCardsFragment.newInstance("test", "test");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.today_framelayout, postsFragment).commit();
        return v;
    }

    @Override
    public void pressedPost(Post postPressed) {
        Fragment postComments = PostCommentsFragment.newInstance(postPressed);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.today_framelayout, postComments).commit();
    }
}
