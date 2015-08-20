package io.mindbend.android.announcements.reusableFrags;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
public class ProfileFragment extends Fragment implements Serializable, OrgsGridAdapter.OrgInteractionListener, PostOverlayFragment.PostsOverlayListener, OrgsGridFragment.OrgsGridInteractionListener, PostCardFullFragment.FullPostInteractionListener, PostCommentsFragment.CommentsInteractionListener {

    private static final String TAG = "ProfileFragment";

    //To add frags to backstack
    public static final String ORG_PROFILE_FRAG = "org_profile_frag";
    private static final String BOTTOM_FRAG_TAG = "tag_for_bottom_frag_of_orgs_and_profiles";
    private Fragment mFullPost;
    private Fragment mCurrentComments;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PROFILE_LISTENER = "profile_listener_interface";
    private static final String ARG_ORG = "org";

    public static final String FULL_POST_FRAG = "full_post_frag";
    public static final String COMMENTS_FRAG = "comments_frag";

    private User mUser;
    private Organization mOrg;

    private OrgsGridAdapter.OrgInteractionListener mOrgListener = this;
    private PostOverlayFragment.PostsOverlayListener mPostsOverlayListener = this;
    private PostCardFullFragment.FullPostInteractionListener mFullPostInteractionListener = this;
    private PostCommentsFragment.CommentsInteractionListener mCommentsInteractionListener = this;
    private ProfileInteractionListener mListener;
    private transient View mView;

    //To set height of content framelayout dynamically (need to change height of embedded layout)
    private RelativeLayout mProfileContentFrameLayoutEmbedded;
    private int mDeviceHeight;
    private float mScale;


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

            //fetch embedded relativelayout
            mProfileContentFrameLayoutEmbedded = (RelativeLayout) mView.findViewById(R.id.profile_content_framelayout_embedded);

            //UI elements to be filled
            TextView name = (TextView) mView.findViewById(R.id.profile_name);
            TextView followCount = (TextView) mView.findViewById(R.id.follow_count);
            TextView profileDetail = (TextView) mView.findViewById(R.id.profile_detail);
            TextView profileTag = (TextView) mView.findViewById(R.id.profile_tag);


            //get device height and px to dp scale (for dynamic sizing)
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            mDeviceHeight = display.getHeight();
            mScale = getActivity().getResources().getDisplayMetrics().density;
            Log.d(TAG, "Scale is: " + mScale + ", device height is: " + mDeviceHeight);

            //by default, height of framelayout should be half of device height as this is (approximately) the size of the view
            mProfileContentFrameLayoutEmbedded.setMinimumHeight(mDeviceHeight / 2);


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
                orgs.add(testOrg3);
                orgs.add(testOrg3);

                //add grid frag to bottom of user profile
                Fragment userOrgsFollowedFragment = OrgsGridFragment.newInstance(orgs, mOrgListener, this);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                //**DYNAMIC SIZING FOR USER FOLLOWED ORGS GRID**
                final int orgCardHeight = 260; //this is defined in the layout xml; card height + padding
                int orgCardColumns = (orgs.size()/2) + (orgs.size()%2); //adds additional row for odd numbered org

                //layout params are in px; must convert dps to px on device
                int viewHeightInDps = (orgCardHeight * orgCardColumns);
                int viewHeightinPx = (int) (viewHeightInDps * mScale + 0.5f);

                if (viewHeightinPx > (mDeviceHeight/2))
                    mProfileContentFrameLayoutEmbedded.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeightinPx));


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

                if (!mOrg.isPrivateOrg()){

                    //TODO: query org's posts from parse, populate arraylist of posts
                    ArrayList<Post> orgPosts = new ArrayList<>();
                    //THE FOLLOWING ARE FAKE TEST POSTS
                    Post testPost1 = new Post("testID", "Test Title 1", "2 hours ago", "This is a test post with fake data. Yeah! eat sleep rave repeat. Is that a world tour or your girl's tour? Rip meek mill 6ix god 6ix god 6ix god 6ix god 6ix god 6ix god 6ix god 6ix god ", "Mindbend Studio", "hasImage");
                    orgPosts.add(testPost1);

                    Post testPost2 = new Post("testID", "Test Title 2", "4 hours ago", "This is a test post with fake data", "Mindbend Studio", "hasImage");
                    orgPosts.add(testPost2);

                    Post testPost3 = new Post("testID", "Test Title 3", "5 hours ago", "This is a test post with fake data", "Mindbend Studio", "");
                    orgPosts.add(testPost3);
                    orgPosts.add(testPost3);

                    //add posts frag to bottom of org profile
                    Fragment orgPostsFragment = PostOverlayFragment.newInstance(orgPosts, mPostsOverlayListener, true);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                    //**DYNAMIC SIZING FOR ORG POSTS**
                    final int postCardHeight = 204; //this is defined in the layout xml; card height + padding
                    final int postCardHeightWithImage = 404; //this is defined in the layout xml; card height + padding
                    int posts = 0;
                    int postsWithImage = 0;

                    //count number of posts with images, and posts without
                    for (int i = 0; i < orgPosts.size(); i++) {
                        if (orgPosts.get(i).getmPostImageURL().equals("")) //no image
                            posts++;
                        else //image
                            postsWithImage++;
                    }

                    //layout params are in px; must convert dps to px on device
                    int viewHeightInDps = (postCardHeight * posts) + (postCardHeightWithImage * postsWithImage);
                    int viewHeightinPx = (int) (viewHeightInDps * mScale + 0.5f);

                    if (viewHeightinPx > (mDeviceHeight/2))
                        mProfileContentFrameLayoutEmbedded.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeightinPx));

                    //TODO: set height to device height - actionbar and tabview (48dp each) when comment open, but also add height of post (image + 10dp per line of text)
                    //ensures one full page of scrollable comments (no obstruction from long post)

                    if (transaction.isEmpty())
                        transaction.add(R.id.profile_content_framelayout, orgPostsFragment, BOTTOM_FRAG_TAG).commit();
                }
            }

            //Get scrollview, scroll to top
            //TODO: not working!
            ParallaxScrollView parallaxScrollView = (ParallaxScrollView) mView.findViewById(R.id.profile_scrollview);
            parallaxScrollView.scrollTo(0, 0);
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

    public interface ProfilePostCommentClick extends Serializable{
        void pressedPostFromProfile (Post postClicked);
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        mListener.pressedOrgFromProfile(orgPressed);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {
        mListener.pressedUserFromCommentOfOrgPost(commenterToBeVisited);
    }

    @Override
    public void fullPostProfile(Post clickedPost) {
        //Open full post frag in parent frame layout
        mFullPost = PostCardFullFragment.newInstance(clickedPost, mFullPostInteractionListener);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.profile_framelayout, mFullPost).addToBackStack(FULL_POST_FRAG).commit();
    }

    @Override
    public void profileComments(Post post) {
        //Open comment frag in parent frame layout
        mCurrentComments = PostCommentsFragment.newInstance(post, mCommentsInteractionListener);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_framelayout, mCurrentComments).addToBackStack(COMMENTS_FRAG).commit();
    }

    @Override
    public void CommentButtonClicked(Post postComments) {
        profileComments(postComments);
    }

    @Override
    public void pressedBackToPosts() {

    }

    @Override
    public void pressedCommenterProfile(User commenterPressed) {
        mPostsOverlayListener.visitCommentersProfile(commenterPressed);
    }
}
