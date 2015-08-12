package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.adminClasses.AdminMainFragment;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment implements Serializable, AdminMainFragment.AdminInteractionListener, OrgsGridAdapter.OrgInteractionListener, OrgsGridFragment.OrgsGridInteractionListener, ProfileFragment.ProfileInteractionListener {
    private static final String MAIN_ADMIN_TAG = "main_admin_frag";
    private AdminMainFragment mAdminMain;

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
            ft.add(R.id.admin_framelayout, mAdminMain, MAIN_ADMIN_TAG).addToBackStack(MAIN_ADMIN_TAG).commit();
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
        transaction.replace(R.id.admin_framelayout, childrenOrgs).addToBackStack(null).commit();
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        //load up orgs
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, this);
        getChildFragmentManager().beginTransaction().replace(R.id.admin_framelayout, orgProfile).addToBackStack(null).commit();
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
        ProfileFragment userProfile = ProfileFragment.newInstance(userPressed, null, this);
        getChildFragmentManager().beginTransaction().replace(R.id.admin_framelayout, userProfile).addToBackStack(null).commit();
    }
}
