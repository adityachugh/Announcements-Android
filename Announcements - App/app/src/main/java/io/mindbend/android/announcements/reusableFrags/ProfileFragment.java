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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OrgsGridAdapter mOrgsAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        //FAKE USER FOR TESTING
        User testUser = new User("Aditya", "Chugh", "getting paper", "node.js", "#Grade12", 9);

        //Adapter not necessary, few elements on page
        name.setText(testUser.getName());
        orgsFollowed.setText(testUser.getNumberOfOrganizationsFollowed());
        interests.setText(testUser.getInterests());
        category.setText(testUser.getUserCategory());

        //Get scrollview, scroll to top
        //TODO: not working!
        ParallaxScrollView parallaxScrollView = (ParallaxScrollView)v.findViewById(R.id.profile_scrollview);
        parallaxScrollView.scrollTo(0, 0);

        //Get framelayout (HIERARCHY: FrameLayout > RecyclerView > CardView)
        //TODO: set height manually, based on number of grandchildren populated in framelayout
        FrameLayout profileContentFrameLayout = (FrameLayout)v.findViewById(R.id.profile_content_framelayout);

        //Fill bottom fragment with discover grid (temporary)
        //TODO: Fetch followed orgs OR organization's announcements (generic fragment)
        Fragment contentFragment = OrgsGridFragment.newInstance("test", "test");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.profile_content_framelayout, contentFragment).commit();

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


}
