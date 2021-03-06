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
import io.mindbend.android.announcements.cloudCode.ErrorCodeMessageDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.reusableFrags.ListFragment;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.reusableFrags.SearchableFrag;
import io.mindbend.android.announcements.reusableFrags.UserListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment implements Serializable, PostsFeedAdapter.PostInteractionListener, ProfileFragment.ProfileInteractionListener, SearchableFrag.SearchInterface, UserListAdapter.UserListInteractionListener, ListFragment.ListFabListener, PostOverlayFragment.PostsOverlayListener, OrgsGridAdapter.OrgInteractionListener, OrgsGridFragment.OrgsGridInteractionListener {


    private static final String TAG = "TAG";
    private static final String ORG_PROFILE_FRAG = "ORG_PROFILE_FRAGMENT";
    private transient SearchableFrag mOrgsGridFrag;
    private transient ProgressBar mLoading;

    //pass into profilefrag new instance in this order!
    private boolean onToday = false;
    private boolean onDiscover = true;
    private boolean onYou = false;
    private boolean onAdmin = false;
    private transient View mView;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_discover, container, false);
        setRetainInstance(true);

        mLoading = (ProgressBar) mView.findViewById(R.id.discover_frag_progressbar);

        mOrgsGridFrag = SearchableFrag.newInstance(SearchableFrag.ORGS_TYPE, null, this, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, mOrgsGridFrag).commitAllowingStateLoss();
        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mLoading = (ProgressBar) mView.findViewById(R.id.discover_frag_progressbar);
            mOrgsGridFrag = SearchableFrag.newInstance(SearchableFrag.ORGS_TYPE, null, this, false);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.discover_framelayout, mOrgsGridFrag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        }
    }

    public SearchableFrag getmOrgsGridFrag() {
        return mOrgsGridFrag;
    }


    @Override
    public void pressedPostComments(Post postPressed) {
        //do stuff, although switching to comments frag is already handled
        Log.d(TAG, "post pressed");
    }

    @Override
    public void pressedPostCard(Post post) {

    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
        pressedOrgFromProfile(orgSelected);
    }

    @Override
    public void pressedOrgFromProfile(final Organization orgPressed) {
        Log.wtf(TAG, "PARSE USER " + ParseUser.getCurrentUser().getObjectId());
        //replace the current profile frag with new org profile frag, while adding it to a backstack
        OrgsDataSource.isFollowingOrganization(R.id.discover_framelayout, getActivity(), mView, mLoading, ParseUser.getCurrentUser().getObjectId(), orgPressed.getmObjectId(), new FunctionCallback<String>() {
            @Override
            public void done(String followState, ParseException e) {
//
                ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgPressed, followState, DiscoverFragment.this, followState.equals(UserDataSource.FOLLOWER_ADMIN), onToday, onDiscover, onYou, onAdmin);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, orgProfile).addToBackStack(null).commitAllowingStateLoss();
//
            }
        });
        Log.d(TAG, "org has been pressed on discover page " + orgPressed.toString());

    }


    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userToVisit = ProfileFragment.newInstance(userPressed, null, null, this, false, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, userToVisit).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void modifyOrg(Organization org) {
        ((TabbedActivity) getActivity()).getmAdminFragment().modifyOrg(org);
    }

    @Override
    public void viewMembers(final Organization org, final boolean isAdmin) {
        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(), mLoading, R.id.discover_framelayout,org.getmObjectId(), 0, 50, isAdmin, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {

                ArrayList<User> users = (ArrayList<User>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                ListFragment adminList = ListFragment.newInstance(isAdmin, DiscoverFragment.this, false, null, null, null, null, users, DiscoverFragment.this, typeOfUsers, org, null, false, false);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.discover_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void viewAnnouncementsState(Organization org) {
        AdminDataSource.getAllPostsForOrganizationForRange(mView, mLoading, R.id.discover_framelayout,getActivity(), org.getmObjectId(), 0, 10, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> posts, ParseException e) {
                if (e == null) {
                    PostsCardsFragment allPosts = PostsCardsFragment.newInstance(posts, DiscoverFragment.this, true, DiscoverFragment.this, false, null, null);
                    getChildFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.discover_framelayout, allPosts)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    Snackbar.make(mView, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void viewChildOrgs(ArrayList<Organization> orgs) {
        OrgsGridFragment childrenOrgs = OrgsGridFragment.newInstance(orgs, DiscoverFragment.this, DiscoverFragment.this, null, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, childrenOrgs).addToBackStack(null).commitAllowingStateLoss();
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
                    //TODO: error handling for adding an existing admin
                    Snackbar.make(mView, getActivity().getString(R.string.format_added_user_as_admin_success_message, user.getName()), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void searchForAdmins(Organization organization) {
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, DiscoverFragment.this, true);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, searchableFrag).addToBackStack(null).commitAllowingStateLoss();
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
    public void refreshPosts(boolean isApproving, boolean isViewingState) {

    }

    @Override
    public void openOrgProfileFromPosts(Organization organization) {
        pressedOrgFromProfile(organization);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {

    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        openOrgProfileFromPosts(orgSelected);
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        openOrgProfileFromPosts(orgPressed);
    }
}
