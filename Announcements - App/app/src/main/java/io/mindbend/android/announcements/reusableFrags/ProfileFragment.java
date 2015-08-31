package io.mindbend.android.announcements.reusableFrags;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.PostsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements Serializable, OrgsGridAdapter.OrgInteractionListener, PostOverlayFragment.PostsOverlayListener, OrgsGridFragment.OrgsGridInteractionListener, PostCardFullFragment.FullPostInteractionListener, PostCommentsFragment.CommentsInteractionListener {

    private static final String TAG = "ProfileFragment";
    public static final int UPDATE_PROFILE_IMAGE = 5;
    public static final int UPDATE_COVER_IMAGE = 6;

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
    private static final String ARG_TO_EDIT = "to_edit";
    private static final String ARG_IS_FOLLOWING_ORG = "is_following_org";


    public static final String FULL_POST_FRAG = "full_post_frag";
    public static final String COMMENTS_FRAG = "comments_frag";

    private static final String ON_TODAY = "on_today";
    private static final String ON_DISCOVER = "on_discover";
    private static final String ON_YOU = "on_you";
    private static final String ON_ADMIN = "on_admin";

    private User mUser;
    private Organization mOrg;
    private boolean mToEdit;
    private boolean mIsFollowingOrg;

    private OrgsGridAdapter.OrgInteractionListener mOrgListener = this;
    private PostOverlayFragment.PostsOverlayListener mPostsOverlayListener = this;
    private PostCardFullFragment.FullPostInteractionListener mFullPostInteractionListener = this;
    private PostCommentsFragment.CommentsInteractionListener mCommentsInteractionListener = this;
    private OrgsGridFragment.OrgsGridInteractionListener mOrgsGridInteractionListener = this;
    private ProfileInteractionListener mListener;
    private transient View mView;

    //To set height of content framelayout dynamically (need to change height of embedded layout)
    private transient RelativeLayout mProfileContentFrameLayoutEmbedded;
    private int mDeviceHeight;
    private float mScale;

    private boolean mOnToday;
    private boolean mOnDiscover;
    private boolean mOnYou;
    private boolean mOnAdmin;


    private transient de.hdodenhof.circleimageview.CircleImageView mUserImage;
    private transient ImageView mCoverImage;
    private transient TextView mProfileDetail;
    private transient TextView mProfileTag;
    private transient ProgressBar mLoading;
    private transient ImageButton mFollowFab;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user USER.
     * @param org  ORGANIZATION.
     * @return A new instance of fragment ProfileFragment.
     */

    public static ProfileFragment newInstance(User user, Organization org, boolean isFollowingOrg,
                                              ProfileInteractionListener profileListener,
                                              boolean toEdit, boolean onTodayTab,
                                              boolean onDiscoverTab, boolean onYouTab,
                                              boolean onAdminTab) {

        //***NOTE*** : one of user or org must be null
        //***NOTE*** : only one of onToday, onDiscover or onYou must be true!

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_ORG, org);
        args.putSerializable(ARG_PROFILE_LISTENER, profileListener);
        args.putBoolean(ARG_TO_EDIT, toEdit);
        args.putBoolean(ON_TODAY, onTodayTab);
        args.putBoolean(ON_DISCOVER, onDiscoverTab);
        args.putBoolean(ON_YOU, onYouTab);
        args.putBoolean(ON_ADMIN, onAdminTab);
        args.putBoolean(ARG_IS_FOLLOWING_ORG, isFollowingOrg);
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
            mToEdit = getArguments().getBoolean(ARG_TO_EDIT); //TODO: CALL checkIfUserIsAdminOfOrganization IN CASE OF ORG!
            mOnToday = getArguments().getBoolean(ON_TODAY);
            mOnDiscover = getArguments().getBoolean(ON_DISCOVER);
            mOnYou = getArguments().getBoolean(ON_YOU);
            mOnAdmin = getArguments().getBoolean(ON_ADMIN);
            mIsFollowingOrg = getArguments().getBoolean(ARG_IS_FOLLOWING_ORG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_profile, container, false);

            mLoading = (ProgressBar)mView.findViewById(R.id.profile_frag_progressbar);

            //fetch embedded relativelayout
            mProfileContentFrameLayoutEmbedded = (RelativeLayout) mView.findViewById(R.id.profile_content_framelayout_embedded);

            mUserImage = (CircleImageView) mView.findViewById(R.id.profile_photo);
            mCoverImage = (ImageView)mView.findViewById(R.id.profile_cover_photo);

            //UI elements to be filled
            TextView name = (TextView) mView.findViewById(R.id.profile_name);
            TextView followCount = (TextView) mView.findViewById(R.id.follow_count);
            mProfileDetail = (TextView) mView.findViewById(R.id.profile_detail);
            mProfileTag = (TextView) mView.findViewById(R.id.profile_tag);
            ImageButton modifyButton = (ImageButton) mView.findViewById(R.id.profile_edit_org);

            //get device height and px to dp scale (for dynamic sizing)
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            mDeviceHeight = display.getHeight();
            mScale = getActivity().getResources().getDisplayMetrics().density;
            Log.d(TAG, "Scale is: " + mScale + ", device height is: " + mDeviceHeight);

            //by default, height of framelayout should be half of device height as this is (approximately) the size of the view
            mProfileContentFrameLayoutEmbedded.setMinimumHeight(mDeviceHeight / 2);

            //if the view is of an org that the user is an admin of, or if the user is viewing his/her own profile
            if (mToEdit) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                builder.setTitle("Options");
                modifyButton.setVisibility(View.VISIBLE);
                Log.wtf("ProfileFrag", "modify profile");
                if (mOrg != null) {
                    modifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //This is what's called when the imagebutton is pressed to modify an org/user
                            //modify org
                            builder.setItems(getResources().getStringArray(R.array.profile_edit_org_dialog_options), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    modifyOrgDialogItemSetup(which);

                                }
                            });
                            builder.show();
                        }
                    });
                } else {
                    //mUser isn't null
                    //this case is only true in the youfrag tab

                    modifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //This is what's called when the imagebutton is pressed to modify an org/user
                            //modify org
                            builder.setItems(getResources().getStringArray(R.array.profile_edit_user_dialog_options), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    modifyUserDialogItemSetup(which);

                                }
                            });
                            builder.show();
                        }
                    });

                }
            }

            //Adapter not necessary, few elements on page
            if (mUser != null) {
                name.setText(mUser.getName());
                followCount.setText(mUser.getNumberOfOrganizationsFollowed());
                mProfileDetail.setText(mUser.getmDescription());
                mProfileTag.setText("@"+mUser.getUserCategory());
                if (!mUser.getmProfilePictureURL().equals(""))
                    Picasso.with(getActivity()).load(mUser.getmProfilePictureURL()).into(mUserImage);
                if (!mUser.getmCoverPictureURL().equals(""))
                    Picasso.with(getActivity()).load(mUser.getmCoverPictureURL()).into(mCoverImage);

                //Fill bottom fragment with discover grid if user
                loadOrgsFollowed(mUser.getmObjectId());
                //loadOrgs();
            }

            if (mOrg != null) {
                if (mOrg.isPrivateOrg()) {
                    ImageView isPrivate = (ImageView)mView.findViewById(R.id.profile_private_org_lock_icon);
                    isPrivate.setVisibility(View.VISIBLE);
                    //TODO: check if user is part of this private org, and if he/she is then load posts
                } else {
                    name.setText(mOrg.getTitle());
                    loadOrgPosts(mOrg.getmObjectId(), 0, 10);
                }
                followCount.setText(mOrg.getFollowers() + " Followers");
                mProfileDetail.setText(mOrg.getDescription());
                mProfileTag.setText(mOrg.getTag());


                mFollowFab = (ImageButton)mView.findViewById(R.id.profile_follow_state_fab);
                if (mIsFollowingOrg)
                    mFollowFab.setImageResource(R.drawable.ic_following);
                mFollowFab.setVisibility(View.VISIBLE);
                mFollowFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFollowState();
                    }
                });

                ImageButton viewMembersButton = (ImageButton)mView.findViewById(R.id.profile_view_members_button);
                viewMembersButton.setVisibility(View.VISIBLE);
                viewMembersButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: get followers of org
                        mListener.viewMembers(mOrg);
                    }
                });
            }
        }

        return mView;
    }

    private void updateFollowState() {
        UserDataSource.updateFollowStateForUser(getActivity(), !mIsFollowingOrg, mOrg.getmObjectId(), new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                if (e == null && success){
                    mIsFollowingOrg = !mIsFollowingOrg;

                    if (!mIsFollowingOrg) {
                        mFollowFab.setImageResource(R.drawable.ic_not_following);
                        mToEdit = false;
                        mOnAdmin = false;
                    }
                    else {
                        mFollowFab.setImageResource(R.drawable.ic_following);
                    }
                }
            }
        });
    }

    private void loadOrgsFollowed(final String userObjectId){
        OrgsDataSource.getOrganizationsFollowedByUser(mLoading, userObjectId, new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> orgs, ParseException e) {
                if (e == null) {
                    //add grid frag to bottom of user profile
                    Fragment userOrgsFollowedFragment = OrgsGridFragment.newInstance(orgs, mOrgListener, mOrgsGridInteractionListener);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                    //**DYNAMIC SIZING FOR USER FOLLOWED ORGS GRID**
                    final int orgCardHeight = 260; //this is defined in the layout xml; card height + padding
                    int orgCardColumns = (orgs.size() / 2) + (orgs.size() % 2); //adds additional row for odd numbered org

                    //layout params are in px; must convert dps to px on device
                    int viewHeightInDps = (orgCardHeight * orgCardColumns);
                    int viewHeightinPx = (int) (viewHeightInDps * mScale + 0.5f);

                    if (viewHeightinPx > (mDeviceHeight / 2))
                        mProfileContentFrameLayoutEmbedded.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeightinPx));

                    if (transaction.isEmpty())
                        transaction.add(R.id.profile_content_framelayout, userOrgsFollowedFragment, BOTTOM_FRAG_TAG).commitAllowingStateLoss();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }



    private void loadOrgPosts (String orgObjectId, int startIndex, int numberOfPosts){
        PostsDataSource.getPostsOfOrganizationInRange(mLoading, getActivity(), orgObjectId, startIndex, numberOfPosts, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> orgPosts, ParseException e) {
                if (e == null){
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

                    if (viewHeightinPx > (mDeviceHeight / 2))
                        mProfileContentFrameLayoutEmbedded.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeightinPx));

                    if (transaction.isEmpty())
                        transaction.add(R.id.profile_content_framelayout, orgPostsFragment, BOTTOM_FRAG_TAG).commitAllowingStateLoss();
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void modifyUserDialogItemSetup(int which) {
        /**
         * 0 = Profile Pic, 1 = Cover Pic, 2 = Description
         */
        final int UPDATE_PROFILE_PHOTO = 0;
        final int UPDATE_COVER_PHOTO = 1;
        final int UPDATE_INTERESTS_DESCRIPTION = 2;


        switch (which) {
            case UPDATE_PROFILE_PHOTO:
                Log.wtf("Image", "image selection begun");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), UPDATE_PROFILE_IMAGE);
                break;
            case UPDATE_COVER_PHOTO:
                Log.wtf("Image", "image selection begun");
                Intent intent2 = new Intent();
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent2,
                        "Select Picture"), UPDATE_COVER_IMAGE);
                break;
            case UPDATE_INTERESTS_DESCRIPTION:
                editInterestsDialog();
                break;
        }
    }

    private void modifyOrgDialogItemSetup(int which) {
        /**
         * 0 = Modify, 1 = View Members, 2 = View Announcements
         */
        final int MODIFY = 0;
        final int VIEW_MEMBERS = 1;
        final int VIEW_ANNOUNCEMENTS = 2;

        switch (which) {
            case MODIFY:
                mListener.modifyOrg(mOrg);
                break;
            case VIEW_MEMBERS:
                mListener.viewMembers(mOrg);
                break;
            case VIEW_ANNOUNCEMENTS:
                mListener.viewAnnouncementsState(mOrg);
                break;
        }
    }

    private void editInterestsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText interestOneET = new EditText(getActivity());
        String in1text = "#1: ";
        if (mUser.getInterestOne() != null)
            in1text = in1text + mUser.getInterestOne();
        interestOneET.setHint(in1text);
        layout.addView(interestOneET);

        final EditText interestTwoET = new EditText(getActivity());
        String in2text = "#2: ";
        if (mUser.getInterestTwo() != null)
            in2text = in2text + mUser.getInterestTwo();
        interestTwoET.setHint(in2text);
        layout.addView(interestTwoET);

        alert.setView(layout);
        alert.setTitle("Enter your Interests");
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                if (!interestOneET.getText().toString().equals("") && !interestTwoET.getText().toString().equals("")) {
                    //TODO: save to parse
                    final String i1 = interestOneET.getText().toString();
                    final String i2 = interestTwoET.getText().toString();
                    UserDataSource.updateUserDescription(getActivity() , ((TabbedActivity) getActivity()).mYouFragment.mLoading, "Interested in " + i1 + " and " + i2, new FunctionCallback<Boolean>() {
                        @Override
                        public void done(Boolean success, ParseException e) {
                            if (success) {
                                Toast.makeText(getActivity(), "Interests updated", Toast.LENGTH_SHORT).show();
                                mUser.setInterestOne(i1);
                                mUser.setInterestTwo(i2);
                                mProfileDetail.setText(mUser.getInterests());
                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                } else
                    Toast.makeText(getActivity(), "Cannot leave fields blank!", Toast.LENGTH_LONG).show();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
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

    public interface ProfileInteractionListener extends Serializable {
        void userProfileToOrgProfile(Organization orgSelected);

        void pressedOrgFromProfile(Organization orgPressed);

        void pressedUserFromCommentOfOrgPost(User userPressed);

        void modifyOrg(Organization org);

        void viewMembers(Organization org);

        void viewAnnouncementsState(Organization org);
    }

    public interface ProfilePostCommentClick extends Serializable {
        void pressedPostFromProfile(Post postClicked);
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
        transaction.replace(getAppropriateFramelayout(), mFullPost).addToBackStack(FULL_POST_FRAG).commitAllowingStateLoss();
    }

    @Override
    public void profileComments(Post post) {
        //Open comment frag in parent frame layout
        mCurrentComments = PostCommentsFragment.newInstance(post, mCommentsInteractionListener);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getAppropriateFramelayout(), mCurrentComments).addToBackStack(COMMENTS_FRAG).commitAllowingStateLoss();
    }

    @Override
    public void refreshPosts() {

    }

    @Override
    public void openOrgProfileFromPosts(Organization organization) {
        if (organization != mOrg)
            pressedOrg(organization);
    }

    @Override
    public void CommentButtonClicked(Post postComments) {
        profileComments(postComments);
    }

    @Override
    public void openPosterOrgProfile(Organization organization) {
        if (organization != mOrg)
            pressedOrg(organization);
    }

    @Override
    public void pressedBackToPosts() {

    }

    @Override
    public void pressedCommenterProfile(User commenterPressed) {
        mPostsOverlayListener.visitCommentersProfile(commenterPressed);
    }

    public void updateProfileImage(Bitmap bitmap) {
        mUserImage.setImageBitmap(bitmap);
    }

    public void updateCoverImage(Bitmap bitmap){
        mCoverImage.setImageBitmap(bitmap);
    }

    private int getAppropriateFramelayout() {
        if (mOnToday)
            return R.id.today_framelayout;
        if (mOnDiscover)
            return R.id.discover_framelayout;
        if (mOnYou)
            return R.id.you_framelayout;
        //if (mOnAdmin)
        return R.id.admin_framelayout;
    }
}
