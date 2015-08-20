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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;

import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements Serializable, OrgsGridAdapter.OrgInteractionListener, PostOverlayFragment.PostsOverlayListener, OrgsGridFragment.OrgsGridInteractionListener {

    private static final String TAG = "ProfileFragment";
    public static final int UPDATE_PROFILE_IMAGE = 5;

    //To add frags to backstack
    public static final String ORG_PROFILE_FRAG = "org_profile_frag";
    private static final String BOTTOM_FRAG_TAG = "tag_for_bottom_frag_of_orgs_and_profiles";
    private Fragment mOrgProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PROFILE_LISTENER = "profile_listener_interface";
    private static final String ARG_ORG = "org";
    private static final String ARG_TO_EDIT = "to_edit";

    private User mUser;
    private Organization mOrg;
    private boolean mToEdit;

    private OrgsGridAdapter.OrgInteractionListener mOrgListener = this;
    private PostOverlayFragment.PostsOverlayListener mPostsOverlayListener = this;
    private ProfileInteractionListener mListener;
    private transient View mView;

    private transient de.hdodenhof.circleimageview.CircleImageView mUserImage;
    private transient TextView mProfileDetail;
    private transient TextView mProfileTag;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user USER.
     * @param org  ORGANIZATION.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(User user, Organization org, ProfileInteractionListener profileListener, boolean toEdit) {

        //***NOTE*** : one of user or org must be null

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_ORG, org);
        args.putSerializable(ARG_PROFILE_LISTENER, profileListener);
        args.putBoolean(ARG_TO_EDIT, toEdit);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_profile, container, false);

            mUserImage = (CircleImageView)mView.findViewById(R.id.profile_photo);

            //UI elements to be filled
            TextView name = (TextView) mView.findViewById(R.id.profile_name);
            TextView followCount = (TextView) mView.findViewById(R.id.follow_count);
            mProfileDetail = (TextView) mView.findViewById(R.id.profile_detail);
            mProfileTag = (TextView) mView.findViewById(R.id.profile_tag);
            ImageButton modifyButton = (ImageButton) mView.findViewById(R.id.profile_edit_org);

            //if the view is of an org that the user is an admin of, or if the user is viewing his/her own profile
            if (mToEdit) {
                if(mOrg != null){
                    modifyButton.setVisibility(View.VISIBLE);
                    modifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //This is what's called when the imagebutton is pressed to modify an org/user
                            Log.wtf("ProfileFrag", "modify profile");
                            //modify org
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                            builder.setTitle("Options");
                            builder.setItems(getResources().getStringArray(R.array.profile_edit_org_dialog_options), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
                            });
                            builder.show();
                        }
                    });
                }
                else {
                    //mUser isn't null
                    //this case is only true in the youfrag tab
                    //TODO: setup tap&hold to update photo and interests
                    mUserImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Log.wtf("Image", "image selection begun");
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            getActivity().startActivityForResult(Intent.createChooser(intent,
                                    "Select Picture"), UPDATE_PROFILE_IMAGE);
                            return true;
                        }
                    });

                    mProfileDetail.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

                            LinearLayout layout = new LinearLayout(getActivity());
                            layout.setOrientation(LinearLayout.VERTICAL);

                            final EditText interestOneET = new EditText(getActivity());
                            interestOneET.setHint("#1: " + mUser.getInterestOne());
                            layout.addView(interestOneET);

                            final EditText interestTwoET = new EditText(getActivity());
                            interestTwoET.setHint("#2: " + mUser.getInterestTwo());
                            layout.addView(interestTwoET);

                            alert.setView(layout);
                            alert.setTitle("Enter your Interests");
                            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //What ever you want to do with the value
                                    if(!interestOneET.getText().toString().equals("") && !interestTwoET.getText().toString().equals("")){
                                        //TODO: save to parse
                                        mUser.setInterestOne(interestOneET.getText().toString());
                                        mUser.setInterestTwo(interestTwoET.getText().toString());

                                        mProfileDetail.setText(mUser.getInterests());
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
                            return true;
                        }
                    });

                    mProfileTag.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

                            final EditText edittext= new EditText(getActivity());
                            edittext.setText(mUser.getUserCategory().substring(1));
                            alert.setTitle("Update your Tag");
                            alert.setView(edittext);
                            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //What ever you want to do with the value
                                    if(!edittext.getText().toString().equals("")){
                                        //TODO: save to parse
                                        mUser.setUserCategory("#"+edittext.getText().toString());
                                        mProfileTag.setText(mUser.getUserCategory());
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
                            return true;
                        }
                    });

                }
            }

            //TODO: branch based on whether user or org is null

            //Adapter not necessary, few elements on page
            if (mUser != null) {
                name.setText(mUser.getName());
                followCount.setText(mUser.getNumberOfOrganizationsFollowed());
                mProfileDetail.setText(mUser.getInterests());
                mProfileTag.setText(mUser.getUserCategory());

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

            if (mOrg != null) {
                if (mOrg.isPrivateOrg()) {
                    name.setText(mOrg.getTitle() + " [PRIVATE]");
                    //TODO: add imageview of lock to indicate private
                } else {
                    name.setText(mOrg.getTitle());
                }
                followCount.setText(mOrg.getFollowers() + " Followers");
                mProfileDetail.setText(mOrg.getDescription());
                mProfileTag.setText(mOrg.getTag());

                if (!mOrg.isPrivateOrg()) {

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

    public interface ProfileInteractionListener extends Serializable {
        void userProfileToOrgProfile(Organization orgSelected);

        void pressedOrgFromProfile(Organization orgPressed);

        void pressedUserFromCommentOfOrgPost(User userPressed);

        void modifyOrg(Organization org);

        void viewMembers(Organization org);

        void viewAnnouncementsState(Organization org);
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        mListener.pressedOrgFromProfile(orgPressed);
    }

    @Override
    public void visitCommentersProfile(User commenterToBeVisited) {
        mListener.pressedUserFromCommentOfOrgPost(commenterToBeVisited);
    }

    public void updateImage(Bitmap bitmap) {
        mUserImage.setImageBitmap(bitmap);
    }
}
