package io.mindbend.android.announcements.reusableFrags;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;

import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener {

    private static final String TAG = "ProfileFragment";

    //To add frags to backstack
    public static final String ORG_PROFILE_FRAG = "org_profile_frag";

    private Fragment mOrgProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_ORG = "org";
    private static final String ARG_USER_INTERFACE = "user_interface";
    private static final String ARG_ORG_INTERFACE = "org_interface";

    private User mUser;
    private Organization mOrg;
    private OrgsGridAdapter mOrgsAdapter;

    private PostsFeedAdapter.PostInteractionListener mPostListener;
    private OrgsGridAdapter.OrgInteractionListener mOrgListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user USER.
     * @param org  ORGANIZATION.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(User user, OrgsGridAdapter.OrgInteractionListener orgListener,
                                              Organization org, PostsFeedAdapter.PostInteractionListener postListener) {

        //***NOTE*** : one of user or org must be null

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_USER_INTERFACE, postListener);
        args.putSerializable(ARG_ORG, org);
        args.putSerializable(ARG_ORG_INTERFACE, orgListener);
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
            mPostListener = (PostsFeedAdapter.PostInteractionListener)getArguments().getSerializable(ARG_USER_INTERFACE);
            mOrgListener = (OrgsGridAdapter.OrgInteractionListener)getArguments().getSerializable(ARG_ORG_INTERFACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //UI elements to be filled
        TextView name = (TextView) v.findViewById(R.id.profile_name);
        TextView orgsFollowed = (TextView) v.findViewById(R.id.user_orgs_followed);
        TextView interests = (TextView) v.findViewById(R.id.user_interests);
        TextView category = (TextView) v.findViewById(R.id.user_category);

        //TODO: branch based on whether user or org is null

        //Adapter not necessary, few elements on page
        if (mUser != null) {
            name.setText(mUser.getName());
            orgsFollowed.setText(mUser.getNumberOfOrganizationsFollowed());
            interests.setText(mUser.getInterests());
            category.setText(mUser.getUserCategory());

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

            Fragment contentFragment = OrgsGridFragment.newInstance(orgs, mOrgListener, mPostListener);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.profile_content_framelayout, contentFragment).commit();
        }

        //Get scrollview, scroll to top
        //TODO: not working!
        ParallaxScrollView parallaxScrollView = (ParallaxScrollView) v.findViewById(R.id.profile_scrollview);
        parallaxScrollView.scrollTo(0, 0);

        //Get framelayout (HIERARCHY: FrameLayout > RecyclerView > CardView)
        //TODO: set height manually, based on number of grandchildren populated in framelayout
        FrameLayout profileContentFrameLayout = (FrameLayout) v.findViewById(R.id.profile_content_framelayout);

        return v;
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

//        replace the current profile frag with new org profile frag, while adding it to a backstack
        mOrgProfile = ProfileFragment.newInstance(null, null, orgSelected, mPostListener);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_framelayout, mOrgProfile).addToBackStack(ORG_PROFILE_FRAG).commit();

        Log.d(TAG, "org has been pressed on profile page " + orgSelected.toString());

    }
}
