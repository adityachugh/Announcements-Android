package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;

public class SearchableFrag extends Fragment implements Serializable, UserListAdapter.UserListInteractionListener, OrgsGridAdapter.OrgInteractionListener, OrgsGridFragment.OrgsGridInteractionListener, SearchView.OnQueryTextListener {
    public final static int USERS_TYPE = 0;
    public final static int ORGS_TYPE = 1;
    public final static int SIGNUP_ORGS_TYPE = 2;

    private final static String ARG_PARENT_ORG = "parent_org";
    private final static String ARG_INTERFACE = "interface";
    private final static String ARG_TYPE_OF_LIST = "type";
    private final static String ARG_IS_ADMIN = "is_admin";

    private Organization mOrgOfUsers;
    private SearchInterface mListener;
    private int mTypeOfList;
    private transient SearchView mSearchView;
    private transient ProgressBar mLoading;

    private OrgsGridAdapter.OrgInteractionListener mOrgInteractionListener = this;
    private OrgsGridFragment.OrgsGridInteractionListener mOrgsGridInteractionListener = this;

    private ArrayList<Organization> mOrgs = new ArrayList<Organization>();
    private ArrayList<User> mUsers;
    private boolean mIsAdmin;
    private transient View mView;

    public static SearchableFrag newInstance(int typeOfList, Organization parentOrganization, SearchInterface listener, boolean isAdmin) {
        SearchableFrag fragment = new SearchableFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARENT_ORG, parentOrganization);
        args.putSerializable(ARG_INTERFACE, listener);
        args.putInt(ARG_TYPE_OF_LIST, typeOfList);
        args.putBoolean(ARG_IS_ADMIN, isAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchableFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrgOfUsers = (Organization)getArguments().getSerializable(ARG_PARENT_ORG);
            mListener = (SearchInterface)getArguments().getSerializable(ARG_INTERFACE);
            mTypeOfList = getArguments().getInt(ARG_TYPE_OF_LIST);
            mIsAdmin = getArguments().getBoolean(ARG_IS_ADMIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_searchable, container, false);
        mSearchView = (SearchView) mView.findViewById(R.id.searchable_searchview);
        mLoading = (ProgressBar) mView.findViewById(R.id.searchable_frag_progressbar);

        switch (mTypeOfList){
            case USERS_TYPE:
                final ListFragment searchListFrag = ListFragment.newInstance(mIsAdmin, null, true, null, null, null, null, mUsers, SearchableFrag.this, null, mOrgOfUsers);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if(ft.isEmpty())
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.searchable_frag, searchListFrag).commitAllowingStateLoss();
                break;
            case ORGS_TYPE:

                loadDiscoverOrgs(ParseUser.getCurrentUser().getObjectId(), 0, 10);

                OrgsGridFragment orgsGridFragment = OrgsGridFragment.newInstance(mOrgs, this, this);
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                if(ft2.isEmpty())
                    ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.searchable_frag, orgsGridFragment).commitAllowingStateLoss();
                break;
        }

        mSearchView.setOnQueryTextListener(this);
        mSearchView.onActionViewExpanded();
        //Stop keyboard from automatically popping up
        mSearchView.clearFocus();

        return mView;
    }

    private void loadDiscoverOrgs(String userObjectId, int startIndex, int numberOfOrganizations) {
        OrgsDataSource.getOrganizationsForDiscoverTabInRange(mView, getActivity(), mLoading, userObjectId, startIndex, numberOfOrganizations, new FunctionCallback<ArrayList<Organization>>() {
            @Override
            public void done(ArrayList<Organization> organizations, ParseException e) {
                if (e == null && organizations != null) {
                    OrgsGridFragment orgsGridFragment = OrgsGridFragment.newInstance(organizations, mOrgInteractionListener, mOrgsGridInteractionListener);
                    FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                    if (ft2.isEmpty())
                        ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.searchable_frag, orgsGridFragment).commitAllowingStateLoss();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void userSelected(User userPressed) {
        mListener.searchUserPressed(userPressed);
    }

    @Override
    public void selectedUserToBeAdmin(User user, Organization nullableOrg) {
        mListener.selectedUserToBeAdmin(user,nullableOrg);
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        mListener.searchOrgPressed(orgSelected);
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        mListener.searchOrgPressed(orgPressed);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        switch (mTypeOfList){
            case USERS_TYPE:
                //TODO: query results from database
                ArrayList<User> users = new ArrayList<>();
                users.add(new User(query, query, "lol", "lola", "wat", 5));
                ListFragment searchListFrag = ListFragment.newInstance(false, null, true, null, null, null, null, users, SearchableFrag.this, null, mOrgOfUsers);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.searchable_frag, searchListFrag).commitAllowingStateLoss();
                break;
            case ORGS_TYPE:
                //TODO: query results from database
                ArrayList<Organization> orgs = new ArrayList<>();
                orgs.add(new Organization("test", query, query, 54, query, false, true));
                OrgsGridFragment orgsGridFragment = OrgsGridFragment.newInstance(orgs, this, this);
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.replace(R.id.searchable_frag, orgsGridFragment).commitAllowingStateLoss();
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public interface SearchInterface extends Serializable {
        void searchUserPressed(User userPressed);
        void searchOrgPressed(Organization orgPressed);
        void selectedUserToBeAdmin (User user, Organization nullableOrg);
    }
}
