package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.adminClasses.ModifyOrganizationFragment;
import io.mindbend.android.announcements.cloudCode.AdminDataSource;
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
public class DiscoverFragment extends Fragment implements Serializable, PostsFeedAdapter.PostInteractionListener, ProfileFragment.ProfileInteractionListener, SearchableFrag.SearchInterface, UserListAdapter.UserListInteractionListener, ListFragment.ListFabListener, PostOverlayFragment.PostsOverlayListener {


    private static final String TAG = "TAG";
    private static final String ORG_PROFILE_FRAG = "ORG_PROFILE_FRAGMENT";
    private transient SearchableFrag mOrgsGridFrag;

    //pass into profilefrag new instance in this order!
    private boolean onToday = false;
    private boolean onDiscover = true;
    private boolean onYou = false;
    private boolean onAdmin = false;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_discover, container, false);
        setRetainInstance(true);
        //TODO: query discover_clubs data from Parse, then pass that data into an OrgsGridFragment that will be created using the OrgsGridFragment.NewInstance static method

        mOrgsGridFrag = SearchableFrag.newInstance(SearchableFrag.ORGS_TYPE, null, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.discover_framelayout, mOrgsGridFrag).commitAllowingStateLoss();
        return v;
    }

    public SearchableFrag getmOrgsGridFrag() {
        return mOrgsGridFrag;
    }


    @Override
    public void pressedPostComments(Post postPressed) {
        //TODO: do stuff, although switching to comments frag is already handled
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
    public void pressedOrgFromProfile(Organization orgPressed) {
        //TODO: check if the user is an admin of this org
        //currently choosing randomly

        Log.wtf(TAG, "PARSE USER " + ParseUser.getCurrentUser().getObjectId().toString());
        AdminDataSource.checkIfUserIsAdminOfOrganization(getActivity(), orgPressed, ParseUser.getCurrentUser(), new FunctionCallback<String>() {
            @Override
            public void done(String isAdmin, ParseException e) {
                if (e == null){
                    Log.wtf(TAG, "IS USER ADMIN? " + isAdmin);
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        Random r = new Random();
        boolean isModifiable = r.nextBoolean();

        //replace the current profile frag with new org profile frag, while adding it to a backstack
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgPressed, this, isModifiable, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, orgProfile).addToBackStack(null).commitAllowingStateLoss();
        Log.d(TAG, "org has been pressed on discover page " + orgPressed.toString());
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userToVisit = ProfileFragment.newInstance(userPressed, null, this, false, onToday, onDiscover, onYou, onAdmin);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, userToVisit).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void modifyOrg(Organization org) {
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.discover_framelayout, ModifyOrganizationFragment.newInstance(null, org))
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

        ListFragment adminList = ListFragment.newInstance(true, DiscoverFragment.this, false, null, null, null, null, users, DiscoverFragment.this, typeOfUsers, org);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.discover_framelayout, adminList)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void viewAnnouncementsState(Organization org) {
        //TODO: query today's posts data from Parse, then pass that data into a PostsCardFragment that will be created using the PostsCardsFragment.NewInstance static method
        //in the meantime, here is fake data
        ArrayList<Post> posts = new ArrayList<>();

        //THE FOLLOWING ARE FAKE TEST POSTS
        Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
        posts.add(testPost1);

        Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
        posts.add(testPost2);

        Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio", "hasImage");
        posts.add(testPost3);

        PostsCardsFragment announcementsStateList = PostsCardsFragment.newInstance(posts, null, true, this);
        getChildFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.discover_framelayout, announcementsStateList)
                .addToBackStack(null)
                .commitAllowingStateLoss();
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
    public void searchForAdmins(Organization organization) {
        //TODO: open searchfrag here
        SearchableFrag searchableFrag = SearchableFrag.newInstance(SearchableFrag.USERS_TYPE, organization, DiscoverFragment.this);
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
    public void refreshPosts() {

    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {

    }
}
