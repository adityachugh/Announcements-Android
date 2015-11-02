package io.mindbend.android.announcements.tabbedFragments;


import android.content.Intent;
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
import io.mindbend.android.announcements.adminClasses.AdminMainFragment;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.reusableFrags.ListFragment;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.PostCardFullFragment;
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.reusableFrags.SearchableFrag;
import io.mindbend.android.announcements.reusableFrags.UserListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment implements Serializable,
        AdminMainFragment.AdminInteractionListener,
        OrgsGridAdapter.OrgInteractionListener,
        OrgsGridFragment.OrgsGridInteractionListener,
        ProfileFragment.ProfileInteractionListener,
        PostOverlayFragment.PostsOverlayListener,
        PostsFeedAdapter.PostInteractionListener,
        UserListAdapter.UserListInteractionListener, ListFragment.ListFabListener, SearchableFrag.SearchInterface, ModifyOrganizationFragment.ModifyOrgInterface, PostCardFullFragment.FullPostInteractionListener {

    public static final String ADMIN_ORGS_TAG = "main_admin_frag";
    public static final String PENDING_POSTS = "pending_posts";
    public static final String ALL_ORG_POSTS = "all_org_posts";
    public static final String FULL_POST = "full_post";

    private static final String TAG = "AdminFragment";
    private static final String ARG_ADMIN_ORGS = "admin_orgs";
    private static final String ARG_ADMING_ORG_FRAG = "admin_orgs_bundle";

    private transient OrgsGridFragment mAdminOrgsFrag;
    private transient AdminMainFragment mAdminMain;
    private ArrayList<Organization> mOrgsList;
    public transient ProgressBar mLoading;
    private PostOverlayFragment.PostsOverlayListener mPostsOverlayListener = this;
    private PostsFeedAdapter.PostInteractionListener mPostInteractionListener = this;
    private PostCardFullFragment.FullPostInteractionListener mFullPostInteractionListener = this;
    private Organization mParentOrg;
    private Organization mViewingOrg;

    private boolean onToday = false;
    private boolean onDiscover = false;
    private boolean onYou = false;
    private boolean onAdmin = true;
    private transient View mView;

    private transient Fragment mCurrentOrgModifyFrag;

    public Fragment getmCurrentOrgModifyFrag() {
        return mCurrentOrgModifyFrag;
    }

    public void setmCurrentOrgModifyFrag(Fragment mCurrentOrgModifyFrag) {
        this.mCurrentOrgModifyFrag = mCurrentOrgModifyFrag;
    }

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance(ArrayList<Organization> adminOrgList) {
        AdminFragment fragment = new AdminFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ADMIN_ORGS, adminOrgList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrgsList = getArguments().getParcelableArrayList(ARG_ADMIN_ORGS);
            mAdminOrgsFrag = OrgsGridFragment.newInstance(mOrgsList, AdminFragment.this, AdminFragment.this, null, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_admin, container, false);
        setRetainInstance(true);

        mLoading = (ProgressBar) mView.findViewById(R.id.admin_frag_progressbar);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (ft.isEmpty()) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.admin_framelayout, mAdminOrgsFrag)
                    .addToBackStack(ADMIN_ORGS_TAG)
                    .commitAllowingStateLoss();
        }

        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(ARG_ADMIN_ORGS, mOrgsList);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null){
            if (savedInstanceState.getParcelableArrayList(ARG_ADMIN_ORGS) != null){
                mOrgsList = savedInstanceState.getParcelableArrayList(ARG_ADMIN_ORGS);
                mAdminOrgsFrag = OrgsGridFragment.newInstance(mOrgsList, AdminFragment.this, AdminFragment.this, null, false);
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.admin_framelayout, mAdminOrgsFrag)
                        .addToBackStack(ADMIN_ORGS_TAG)
                        .commitAllowingStateLoss();
            }

            mPostsOverlayListener = this;
            mPostInteractionListener = this;
            mFullPostInteractionListener = this;

            onToday = false;
            onDiscover = false;
            onYou = false;
            onAdmin = true;

            mLoading = (ProgressBar) mView.findViewById(R.id.admin_frag_progressbar);
        }
    }

    public AdminMainFragment getmAdminMainFrag() {
        return mAdminMain;
    }

    public OrgsGridFragment getmAdminOrgsFrag() {
        return mAdminOrgsFrag;
    }

    public void updateModifiedAdminOrg(Organization updatedOrg){
        Log.wtf("AdminFrag", "Org updated");
        mAdminOrgsFrag.updateSingleOrg(updatedOrg);
    }

    @Override
    public void viewChildren(Organization org) {
        /**
         * this method is called when "view schools" is pressed
         */

        OrgsDataSource.getAllChildOrganizations(mView, getActivity(), mLoading, R.id.admin_framelayout, org.getmObjectId(), new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> organizations, ParseException e) {
                if (e == null) {
                    OrgsGridFragment childrenOrgs = OrgsGridFragment.newInstance(organizations, AdminFragment.this, AdminFragment.this, null, false);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, childrenOrgs).addToBackStack(null).commitAllowingStateLoss();
                }
            }
        });
    }

    @Override
    public void addAnnouncement(Organization organization) {
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, mAdminMain.getmNewAnnouncementFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void addChildOrganization(Organization parentOrg) {
        mCurrentOrgModifyFrag = ModifyOrganizationFragment.newInstance(parentOrg, null, AdminFragment.this);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, mCurrentOrgModifyFrag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void getChildPendingPosts(Organization parentOrg) {
        mParentOrg = parentOrg; //private field needed for refresh
        loadPendingPosts(parentOrg.getmObjectId());
    }

    private void loadPendingPosts(final String parentId){
        AdminDataSource.getPostsToBeApprovedInRange(mView, mLoading, R.id.admin_framelayout, getActivity(), parentId, 0, 10, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> posts, ParseException e) {
                if (e == null) {
                    PostsCardsFragment pendingPosts = PostsCardsFragment.newInstance(posts, mPostInteractionListener, false, mPostsOverlayListener, true, parentId, null); //true; is approving
                    getChildFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.admin_framelayout, pendingPosts)
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
    public void viewAdmins(final Organization parentOrg) {
        OrgsDataSource.getAdminsForOrganizationInRange(mView, getActivity(), mLoading, R.id.admin_framelayout, parentOrg.getmObjectId(), 0, 20, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {
                ArrayList<User> users = (ArrayList<User>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);
                if (users.size() > 0 && typeOfUsers.size() > 0){
                    Log.wtf("First admin:", users.get(0).getName());
                    Log.wtf("Follower type (should be 1)", ""+typeOfUsers.get(users.get(0)));
                }

                ListFragment adminList = ListFragment.newInstance(true, AdminFragment.this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, parentOrg, null, true, false);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.admin_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }


    @Override
    public void viewFollowers(final Organization organization) {
        //is admin is false so pending followers dont show up in this list
        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(), mLoading, R.id.admin_framelayout, organization.getmObjectId(), 0, 20, false, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {

                ArrayList<User> users = (ArrayList<User>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                ListFragment adminList = ListFragment.newInstance(true, AdminFragment.this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, organization, null, false, false);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.admin_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void viewPendingFollowers(final Organization organization) {
        OrgsDataSource.getRequestedPendingPrivateOrganizationUsers(mView, getActivity(), mLoading, R.id.admin_framelayout, organization.getmObjectId(), 0, 20, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {
                if (e == null) {
                    ArrayList<User> users = (ArrayList<User>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                    HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                    ListFragment pendingFollowers = ListFragment.newInstance(true, AdminFragment.this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, organization, null, false, true);
                    getChildFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.admin_framelayout, pendingFollowers)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });
    }

    @Override
    public void viewAnnouncementsState(Organization organization) {
        mViewingOrg = organization; //private field needed for refresh
        allOrgPosts(organization.getmObjectId());
    }

    @Override
    public void viewChildOrgs(ArrayList<Organization> orgs) {
        OrgsGridFragment childrenOrgs = OrgsGridFragment.newInstance(orgs, AdminFragment.this, AdminFragment.this, null, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, childrenOrgs).addToBackStack(null).commitAllowingStateLoss();
    }

    private void allOrgPosts(final String orgId){
        AdminDataSource.getAllPostsForOrganizationForRange(mView, mLoading, R.id.admin_framelayout,getActivity(), orgId, 0, 10, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> posts, ParseException e) {
                if (e == null) {
                    PostsCardsFragment allPosts = PostsCardsFragment.newInstance(posts, mPostInteractionListener, true, mPostsOverlayListener, false, null, orgId);
                    getChildFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.admin_framelayout, allPosts)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }

    private boolean isLookingAtAdminOrgs() {
        return mAdminOrgsFrag.isVisible();
    }

    /**
     * The rest of the interfaces (required to have "infinite depth" on the admin tab) are listed below
     */

    @Override
    public void pressedOrg(final Organization orgSelected) {
        if (isLookingAtAdminOrgs()) {
            mAdminMain = AdminMainFragment.newInstance(orgSelected, AdminFragment.this);
            getChildFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.admin_framelayout, mAdminMain)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            Log.wtf(TAG, "PARSE USER " + ParseUser.getCurrentUser().getObjectId());
            //replace the current profile frag with new org profile frag, while adding it to a backstack
            OrgsDataSource.isFollowingOrganization(R.id.admin_framelayout,getActivity(), mView, mLoading, ParseUser.getCurrentUser().getObjectId(), orgSelected.getmObjectId(), new FunctionCallback<String>() {
                @Override
                public void done(String followState, ParseException e) {
                    ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, followState, AdminFragment.this, followState.equals(UserDataSource.FOLLOWER_ADMIN), onToday, onDiscover, onYou, onAdmin);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, orgProfile).addToBackStack(null).commitAllowingStateLoss();
                }
            });
            Log.d(TAG, "org has been pressed on admin page " + orgSelected.toString());
        }
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        pressedOrg(orgPressed);
    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
        pressedOrg(orgSelected);
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        pressedOrg(orgPressed);
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userProfile = ProfileFragment.newInstance(userPressed, null, null, this, false, onToday, onDiscover, onYou, onAdmin);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, userProfile).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void modifyOrg(Organization org) {
        ((TabbedActivity) getActivity()).getmViewPager().setCurrentItem(3);
        mCurrentOrgModifyFrag = ModifyOrganizationFragment.newInstance(null, org, AdminFragment.this);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, mCurrentOrgModifyFrag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void viewMembers(final Organization org, final boolean isAdmin) {
        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(), mLoading, R.id.admin_framelayout,org.getmObjectId(), 0, 20, isAdmin, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {

                ArrayList<User> users = (ArrayList<User>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>) booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                ListFragment adminList = ListFragment.newInstance(isAdmin, AdminFragment.this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, org, null, false, false);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.admin_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void userSelected(User user) {
        pressedUserFromCommentOfOrgPost(user);
    }

    @Override
    public void selectedUserToBeAdmin(final User user, Organization nullableOrg) {
        //check if a user is being added to an existing org or is being saved to be an admin for a NEW org
        if (mCurrentOrgModifyFrag != null && !mCurrentOrgModifyFrag.isHidden()) {
            Log.wtf("test", "attempt to add " + user.getName() + " as initial admin");
            //save user for setting as admin for new org
            ((ModifyOrganizationFragment) mCurrentOrgModifyFrag).setInitialAdmin(user);
            getChildFragmentManager().popBackStack();
        } else {
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
    }

    @Override
    public void searchForAdmins(Organization organization) {
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, AdminFragment.this, true);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, searchableFrag).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void searchUserPressed(User userPressed) {
        pressedUserFromCommentOfOrgPost(userPressed);
    }

    @Override
    public void searchOrgPressed(Organization orgPressed) {
        pressedOrg(orgPressed);
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
        //refresh when approving posts; load the 10 latest posts
        getChildFragmentManager().popBackStack();
        if (isApproving)
            loadPendingPosts(mParentOrg.getmObjectId());
        else if (isViewingState)
            allOrgPosts(mViewingOrg.getmObjectId());
    }

    @Override
    public void openOrgProfileFromPosts(Organization organization) {
        pressedOrg(organization);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {

    }

    @Override
    public void pressedPostCard(Post post) {
        //In this case, open full post
        PostCardFullFragment fullCard = PostCardFullFragment.newInstance(post, mFullPostInteractionListener, true);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, fullCard).addToBackStack(FULL_POST).commitAllowingStateLoss();

    }

    @Override
    public void pressedPostComments(Post postPressed) {

    }

    @Override
    public void CommentButtonClicked(Post postComments) {

    }

    @Override
    public void openPosterOrgProfile(Organization organization) {

    }
}
