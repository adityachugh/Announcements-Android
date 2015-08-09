package io.mindbend.android.announcements.reusableFrags;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener, PostOverlayFragment.PostsOverlayListener, OrgsGridFragment.OrgsGridInteractionListener {

    private static final String TAG = "ProfileFragment";

    //To add frags to backstack
    public static final String ORG_PROFILE_FRAG = "org_profile_frag";
    private static final String BOTTOM_FRAG_TAG = "tag_for_bottom_frag_of_orgs_and_profiles";
    private Fragment mOrgProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PROFILE_LISTENER = "profile_listener_interface";
    private static final String ARG_ORG = "org";

    private User mUser;
    private Organization mOrg;

    private OrgsGridAdapter.OrgInteractionListener mOrgListener = this;
    private PostOverlayFragment.PostsOverlayListener mPostsOverlayListener = this;
    private ProfileInteractionListener mListener;
    private transient View mView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user USER.
     * @param org  ORGANIZATION.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(User user, Organization org, ProfileInteractionListener profileListener) {

        //***NOTE*** : one of user or org must be null

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_ORG, org);
        args.putSerializable(ARG_PROFILE_LISTENER, profileListener);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
            mOrg = (Organization) getArguments().getSerializable(ARG_ORG);
            mListener = (ProfileInteractionListener) getArguments().getSerializable(ARG_PROFILE_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null){
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_profile, container, false);

            //UI elements to be filled
            TextView name = (TextView) mView.findViewById(R.id.profile_name);
            TextView followCount = (TextView) mView.findViewById(R.id.follow_count);
            TextView profileDetail = (TextView) mView.findViewById(R.id.profile_detail);
            TextView profileTag = (TextView) mView.findViewById(R.id.profile_tag);

            //TODO: branch based on whether user or org is null

            //Adapter not necessary, few elements on page
            if (mUser != null) {
                name.setText(mUser.getName());
                followCount.setText(mUser.getNumberOfOrganizationsFollowed());
                profileDetail.setText(mUser.getInterests());
                profileTag.setText(mUser.getUserCategory());

                //Fill bottom fragment with discover grid if user(temporary)
                //TODO: Fetch followed orgs OR organization's announcements (generic fragment)
                ArrayList<Organization> orgs = new ArrayList<>();

                //ORG CONSTRUCTOR: String objectId, String title, String description, int followers, String tag, boolean privateOrg, boolean newOrg
                //FAKE ORGANIZATIONS TO TEST
                Organization testOrg1 = new Organization("test Id", "Software Dev Club", "Learn to make apps! Android! Fun!", 803, "#SoftwareDevClub", false, true); //TODO: change "NEW" to be a dynamically chosen banner
                orgs.add(testOrg1);

                Organization testOrg2 = new Organization("test Id", "Math Club", "We had that one meeting that one time", 11, "#MathClub", false, false); //TODO: change "NEW" to be a dynamically chosen banner
                orgs.add(testOrg2);

                Organization testOrg3 = new Organization("test Id", "Mindbend Studio", "The best dev firm hello@mindbend.io", 80, "#BendBoundaries", true, true); //TODO: change "NEW" to be a dynamically chosen banner
                orgs.add(testOrg3);

                //add grid frag to bottom of user profile
                Fragment userOrgsFollowedFragment = OrgsGridFragment.newInstance(orgs, mOrgListener, this);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                if (transaction.isEmpty())
                    transaction.add(R.id.profile_content_framelayout, userOrgsFollowedFragment, BOTTOM_FRAG_TAG).commit();
            }

            if(mOrg != null){
                if (mOrg.isPrivateOrg()){
                    name.setText(mOrg.getTitle() + " [PRIVATE]");
                    //TODO: add imageview of lock to indicate private
                }
                else {
                    name.setText(mOrg.getTitle());
                }
                followCount.setText(mOrg.getFollowers() + " Followers");
                profileDetail.setText(mOrg.getDescription());
                profileTag.setText(mOrg.getTag());

                if (mOrg.isPrivateOrg() == false){

                    //TODO: query org's posts from parse, populate arraylist of posts
                    ArrayList<Post> orgPosts = new ArrayList<>();
                    //THE FOLLOWING ARE FAKE TEST POSTS
                    Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data", "Mindbend Studio");
                    orgPosts.add(testPost1);

                    Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio");
                    orgPosts.add(testPost2);

                    Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio");
                    orgPosts.add(testPost3);

                    //add posts frag to bottom of org profile
                    Fragment orgPostsFragment = PostOverlayFragment.newInstance(orgPosts, mPostsOverlayListener);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    if (transaction.isEmpty())
                        transaction.add(R.id.profile_content_framelayout, orgPostsFragment, BOTTOM_FRAG_TAG).commit();
                }
            }

            //Get scrollview, scroll to top
            //TODO: not working!
            ParallaxScrollView parallaxScrollView = (ParallaxScrollView) mView.findViewById(R.id.profile_scrollview);
            parallaxScrollView.scrollTo(0, 0);

            //Get framelayout (HIERARCHY: FrameLayout > RecyclerView > CardView)
            //TODO: set height manually, based on number of grandchildren populated in framelayout
            FrameLayout profileContentFrameLayout = (FrameLayout) mView.findViewById(R.id.profile_content_framelayout);
        }

        return mView;
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
    public void pressedOrg(Organization orgSelected) {
        mListener.userProfileToOrgProfile(orgSelected);
    }

    @Override
    public void onReturnToPosts() {
        //required empty method for post overlay listener
    }

    @Override
    public void onCommentsOpened(Post postPressed) {
        //required empty method for post overlay listener
    }

    public interface ProfileInteractionListener extends Serializable{
        void userProfileToOrgProfile (Organization orgSelected);
        void pressedOrgFromProfile(Organization orgPressed);
        void pressedUserFromCommentOfOrgPost(User userPressed);
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        mListener.pressedOrgFromProfile(orgPressed);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {
        mListener.pressedUserFromCommentOfOrgPost(commenterToBeVisited);
    }
}
