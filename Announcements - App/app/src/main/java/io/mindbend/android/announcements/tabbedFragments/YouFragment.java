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
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
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
public class YouFragment extends Fragment implements Serializable, ProfileFragment.ProfileInteractionListener, SearchableFrag.SearchInterface, ListFragment.ListFabListener, UserListAdapter.UserListInteractionListener, PostOverlayFragment.PostsOverlayListener{
    private static final String TAG = "TAG";
    private static final String DEFAULT = "default_frag";
    private ProfileFragment mProfileFragment;

    private boolean onToday = false;
    private boolean onDiscover = false;
    private boolean onYou = true;
    private boolean onAdmin = false;

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
        View v = inflater.inflate(R.layout.fragment_you, container, false);

        //FAKE USER FOR TESTING
        User testUser = new User("Aditya", "Chugh", "getting paper", "node.js", "#Grade12", 9);

        mProfileFragment = ProfileFragment.newInstance(testUser, null, this, true, onToday, onDiscover, onYou, onAdmin);

        //inflate profileFrag in framelayout
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.you_framelayout, mProfileFragment).addToBackStack(DEFAULT).commitAllowingStateLoss();

        return v;
    }
    public Fragment getmProfileFragment() {
        return mProfileFragment;
    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
        //TODO: check if the user is an admin of this org
        //currently choosing randomly

        Random r = new Random();
        boolean isModifiable = r.nextBoolean();

//        replace the current profile frag with new org profile frag, while adding it to a backstack
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, this, isModifiable, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.you_framelayout, orgProfile).addToBackStack(null).commitAllowingStateLoss();

        Log.d(TAG, "org has been pressed on profile page " + orgSelected.toString());
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        userProfileToOrgProfile(orgPressed);
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userToVisit = ProfileFragment.newInstance(userPressed, null, this, false, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.you_framelayout, userToVisit).addToBackStack(null).commitAllowingStateLoss();
    }
    @Override
    public void modifyOrg(Organization org) {
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.you_framelayout, ModifyOrganizationFragment.newInstance(null, org))
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void viewMembers(Organization org) {
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

        ListFragment adminList = ListFragment.newInstance(true, YouFragment.this, false, null, null, null, null, users, YouFragment.this, typeOfUsers, org);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.you_framelayout, adminList)
                .addToBackStack(null)
                .commitAllowingStateLoss();
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
    public void searchForAdmins(Organization organization) {
        //TODO: open searchfrag here
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, YouFragment.this);
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
    public void visitCommentersProfile(User commenterToBeVisited) {

    }
}
