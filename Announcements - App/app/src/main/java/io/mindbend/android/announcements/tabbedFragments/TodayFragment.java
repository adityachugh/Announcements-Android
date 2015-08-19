package io.mindbend.android.announcements.tabbedFragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment implements Serializable, View.OnClickListener, DatePickerDialog.OnDateSetListener, PostOverlayFragment.PostsOverlayListener, ProfileFragment.ProfileInteractionListener {
    private transient ImageButton mFab;
    //in order to add frags to the backstack
    public static final String TODAY_POSTS_FRAG = "today_posts_frag";
    private PostOverlayFragment mPostsOverlayFragment;

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
        mFab = (ImageButton) v.findViewById(R.id.today_fab);
        mFab.setOnClickListener(this);

        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        //in the meantime, here is fake data
        ArrayList<Post> posts = new ArrayList<>();

        //THE FOLLOWING ARE FAKE TEST POSTS
        Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data. Lots and lots of fake data. Yes. I love fake data. This needs to be long as hell. Yum yum yum yum yum yum yum yum yum!!!!", "Mindbend Studio", "");
        posts.add(testPost1);

        Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio", "yes");
        posts.add(testPost2);

        Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
        posts.add(testPost3);

        //pass in "this" in order to set the listener for the posts overlay frag in order to open the comments feed for a post
        mPostsOverlayFragment = PostOverlayFragment.newInstance(posts, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.today_framelayout, mPostsOverlayFragment).addToBackStack(TODAY_POSTS_FRAG).commit();

        return v;
    }

    public PostOverlayFragment getmPostsOverlayFragment() {
        return mPostsOverlayFragment;
    }

    @Override
    public void onClick(View v) {
        //TODO: dialogue box to change today date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); //new Date gets the current date and time

        //instantiate the date picker dialog and implement the onDateSet method (it is implemented by the today frag)
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //TODO: reload the posts fragment with the new date.
    }

    @Override
    public void onCommentsOpened(Post postPressed) {
        //remove the date fab so that the comments fab can be seen
        mFab.setVisibility(View.GONE);

    }

    @Override
    public void onReturnToPosts() {
        //bring back the date fab
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {
        ProfileFragment commenterVisited = ProfileFragment.newInstance(commenterToBeVisited, null, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.today_framelayout, commenterVisited).addToBackStack(null).commit();
    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
        ProfileFragment orgToVisit = ProfileFragment.newInstance(null, orgSelected, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.today_framelayout, orgToVisit).addToBackStack(null).commit();
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        userProfileToOrgProfile(orgPressed);
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        visitCommentersProfile(userPressed);
    }
}
