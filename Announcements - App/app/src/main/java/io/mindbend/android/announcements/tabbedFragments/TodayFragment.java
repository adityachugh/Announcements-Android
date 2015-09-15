package io.mindbend.android.announcements.tabbedFragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.PostsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.reusableFrags.ListFragment;
import io.mindbend.android.announcements.reusableFrags.PostCardFullFragment;
import io.mindbend.android.announcements.reusableFrags.PostCommentsFragment;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.reusableFrags.SearchableFrag;
import io.mindbend.android.announcements.reusableFrags.UserListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment implements Serializable,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        PostOverlayFragment.PostsOverlayListener,
        ProfileFragment.ProfileInteractionListener, ListFragment.ListFabListener, UserListAdapter.UserListInteractionListener, SearchableFrag.SearchInterface, PostsFeedAdapter.PostInteractionListener{

    private static final String TAG = "TodayFragment";

    private transient ImageButton mFab;
    public transient ProgressBar mLoading;
    //in order to add frags to the backstack
    public static final String TODAY_POSTS_FRAG = "today_posts_frag";
    private transient PostOverlayFragment mPostsOverlayFragment;

    //pass into profilefrag new instance in this order!
    private boolean onToday = true;
    private boolean onDiscover = false;
    private boolean onYou = false;
    private boolean onAdmin = false;

    public Date mCurrentDateSelected;
    private transient View mView;

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
        mView = inflater.inflate(R.layout.fragment_today, container, false);
        setRetainInstance(true);

        //instantiate the fab so that you can change its onClick method and src logo when switching between posts and comments fragments
        mFab = (ImageButton) mView.findViewById(R.id.today_fab);
        mFab.setOnClickListener(this);

        mLoading = (ProgressBar) mView.findViewById(R.id.today_progressbar);

        mCurrentDateSelected = new Date();

        loadPosts(0, 10);

        return mView;
    }

    private void loadPosts(int startIndex, int numberOfPosts) {
        PostsDataSource.getRangeOfPostsForDay(mView, mLoading, getActivity(), startIndex, numberOfPosts, mCurrentDateSelected, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> posts, ParseException e) {
                if (e == null) {
                    //pass in "this" in order to set the listener for the posts overlay frag in order to open the comments feed for a post
                    mPostsOverlayFragment = PostOverlayFragment.newInstance(posts, TodayFragment.this, false);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.today_framelayout, mPostsOverlayFragment).addToBackStack(TODAY_POSTS_FRAG).commitAllowingStateLoss();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public PostOverlayFragment getmPostsOverlayFragment() {
        return mPostsOverlayFragment;
    }

    @Override
    public void onClick(View v) {
        // dialogue box to change today date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDateSelected); //new Date gets the current date and time

        //instantiate the date picker dialog and implement the onDateSet method (it is implemented by the today frag)
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCurrentDateSelected = new Date(year - 1900, monthOfYear, dayOfMonth);
        loadPosts(0, 10);
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
        ProfileFragment commenterVisited = ProfileFragment.newInstance(commenterToBeVisited, null, null, this, false, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.today_framelayout, commenterVisited).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void fullPostProfile(Post clickedPost) {
        mFab.setVisibility(View.GONE);
    }

    @Override
    public void userProfileToOrgProfile(final Organization orgSelected) {
        openOrgProfileFromPosts(orgSelected);
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        userProfileToOrgProfile(orgPressed);
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        visitCommentersProfile(userPressed);
    }

    @Override
    public void profileComments(Post post) {

    }

    @Override
    public void refreshPosts(boolean isApproving, boolean isViewingState) {
        Log.wtf(TAG, "TODAYFRAG refreshed!");

        //refreshing will load the latest 10 posts
        loadPosts(0, 10);
    }

    @Override
    public void openOrgProfileFromPosts(final Organization organization) {
        OrgsDataSource.isFollowingOrganization(getActivity(), mView, mLoading, ParseUser.getCurrentUser().getObjectId(), organization.getmObjectId(), new FunctionCallback<String>() {
            @Override
            public void done(String followState, ParseException e) {
                if (e == null) {
                    ProfileFragment orgProfile = ProfileFragment.newInstance(null, organization, followState, TodayFragment.this, followState.equals(UserDataSource.FOLLOWER_ADMIN), onToday, onDiscover, onYou, onAdmin);
                    getChildFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.today_framelayout, orgProfile)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });


    }

    @Override
    public void modifyOrg(Organization org) {
        ((TabbedActivity) getActivity()).getmAdminFragment().modifyOrg(org);
    }

    @Override
    public void viewMembers(final Organization org, final boolean isAdmin) {
        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(), mLoading, org.getmObjectId(), 0, 50, isAdmin, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {

                ArrayList<User> users = (ArrayList<User>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                ListFragment adminList = ListFragment.newInstance(isAdmin, TodayFragment.this, false, null, null, null, null, users, TodayFragment.this, typeOfUsers, org, null);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.today_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void viewAnnouncementsState(Organization org) {
        AdminDataSource.getAllPostsForOrganizationForRange(mView, mLoading, getActivity(), org.getmObjectId(), 0, 10, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> posts, ParseException e) {
                if (e == null) {
                    PostsCardsFragment allPosts = PostsCardsFragment.newInstance(posts, TodayFragment.this, true, TodayFragment.this, false, null, null);
                    getChildFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.today_framelayout, allPosts)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void userSelected(User userPressed) {
        pressedUserFromCommentOfOrgPost(userPressed);
    }

    @Override
    public void selectedUserToBeAdmin(final User user, Organization nullableOrg) {
        //add user to existing org
        AdminDataSource.addAdminToOrganization(getActivity(), mView, mLoading, nullableOrg.getmObjectId(), user.getmObjectId(), new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                if (success && e == null) {
                    Snackbar.make(mView, getActivity().getString(R.string.format_added_user_as_admin_success_message, user.getName()), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void searchForAdmins(Organization organization) {
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, TodayFragment.this, true);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.today_framelayout, searchableFrag).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void searchUserPressed(User userPressed) {
        pressedUserFromCommentOfOrgPost(userPressed);
    }

    @Override
    public void searchOrgPressed(Organization orgPressed) {
        pressedOrgFromProfile(orgPressed);
    }

    @Override
    public void pressedPostComments(Post postPressed) {

    }

    @Override
    public void pressedPostCard(Post post) {

    }
}
