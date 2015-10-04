package io.mindbend.android.announcements.reusableFrags;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

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

    private static final int COVER_PHOTO_WIDTH = 1000;
    private static final int COVER_PHOTO_HEIGHT = 600;

    //To add frags to backstack
    public static final String ORG_PROFILE_FRAG = "org_profile_frag";
    private static final String BOTTOM_FRAG_TAG = "tag_for_bottom_frag_of_orgs_and_profiles";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PROFILE_LISTENER = "profile_listener_interface";
    private static final String ARG_ORG = "org";
    private static final String ARG_TO_EDIT = "to_edit";
    private static final String ARG_ORG_FOLLOW_STATE = "org_follow_state";


    public static final String FULL_POST_FRAG = "full_post_frag";
    public static final String COMMENTS_FRAG = "comments_frag";

    private static final String ON_TODAY = "on_today";
    private static final String ON_DISCOVER = "on_discover";
    private static final String ON_YOU = "on_you";
    private static final String ON_ADMIN = "on_admin";

    private User mUser;
    private Organization mOrg;
    private boolean mToEdit;
    private String mOrgFollowState;

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

    public static ProfileFragment newInstance(User user, Organization org, String orgFollowState,
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
        args.putString(ARG_ORG_FOLLOW_STATE, orgFollowState);
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
            mToEdit = getArguments().getBoolean(ARG_TO_EDIT);
            mOnToday = getArguments().getBoolean(ON_TODAY);
            mOnDiscover = getArguments().getBoolean(ON_DISCOVER);
            mOnYou = getArguments().getBoolean(ON_YOU);
            mOnAdmin = getArguments().getBoolean(ON_ADMIN);
            mOrgFollowState = getArguments().getString(ARG_ORG_FOLLOW_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_profile, container, false);

            mLoading = (ProgressBar) mView.findViewById(R.id.profile_frag_progressbar);

//            mRefresher = (SwipyRefreshLayout)mView.findViewById(R.id.profile_refresh);
//            mRefresher.setColorSchemeResources(R.color.accent, R.color.primary);
//            if (mUser != null) { //don't need user refresh of info, can`t refresh user orgs due to fixed parallax view height
//                mRefresher.setVisibility(View.GONE);
//            }
//            else {
//                mRefresher.setOnRefreshListener(this);
//            }

            //fetch embedded relativelayout
            mProfileContentFrameLayoutEmbedded = (RelativeLayout) mView.findViewById(R.id.profile_content_framelayout_embedded);

            mUserImage = (CircleImageView) mView.findViewById(R.id.profile_photo);
            mCoverImage = (ImageView) mView.findViewById(R.id.profile_cover_photo);

            //UI elements to be filled
            TextView name = (TextView) mView.findViewById(R.id.profile_name);
            TextView followCount = (TextView) mView.findViewById(R.id.follow_count);
            mProfileDetail = (TextView) mView.findViewById(R.id.profile_detail);
            TextView mProfileTag = (TextView) mView.findViewById(R.id.profile_tag);
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
                mProfileTag.setText("@" + mUser.getUserCategory());
                if (!mUser.getmProfilePictureURL().equals(""))
                    Picasso.with(getActivity()).load(mUser.getmProfilePictureURL()).into(mUserImage);
                if (!mUser.getmCoverPictureURL().equals(""))
                    Picasso.with(getActivity()).load(mUser.getmCoverPictureURL()).resize(COVER_PHOTO_WIDTH,COVER_PHOTO_HEIGHT).into(mCoverImage);

                //Fill bottom fragment with discover grid if user
                loadOrgsFollowed(mUser.getmObjectId());
                //loadOrgs();
            }

            if (mOrg != null) {
                name.setText(mOrg.getTitle());

                if (mOrg.getmProfileImageURL() != null && !mOrg.getmProfileImageURL().equals(""))
                    Picasso.with(getActivity()).load(mOrg.getmProfileImageURL()).into(mUserImage);
                if (mOrg.getmCoverImageURL() != null && !mOrg.getmCoverImageURL().equals(""))
                    Picasso.with(getActivity()).load(mOrg.getmCoverImageURL()).resize(COVER_PHOTO_WIDTH,COVER_PHOTO_HEIGHT).into(mCoverImage);

                if (mOrg.isPrivateOrg()) {
                    ImageView isPrivate = (ImageView) mView.findViewById(R.id.profile_private_org_lock_icon);
                    isPrivate.setVisibility(View.VISIBLE);
                    if (mOrgFollowState != null && (mOrgFollowState.equals(UserDataSource.FOLLOWER_NORMAL) || mOrgFollowState.equals(UserDataSource.FOLLOWER_ADMIN)))
                        loadOrgPosts(mOrg.getmObjectId(), 0, 10);
                } else {
                    loadOrgPosts(mOrg.getmObjectId(), 0, 10);
                }
                followCount.setText(mOrg.getFollowers() + " Followers");
                mProfileDetail.setText(mOrg.getDescription());
                mProfileTag.setText(mOrg.getTag());


                mFollowFab = (ImageButton) mView.findViewById(R.id.profile_follow_state_fab);
                mFollowFab.setVisibility(View.VISIBLE);

                if (!mOrgFollowState.equals(UserDataSource.FOLLOWER_NOT_FOLLOWING)) {
                    switch (mOrgFollowState) {
                        case UserDataSource.FOLLOWER_ADMIN:
                            mFollowFab.setImageResource(R.drawable.ic_following);
                            break;
                        case UserDataSource.FOLLOWER_NORMAL:
                            mFollowFab.setImageResource(R.drawable.ic_following);
                            break;
                        case UserDataSource.FOLLOWER_PENDING:
                            mFollowFab.setImageResource(R.drawable.ic_pending);
                            break;
                        case UserDataSource.FOLLOWER_REJECTED:
                            mFollowFab.setImageResource(R.drawable.ic_rejected);
                            break;
                    }
                }

                mFollowFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.wtf("has access code?", ""+mOrg.hasAccessCode());
                        if (!mOrg.isPrivateOrg() || (mOrg.isPrivateOrg() && !mOrg.hasAccessCode() && !mOrgFollowState.equals(UserDataSource.FOLLOWER_PENDING)))//if it's public or private but with no access code
                            updateFollowState();
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                            if (mOrgFollowState.equals(UserDataSource.FOLLOWER_NOT_FOLLOWING)) {
                                sendFollowRequestToPrivateOrg();
                            } else {
                                switch (mOrgFollowState) {
                                    case UserDataSource.FOLLOWER_PENDING:
                                        builder.setTitle(R.string.follow_request_already_sent)
                                                .setMessage(R.string.follow_request_already_sent_dialog_detailed_message)
                                                .setPositiveButton("OK", null)
                                                .show();
                                        break;
                                    case UserDataSource.FOLLOWER_REJECTED:
                                        builder.setTitle(R.string.rejected_resend_follow_request_dialog_title)
                                                .setMessage(R.string.rejected_resend_follow_request_dialog_message)
                                                .setNegativeButton("Cancel", null)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        sendFollowRequestToPrivateOrg();
                                                    }
                                                })
                                                .show();
                                        break;
                                    default:
                                        //already follower, state = accepted
                                        updateFollowState();
                                        break;
                                }
                            }
                        }
                    }
                });

                //make the view members button visible
                Button viewMembersButton = (Button) mView.findViewById(R.id.profile_view_members_button);
                viewMembersButton.setVisibility(View.VISIBLE);
                viewMembersButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.viewMembers(mOrg, mToEdit);
                    }
                });

                //make the "child org" button visible and set its text to the appropriate level below the current org
                if (mOrg.getmChildLevel() != null && mOrg.getmChildLevel().getmLevelTitle() != null && !mOrg.getmChildLevel().getmLevelTitle().equals("")){
                    Button childrenOrgsButton = (Button)mView.findViewById(R.id.profile_view_children_button);
                    childrenOrgsButton.setVisibility(View.VISIBLE);
                    childrenOrgsButton.setText(mOrg.getmChildLevel().getmLevelTitle());
                    childrenOrgsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: show children orgs
                        }
                    });
                }
            }
        }

        return mView;
    }

    private void sendFollowRequestToPrivateOrg() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 0, 16, 0);

        final EditText requestCode = new EditText(getActivity());
        requestCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        requestCode.setHint(R.string.enter_access_code_for_private_org_message);
        layout.addView(requestCode);

        alert.setView(layout);
        alert.setTitle("Send follow request to " + mOrg.getTitle());
        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                OrgsDataSource.privateOrganizationAccessCodeEntered(getActivity(), mView, mLoading, R.id.profile_remove_view_while_loading,mOrg.getmObjectId(), requestCode.getText().toString(), new FunctionCallback<Boolean>() {
                    @Override
                    public void done(Boolean followRequestSent, ParseException e) {
                        if (e == null){
                            if (followRequestSent){
                                mOrgFollowState = UserDataSource.FOLLOWER_PENDING;
                                mFollowFab.setImageResource(R.drawable.ic_pending);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                                builder.setTitle("Incorrect access code entered.")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }
                    }
                });
            }
        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateFollowState() {
        final boolean isFollowing = mOrgFollowState != null && (mOrgFollowState.equals(UserDataSource.FOLLOWER_NORMAL) || mOrgFollowState.equals(UserDataSource.FOLLOWER_ADMIN));
        final boolean toChangeStateTo = !isFollowing;

        if (isFollowing) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
            builder.setTitle(R.string.unfollow_org_title)
                    .setMessage(getString(R.string.format_unfollow_org_message, mOrg.getTitle()))
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            UserDataSource.updateFollowStateForUser(getActivity(), mOrg.isPrivateOrg(), mView, toChangeStateTo, mOrg.getmObjectId(), new FunctionCallback<Boolean>() {
                                @Override
                                public void done(Boolean success, ParseException e) {
                                    if (success) {
                                        //unfollows
                                        mFollowFab.setImageResource(R.drawable.ic_not_following);
                                        mToEdit = false;
                                        mOrgFollowState = UserDataSource.FOLLOWER_NOT_FOLLOWING;
                                    }
                                }
                            });
                        }
                    })
                    .show();
        } else {
            UserDataSource.updateFollowStateForUser(getActivity(), mOrg.isPrivateOrg(), mView, toChangeStateTo, mOrg.getmObjectId(), new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    if (success) {
                        if (mOrg.isPrivateOrg()){
                            mFollowFab.setImageResource(R.drawable.ic_pending);
                            mOrgFollowState = UserDataSource.FOLLOWER_PENDING;
                        } else {
                            mFollowFab.setImageResource(R.drawable.ic_following);
                            mOrgFollowState = UserDataSource.FOLLOWER_NORMAL;
                        }
                    }
                }
            });
        }
    }

    private void loadOrgsFollowed(final String userObjectId) {
        OrgsDataSource.getOrganizationsFollowedByUserInRange(mView, getActivity(), mLoading, userObjectId, new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> orgs, ParseException e) {
                if (e == null) {
                    //add grid frag to bottom of user profile
                    Fragment userOrgsFollowedFragment = OrgsGridFragment.newInstance(orgs, mOrgListener, mOrgsGridInteractionListener, null, false);

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
                    Snackbar.make(mView, "Error", Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }


    private void loadOrgPosts(String orgObjectId, int startIndex, int numberOfPosts) {
        PostsDataSource.getPostsOfOrganizationInRange(mView, mLoading, false,R.id.profile_remove_view_while_loading ,getActivity(), orgObjectId, startIndex, numberOfPosts, new FunctionCallback<ArrayList<Post>>() {
            @Override
            public void done(ArrayList<Post> orgPosts, ParseException e) {
                if (e == null) {
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
                } else {
                    Snackbar.make(mView, "Error", Snackbar.LENGTH_SHORT).show();
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
                mListener.viewMembers(mOrg, mToEdit);
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
        layout.setPadding(16,0,0,16);

        final EditText newDescription = new EditText(getActivity());
        newDescription.setHint(mUser.getmDescription());
        layout.addView(newDescription);

        alert.setView(layout);
        alert.setTitle("Enter your description");
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                final String description = newDescription.getText().toString();
                if (!description.equals("")) {
                    final String i1 = newDescription.getText().toString();
                    UserDataSource.updateUserDescription(mView, getActivity(), description, new FunctionCallback<Boolean>() {
                        @Override
                        public void done(Boolean success, ParseException e) {
                            if (success) {
                                Snackbar.make(mView, "Interests updated", Snackbar.LENGTH_SHORT).show();
                                mUser.setmDescription(newDescription.getText().toString());
                                mProfileDetail.setText(description);
                            } else {
                                Snackbar.make(mView, "Error", Snackbar.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                } else
                    Snackbar.make(mView, "Cannot leave fields blank!", Snackbar.LENGTH_LONG).show();

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

        void viewMembers(Organization org, boolean isAdmin);

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
        Fragment mFullPost = PostCardFullFragment.newInstance(clickedPost, mFullPostInteractionListener, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getAppropriateFramelayout(), mFullPost).addToBackStack(FULL_POST_FRAG).commitAllowingStateLoss();
    }

    @Override
    public void profileComments(Post post) {
        //Open comment frag in parent frame layout
        Fragment mCurrentComments = PostCommentsFragment.newInstance(post, mCommentsInteractionListener);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getAppropriateFramelayout(), mCurrentComments).addToBackStack(COMMENTS_FRAG).commitAllowingStateLoss();
    }

    @Override
    public void refreshPosts(boolean isApproving, boolean isViewingState) {

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

    public void updateCoverImage(Bitmap bitmap) {
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
