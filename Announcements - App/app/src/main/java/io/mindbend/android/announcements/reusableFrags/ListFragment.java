package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Notification;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

public class ListFragment extends Fragment implements Serializable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ORGS = "param_orgs";
    private static final String ARG_NOTIFS = "param_notifs";
    private static final String ARG_USERS = "param_users";
    private static final String ARG_USERS_TYPE = "type_of_users";
    private static final String ARG_INTERFACE = "interface_passed_in";

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
    OrgsListAdapter.OrgListInteractionListener mOrgListener;
    NotifsListAdapter.NotifInteractionListener mNotifListener;
    UserListAdapter.UserListInteractionListener mUserListener;
    int mTypeOfUsers; //this is to detail if the users are members, admins, or pending members

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
    public static ListFragment newInstance(ArrayList<Organization> orgsIfPresent, OrgsListAdapter.OrgListInteractionListener orgListenerIfPresent,
                                           ArrayList<Notification> notifsIfPresent, NotifsListAdapter.NotifInteractionListener notifListenerIfPresent,
                                           ArrayList<User> usersIfPresent, UserListAdapter.UserListInteractionListener userListenerIfPresent, int typeOfUser) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();

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
            args.putSerializable(ARG_INTERFACE, userListenerIfPresent);
            args.putInt(ARG_USERS_TYPE, typeOfUser);
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
            mUserListener = (UserListAdapter.UserListInteractionListener)getArguments().getSerializable(ARG_INTERFACE);
            mTypeOfUsers = getArguments().getInt(ARG_USERS_TYPE);
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
}
