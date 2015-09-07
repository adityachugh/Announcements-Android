package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.reusableFrags.ListFragment;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.reusableFrags.SearchableFrag;
import io.mindbend.android.announcements.reusableFrags.UserListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class YouFragment extends Fragment implements Serializable, ProfileFragment.ProfileInteractionListener, SearchableFrag.SearchInterface, ListFragment.ListFabListener, UserListAdapter.UserListInteractionListener, PostOverlayFragment.PostsOverlayListener {
    private static final String TAG = "TAG";
    private static final String DEFAULT = "default_frag";
    private ProfileFragment mProfileFragment;

    private boolean onToday = false;
    private boolean onDiscover = false;
    private boolean onYou = true;
    private boolean onAdmin = false;
    public transient ProgressBar mLoading;
    private transient View mView;

    //NOTE: Opens child ProfileFragment, which has a grandchild for user followed organizations/ organization announcements
    //Makes Profile fragment generic (useable by both users and organizations)


    public YouFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        mView = inflater.inflate(R.layout.fragment_you, container, false);
        mLoading = (ProgressBar) mView.findViewById(R.id.you_frag_progressbar);

        UserDataSource.getCurrentUserWithInfo(mLoading, new FunctionCallback<User>() {
            @Override
            public void done(User user, ParseException e) {
                mProfileFragment = ProfileFragment.newInstance(user, null, null, YouFragment.this, true, onToday, onDiscover, onYou, onAdmin);
                //inflate profileFrag in framelayout
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.you_framelayout, mProfileFragment).addToBackStack(DEFAULT).commitAllowingStateLoss();
            }
        });

        return mView;
    }

    public Fragment getmProfileFragment() {
        return mProfileFragment;
    }

    @Override
    public void userProfileToOrgProfile(final Organization orgSelected) {
        Log.wtf(TAG, "PARSE USER " + ParseUser.getCurrentUser().getObjectId());
        //replace the current profile frag with new org profile frag, while adding it to a backstack
        OrgsDataSource.isFollowingOrganization(mView, mLoading, ParseUser.getCurrentUser().getObjectId(), orgSelected.getmObjectId(), new FunctionCallback<String>() {
            @Override
            public void done(String retrievedFollowState, ParseException e) {
                if (e == null) {
                    ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, retrievedFollowState, YouFragment.this, retrievedFollowState.equals(UserDataSource.FOLLOWER_ADMIN), onToday, onDiscover, onYou, onAdmin);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.you_framelayout, orgProfile).addToBackStack(null).commitAllowingStateLoss();
                }
            }
        });
        Log.d(TAG, "org has been pressed on profile page " + orgSelected.toString());
    }


    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        userProfileToOrgProfile(orgPressed);
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userToVisit = ProfileFragment.newInstance(userPressed, null, null, this, false, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.you_framelayout, userToVisit).addToBackStack(null).commitAllowingStateLoss();
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

                ListFragment adminList = ListFragment.newInstance(isAdmin, YouFragment.this, false, null, null, null, null, users, YouFragment.this, typeOfUsers, org);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.you_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void viewAnnouncementsState(Organization org) {
        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        //in the meantime, here is fake data
        ArrayList<Post> posts = new ArrayList<>();

        //THE FOLLOWING ARE FAKE TEST POSTS
        Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data", "Mindbend Studio", "hasImage");
        posts.add(testPost1);

        Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
        posts.add(testPost2);

        Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio", "hasImage");
        posts.add(testPost3);

        PostsCardsFragment announcementsStateList = PostsCardsFragment.newInstance(posts, null, true, this);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.you_framelayout, announcementsStateList)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void userSelected(User userPressed) {
        pressedUserFromCommentOfOrgPost(userPressed);
    }

    @Override
    public void selectedUserToBeAdmin(final User user, Organization nullableOrg) {
        //add user to existing org
        AdminDataSource.addAdminToOrganization(mView, mLoading, nullableOrg.getmObjectId(), user.getmObjectId(), new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                if (success && e == null) {
                    //TODO: error handling for adding an existing admin
                    Snackbar.make(mView, getActivity().getString(R.string.format_added_user_as_admin_success_message, user.getName()), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void searchForAdmins(Organization organization) {
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, YouFragment.this, true);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.you_framelayout, searchableFrag).addToBackStack(null).commitAllowingStateLoss();
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
    public void fullPostProfile(Post clickedPost) {

    }

    @Override
    public void onCommentsOpened(Post postPressed) {

    }

    @Override
    public void onReturnToPosts() {

    }

    @Override
    public void profileComments(Post post) {

    }

    @Override
    public void refreshPosts() {

    }

    @Override
    public void openOrgProfileFromPosts(Organization organization) {
        userProfileToOrgProfile(organization);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {

    }
}
