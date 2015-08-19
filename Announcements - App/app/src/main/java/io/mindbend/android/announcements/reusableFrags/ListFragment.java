package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Notification;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

public class ListFragment extends Fragment implements Serializable, View.OnClickListener {
    private static final String ARG_ORGS = "param_orgs";
    private static final String ARG_NOTIFS = "param_notifs";
    private static final String ARG_USERS = "param_users";
    private static final String ARG_ORG_OF_USERS = "the_org_of_the_users";
    private static final String ARG_USERS_TYPE = "type_of_users";
    private static final String ARG_INTERFACE = "interface_passed_in";
    private static final String ARG_IS_ADMIN = "is_current_user_admin_of_list";

    /**The following int details WHAT object list has been passed in
     * 0 = orgs
     * 1 = notifs
     * 2 = users
     * **/
    private int whatObjectList; //REMEMBER: defaultly, ints = 0
    private static final int ORGS_SELECTED = 0;
    private static final int NOTIFS_SELECTED = 1;
    private static final int USERS_SELECTED = 2;

    //lists of all possible items passed into the frag
    private List<Organization> mOrgs;
    private List<Notification> mNotifs;
    private List<User> mUsers;

    //corresponding interfaces (listeners) passed in
    private OrgsListAdapter.OrgListInteractionListener mOrgListener;
    private NotifsListAdapter.NotifInteractionListener mNotifListener;
    private Organization mOrgOfUsers;
    private UserListAdapter.UserListInteractionListener mUserListener;
    private HashMap<User, Integer> mTypeOfUsers; //this is to detail if the users are members, admins, or pending members
    private boolean mIsAdmin;

    //the searchview
    private LinearLayout mSearchLayout;
    private android.support.v7.widget.SearchView mSearchView;
    private Button mDoneSearch;

    private ImageButton mAddAdminFab;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param orgsIfPresent list of organizations if this list is used to display organizations
     * @param notifsIfPresent list of notifications if this list is used to display notifications
     * @param usersIfPresent list of users if this list is used to display users
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(boolean isAdmin,
                                           ArrayList<Organization> orgsIfPresent, OrgsListAdapter.OrgListInteractionListener orgListenerIfPresent,
                                           ArrayList<Notification> notifsIfPresent, NotifsListAdapter.NotifInteractionListener notifListenerIfPresent,
                                           ArrayList<User> usersIfPresent, UserListAdapter.UserListInteractionListener userListenerIfPresent, HashMap<User, Integer> typeOfUser, Organization orgOfUsers) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();

        args.putBoolean(ARG_IS_ADMIN, isAdmin);
        if (orgsIfPresent != null) {
            args.putParcelableArrayList(ARG_ORGS, orgsIfPresent);
            args.putSerializable(ARG_INTERFACE, orgListenerIfPresent);
        }
        if (notifsIfPresent != null) {
            args.putParcelableArrayList(ARG_NOTIFS, notifsIfPresent);
            args.putSerializable(ARG_INTERFACE, notifListenerIfPresent);
        }
        if (usersIfPresent != null) {
            args.putParcelableArrayList(ARG_USERS, usersIfPresent);
            args.putSerializable(ARG_ORG_OF_USERS, orgOfUsers);
            args.putSerializable(ARG_INTERFACE, userListenerIfPresent);
            args.putSerializable(ARG_USERS_TYPE, typeOfUser);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsAdmin = getArguments().getBoolean(ARG_IS_ADMIN);
        mOrgOfUsers = (Organization)getArguments().getSerializable(ARG_ORG_OF_USERS);

        if (getArguments().getParcelableArrayList(ARG_ORGS) != null)  {
            whatObjectList = ORGS_SELECTED;
            mOrgs = getArguments().getParcelableArrayList(ARG_ORGS);
            mOrgListener = (OrgsListAdapter.OrgListInteractionListener)getArguments().getSerializable(ARG_INTERFACE);
        }
        else if (getArguments().getParcelableArrayList(ARG_NOTIFS) != null)  {
            whatObjectList = NOTIFS_SELECTED;
            mNotifs = getArguments().getParcelableArrayList(ARG_NOTIFS);
            mNotifListener = (NotifsListAdapter.NotifInteractionListener)getArguments().getSerializable(ARG_INTERFACE);
        }
        else if (getArguments().getParcelableArrayList(ARG_USERS) != null)  {
            whatObjectList = USERS_SELECTED;
            mUsers = getArguments().getParcelableArrayList(ARG_USERS);
            mOrgOfUsers = (Organization)getArguments().getSerializable(ARG_ORG_OF_USERS);
            mUserListener = (UserListAdapter.UserListInteractionListener)getArguments().getSerializable(ARG_INTERFACE);
            mTypeOfUsers = (HashMap<User, Integer>)getArguments().getSerializable(ARG_USERS_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //get the recycler view
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchView = (SearchView)v.findViewById(R.id.list_searchview);
        mSearchLayout = (LinearLayout)v.findViewById(R.id.list_searchview_layout);
        mDoneSearch = (Button)v.findViewById(R.id.list_searchview_done);

        switch (whatObjectList){
            case ORGS_SELECTED:
                OrgsListAdapter orgsAdapter = new OrgsListAdapter(getActivity(), mOrgs, mOrgListener);
                recyclerView.setAdapter(orgsAdapter);
                break;
            case NOTIFS_SELECTED:
                NotifsListAdapter notifsAdapter = new NotifsListAdapter(getActivity(), mNotifs, mNotifListener);
                recyclerView.setAdapter(notifsAdapter);
                break;
            case USERS_SELECTED:
                UserListAdapter userAdapter = new UserListAdapter(getActivity(), mUsers, mUserListener, mTypeOfUsers);
                recyclerView.setAdapter(userAdapter);
                if (mIsAdmin){
                    //the add admin fab
                    mAddAdminFab = (ImageButton)v.findViewById(R.id.list_fab);
                    mAddAdminFab.setVisibility(View.VISIBLE);
                    mAddAdminFab.setImageResource(R.drawable.ic_add_admin);
                    mAddAdminFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //when the fab is clicked
                            switch (whatObjectList){
                                case ORGS_SELECTED:
                                    break;
                                case NOTIFS_SELECTED:
                                    break;
                                case USERS_SELECTED:
                                    //add an admin to the org
                                    Log.wtf("List Fab", "Add Admin Fab pressed");
                                    mSearchLayout.setVisibility(View.VISIBLE);
                                    mAddAdminFab.setVisibility(View.GONE);
                                    mDoneSearch.setOnClickListener(ListFragment.this);
                                    break;
                            }
                        }
                    });
                }
                break;
        }

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
    public void onClick(View v) {
        //what to do when done with search
        mSearchLayout.setVisibility(View.GONE);
        mAddAdminFab.setVisibility(View.VISIBLE);
        //TODO: reload data from Parse
    }

}
