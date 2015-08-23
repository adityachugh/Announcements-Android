package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.adminClasses.AdminMainFragment;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.adminClasses.NewAnnouncementFragment;
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
    private static final String MAIN_ADMIN_TAG = "main_admin_frag";
    private transient AdminMainFragment mAdminMain;

    private boolean onToday = false;
    private boolean onDiscover = false;
    private boolean onYou = false;
    private boolean onAdmin = true;

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin, container, false);
        setRetainInstance(true);

        mAdminMain = AdminMainFragment.newInstance(this);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (ft.isEmpty()) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.admin_framelayout, mAdminMain, MAIN_ADMIN_TAG).addToBackStack(MAIN_ADMIN_TAG).commit();
        }
        return v;
    }

    public AdminMainFragment getmAdminMain() {
        return mAdminMain;
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
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, childrenOrgs).addToBackStack(null).commit();
    }

    @Override
    public void addAnnouncement(Organization organization) {
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, mAdminMain.getmNewAnnouncementFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void addChildOrganization(Organization parentOrg) {
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, ModifyOrganizationFragment.newInstance(parentOrg, null))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void userListOpened(Organization parentOrg) {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new User("Tech", "Retreater", "all things Waterloo", "CS", "Admin", 10));
        users.add(new User("Tech", "Retreater", "all things Waterloo", "CS", "Admin", 10));
        users.add(new User("Tech", "Retreater", "all things Waterloo", "CS", "Admin", 10));
        users.add(new User("Tech", "Retreater", "all things Waterloo", "CS", "Admin", 10));
        users.add(new User("Tech", "Retreater", "all things Waterloo", "CS", "Admin", 10));

        //for test purposes, randomly selects what type of users list to display (admin, pending, or normal)
        HashMap<User, Integer> typeOfUsers = new HashMap<>();
        for (User user : users){
            Random r = new Random();
            typeOfUsers.put(user, r.nextInt(3));
        }

        ListFragment adminList = ListFragment.newInstance(true, this, false, null, null, null, null, users, AdminFragment.this, typeOfUsers, parentOrg);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, adminList)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void viewAnnouncementsState(Organization organization) {
        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        //in the meantime, here is fake data
        ArrayList<Post> posts = new ArrayList<>();

        //THE FOLLOWING ARE FAKE TEST POSTS
        Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data", "Mindbend Studio", null);
        posts.add(testPost1);

        Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio", null);
        posts.add(testPost2);

        Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio", null);
        posts.add(testPost3);

        PostsCardsFragment announcementsStateList = PostsCardsFragment.newInstance(posts, null, true, this);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, announcementsStateList)
                .addToBackStack(null)
                .commit();
    }

    /**
     * The rest of the interfaces (required to have "infinite depth" on the admin tab) are listed below
     *
     */

    @Override
    public void pressedOrg(Organization orgSelected) {
        //TODO: check if the user is an admin of this org
        //currently choosing randomly

        Random r = new Random();
        boolean isModifiable = r.nextBoolean();

        //load up orgs
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, this, isModifiable, onToday, onDiscover, onYou, onAdmin);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, orgProfile).addToBackStack(null).commit();
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
        ProfileFragment userProfile = ProfileFragment.newInstance(userPressed, null, this, false, onToday, onDiscover, onYou, onAdmin);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, userProfile).addToBackStack(null).commit();
    }

    @Override
    public void modifyOrg(Organization org) {
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.admin_framelayout, ModifyOrganizationFragment.newInstance(null, org))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void viewMembers(Organization org) {
        userListOpened(org);
    }

    @Override
    public void userSelected(User user) {
        pressedUserFromCommentOfOrgPost(user);
    }

    @Override
    public void searchForAdmins(Organization organization) {
        //TODO: open searchfrag here
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, AdminFragment.this);
        getChildFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.admin_framelayout, searchableFrag).addToBackStack(null).commit();
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
    public void visitCommentersProfile(User commenterToBeVisited) {

    }
}
