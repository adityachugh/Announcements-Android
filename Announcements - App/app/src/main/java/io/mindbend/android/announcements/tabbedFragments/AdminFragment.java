package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
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
import io.mindbend.android.announcements.reusableFrags.PostOverlayFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
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
        UserListAdapter.UserListInteractionListener, ListFragment.ListFabListener, SearchableFrag.SearchInterface {
    private static final String ADMIN_ORGS_TAG = "main_admin_frag";
    private static final String TAG = "AdminFragment";
    private static final String ARG_ADMIN_ORGS = "admin_orgs";
    private transient OrgsGridFragment mAdminOrgsFrag;
    private transient AdminMainFragment mAdminMain;
    private ArrayList<Organization> mOrgsList;
    private transient ProgressBar mLoading;

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
            mAdminOrgsFrag = OrgsGridFragment.newInstance(mOrgsList, AdminFragment.this, AdminFragment.this);
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
        if (ft.isEmpty()){
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.admin_framelayout, mAdminOrgsFrag)
                    .addToBackStack(ADMIN_ORGS_TAG)
                    .commitAllowingStateLoss();
        }

        return mView;
    }

    public AdminMainFragment getmAdminMainFrag() {
        return mAdminMain;
    }

    public OrgsGridFragment getmAdminOrgsFrag() {
        return mAdminOrgsFrag;
    }

    @Override
    public void viewChildren(Organization org) {
        /**
         * this method is called when "view schools" is pressed
         */

        //TODO: actually query children org from Parse
        //the following is fake data
        ArrayList<Organization> orgs = new ArrayList<>();

        Organization testOrg1 = new Organization("test Id", "Turner Fenton SS", "The best (IB) school in Peel, 100%", 2000, "#TFSS", false, true); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg1);

        Organization testOrg2 = new Organization("test Id", "Glenforest SS", "The second best IB school in Peel", 1500, "#GFSS", false, false); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg2);

        Organization testOrg3 = new Organization("test Id", "North Park SS", "A great tech school!", 1500, "#NPSS", false, true); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg3);

        OrgsGridFragment childrenOrgs = OrgsGridFragment.newInstance(orgs, this, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, childrenOrgs).addToBackStack(null).commitAllowingStateLoss();
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
        mCurrentOrgModifyFrag = ModifyOrganizationFragment.newInstance(parentOrg, null);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, mCurrentOrgModifyFrag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void userListOpened(final Organization parentOrg) {
        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(), mLoading, parentOrg.getmObjectId(), 0, 50, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {

                ArrayList<User> users = (ArrayList<User>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                ListFragment adminList = ListFragment.newInstance(true, AdminFragment.this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, parentOrg);
                getChildFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.admin_framelayout, adminList)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void viewAnnouncementsState(Organization organization) {
        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        //in the meantime, here is fake data
        ArrayList<Post> posts = new ArrayList<>();

        //THE FOLLOWING ARE FAKE TEST POSTS
        Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data", "Mindbend Studio", "hasImage");
        posts.add(testPost1);

        Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
        posts.add(testPost2);

        Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
        posts.add(testPost3);

        PostsCardsFragment announcementsStateList = PostsCardsFragment.newInstance(posts, null, true, this);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, announcementsStateList)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private boolean isLookingAtAdminOrgs () {
        return mAdminOrgsFrag.isVisible();
    }

    /**
     * The rest of the interfaces (required to have "infinite depth" on the admin tab) are listed below
     *
     */

    @Override
    public void pressedOrg(final Organization orgSelected) {
        if (isLookingAtAdminOrgs()){
            mAdminMain = AdminMainFragment.newInstance(orgSelected, AdminFragment.this);
            getChildFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.admin_framelayout, mAdminMain)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            Log.wtf(TAG, "PARSE USER " + ParseUser.getCurrentUser().getObjectId());
            AdminDataSource.checkIfUserIsAdminOfOrganization(mLoading, getActivity(), orgSelected.getmObjectId(), ParseUser.getCurrentUser().getObjectId(), new FunctionCallback<Boolean>() {
                @Override
                public void done(final Boolean isAdmin, ParseException e) {
                    if (e == null) {
                        Log.wtf(TAG, "IS USER ADMIN? " + isAdmin);
                        //replace the current profile frag with new org profile frag, while adding it to a backstack
                        OrgsDataSource.isFollowingOrganization(mView, mLoading, ParseUser.getCurrentUser().getObjectId(), orgSelected.getmObjectId(), new FunctionCallback<String>() {
                            @Override
                            public void done(String followState, ParseException e) {
                                ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, followState, AdminFragment.this, isAdmin, onToday, onDiscover, onYou, onAdmin);
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, orgProfile).addToBackStack(null).commitAllowingStateLoss();
                            }
                        });
                        Log.d(TAG, "org has been pressed on admin page " + orgSelected.toString());
                    } else {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
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
        ProfileFragment userProfile = ProfileFragment.newInstance(userPressed, null, null,  this, false, onToday, onDiscover, onYou, onAdmin);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, userProfile).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void modifyOrg(Organization org) {
        ((TabbedActivity)getActivity()).getmViewPager().setCurrentItem(3);
        mCurrentOrgModifyFrag = ModifyOrganizationFragment.newInstance(null, org);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, mCurrentOrgModifyFrag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void viewMembers(final Organization org, final boolean isAdmin) {
        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(), mLoading, org.getmObjectId(), 0, 50, new FunctionCallback<HashMap<Boolean, Object>>() {
            @Override
            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {

                ArrayList<User> users = (ArrayList<User>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                ListFragment adminList = ListFragment.newInstance(isAdmin, AdminFragment.this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, org);
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
    public void searchForAdmins(Organization organization) {
        //TODO: open searchfrag here
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, AdminFragment.this);
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
    public void refreshPosts() {

    }

    @Override
    public void openOrgProfileFromPosts(Organization organization) {
        pressedOrg(organization);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {

    }
}
