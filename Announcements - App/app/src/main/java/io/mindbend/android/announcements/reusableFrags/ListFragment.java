package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Notification;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.cloudCode.UserDataSource;

public class ListFragment extends Fragment implements Serializable, SwipyRefreshLayout.OnRefreshListener {
    private final static String ARG_LISTENER = "fab_listener";
    private final static String ARG_IS_SEARCHING = "is_searching";
    private static final String ARG_ORGS = "param_orgs";
    private static final String ARG_NOTIFS = "param_notifs";
    private static final String ARG_USERS = "param_users";
    private static final String ARG_ORG_OF_USERS = "the_org_of_the_users";
    private static final String ARG_USERS_TYPE = "type_of_users";
    private static final String ARG_INTERFACE = "interface_passed_in";
    private static final String ARG_IS_ADMIN = "is_current_user_admin_of_list";
    private static final String ARG_IS_VIEWING_PENDING = "is_current_user_viewing_pending_users";
    private static final String ARG_SEARCH_TEXT = "search_text";

    /**
     * The following int details WHAT object list has been passed in
     * 0 = orgs
     * 1 = notifs
     * 2 = users
     **/
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
    private boolean mViewingPending;
    private ListFabListener mListener;
    private ImageButton mAddAdminFab;

    private boolean mIsSearching;
    private String mSearchQueryText;
    private UserListAdapter mUserAdapter;
    private View mView;
    private transient ProgressBar mLoading;
    private transient SwipyRefreshLayout mRefreshLayout;

    public static ListFragment newInstance(boolean isAdmin, ListFabListener listener, boolean isSearching,
                                           ArrayList<Organization> orgsIfPresent, OrgsListAdapter.OrgListInteractionListener orgListenerIfPresent,
                                           ArrayList<Notification> notifsIfPresent, NotifsListAdapter.NotifInteractionListener notifListenerIfPresent,
                                           ArrayList<User> usersIfPresent, UserListAdapter.UserListInteractionListener userListenerIfPresent, HashMap<User,
            Integer> typeOfUser, Organization orgOfUsers, String nullableSearchQueryText, boolean viewingPendingUsers) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();

        args.putBoolean(ARG_IS_ADMIN, isAdmin);
        args.putSerializable(ARG_LISTENER, listener);
        args.putBoolean(ARG_IS_SEARCHING, isSearching);

        //if orgs
        args.putParcelableArrayList(ARG_ORGS, orgsIfPresent);
        args.putSerializable(ARG_INTERFACE, orgListenerIfPresent);

        //if notifs
        args.putParcelableArrayList(ARG_NOTIFS, notifsIfPresent);
        args.putSerializable(ARG_INTERFACE, notifListenerIfPresent);

        //if users
        args.putParcelableArrayList(ARG_USERS, usersIfPresent);
        args.putSerializable(ARG_ORG_OF_USERS, orgOfUsers);
        args.putBoolean(ARG_IS_VIEWING_PENDING, viewingPendingUsers);
        args.putSerializable(ARG_INTERFACE, userListenerIfPresent);
        args.putSerializable(ARG_USERS_TYPE, typeOfUser);
        if (nullableSearchQueryText != null && isSearching)
            args.putString(ARG_SEARCH_TEXT, nullableSearchQueryText);

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
        mListener = (ListFabListener) getArguments().getSerializable(ARG_LISTENER);
        mIsSearching = getArguments().getBoolean(ARG_IS_SEARCHING);

        if(getArguments().getString(ARG_SEARCH_TEXT) != null)
            mSearchQueryText = getArguments().getString(ARG_SEARCH_TEXT);

        if (getArguments().getParcelableArrayList(ARG_ORGS) != null) {
            whatObjectList = ORGS_SELECTED;
            mOrgs = getArguments().getParcelableArrayList(ARG_ORGS);
            mOrgListener = (OrgsListAdapter.OrgListInteractionListener) getArguments().getSerializable(ARG_INTERFACE);
        } else if (getArguments().getParcelableArrayList(ARG_NOTIFS) != null) {
            whatObjectList = NOTIFS_SELECTED;
            mNotifs = getArguments().getParcelableArrayList(ARG_NOTIFS);
            mNotifListener = (NotifsListAdapter.NotifInteractionListener) getArguments().getSerializable(ARG_INTERFACE);
        } else if (getArguments().getParcelableArrayList(ARG_USERS) != null) {
            whatObjectList = USERS_SELECTED;
            mOrgOfUsers = (Organization) getArguments().getSerializable(ARG_ORG_OF_USERS);
            mUsers = getArguments().getParcelableArrayList(ARG_USERS);
            mTypeOfUsers = (HashMap<User, Integer>) getArguments().getSerializable(ARG_USERS_TYPE);
            mOrgOfUsers = (Organization) getArguments().getSerializable(ARG_ORG_OF_USERS);
            mUserListener = (UserListAdapter.UserListInteractionListener) getArguments().getSerializable(ARG_INTERFACE);
            mViewingPending = getArguments().getBoolean(ARG_IS_VIEWING_PENDING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_list, container, false);

        mLoading = (ProgressBar) mView.findViewById(R.id.list_progressbar);


        //get the recycler view
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRefreshLayout = (SwipyRefreshLayout) mView.findViewById(R.id.list_refresher);
        mRefreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        mRefreshLayout.setOnRefreshListener(this);

        switch (whatObjectList) {
            case ORGS_SELECTED:
                OrgsListAdapter orgsAdapter = new OrgsListAdapter(getActivity(), mOrgs, mOrgListener);
                recyclerView.setAdapter(orgsAdapter);
                break;
            case NOTIFS_SELECTED:
                NotifsListAdapter notifsAdapter = new NotifsListAdapter(getActivity(), mNotifs, mNotifListener);
                recyclerView.setAdapter(notifsAdapter);
                break;
            case USERS_SELECTED:
                mUserAdapter = new UserListAdapter(getActivity(), mUsers, mUserListener, mTypeOfUsers, mIsSearching, mOrgOfUsers, mView, mLoading);
                recyclerView.setAdapter(mUserAdapter);
                if (mIsAdmin && !mViewingPending) {
                    //the add admin fab
                    mAddAdminFab = (ImageButton) mView.findViewById(R.id.list_fab);
                    mAddAdminFab.setVisibility(View.VISIBLE);
                    mAddAdminFab.setImageResource(R.drawable.ic_add_admin);
                    mAddAdminFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //when the fab is clicked
                            //add an admin to the org
                            Log.wtf("List Fab", "Add Admin Fab pressed");
                            mListener.searchForAdmins(mOrgOfUsers);

                        }
                    });
                }
                break;
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
        mListener = null;
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
        mRefreshLayout.setRefreshing(false);
        switch (whatObjectList){
            case NOTIFS_SELECTED:
                break;
            case ORGS_SELECTED:
                break;
            case USERS_SELECTED:
                if (swipyRefreshLayoutDirection.equals(SwipyRefreshLayoutDirection.BOTTOM)){
                    if (mSearchQueryText != null){
                        UserDataSource.searchForUsersInRange(getActivity(), mView, mLoading, mSearchQueryText,
                                mUsers.size(), new FunctionCallback<ArrayList<User>>() {
                                    @Override
                                    public void done(ArrayList<User> users, ParseException e) {
                                        mRefreshLayout.setRefreshing(false);
                                        if (e == null){
                                            mUsers.addAll(users);
                                            mUserAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    } else if (mViewingPending){
                        //load more pending users
                      OrgsDataSource.getRequestedPendingPrivateOrganizationUsers(mView, getActivity(), mLoading,
                              R.id.list_remove_view_while_loading, mOrgOfUsers.getmObjectId(), mUsers.size(), 20, new FunctionCallback<HashMap<Boolean, Object>>() {
                                  @Override
                                  public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {
                                      if (e == null){
                                          ArrayList<User> usersLoaded = (ArrayList<User>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                                          HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY); // all pending
                                          
                                          if (usersLoaded.size() == 0)
                                              Snackbar.make(mView, getActivity().getString(R.string.no_more_users_message), Snackbar.LENGTH_SHORT).show();

                                          mUsers.addAll(usersLoaded);
                                          mTypeOfUsers.putAll(typeOfUsers);
                                          mUserAdapter.notifyDataSetChanged();
                                      }
                                  }
                              });
                    } else {
                        //load more users
                        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(),
                                mLoading, R.id.list_remove_view_while_loading, mOrgOfUsers.getmObjectId(), mUsers.size(), 20, mIsAdmin, new FunctionCallback<HashMap<Boolean, Object>>() {
                                    @Override
                                    public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {
                                        if (e == null){
                                            ArrayList<User> usersLoaded = (ArrayList<User>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                                            HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                                            if (usersLoaded.size() == 0)
                                                Snackbar.make(mView, getActivity().getString(R.string.no_more_users_message), Snackbar.LENGTH_SHORT).show();

                                            mUsers.addAll(usersLoaded);
                                            mTypeOfUsers.putAll(typeOfUsers);
                                            mUserAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                } else {
                //TOP REFRESH
                    if (mSearchQueryText != null){
                        UserDataSource.searchForUsersInRange(getActivity(), mView, mLoading, mSearchQueryText,
                                0, new FunctionCallback<ArrayList<User>>() {
                                    @Override
                                    public void done(ArrayList<User> users, ParseException e) {
                                        if (e == null){
                                            mUsers.clear();
                                            mUsers.addAll(users);
                                            mUserAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    } else if (mViewingPending){
                        //refresh pending users
                        OrgsDataSource.getRequestedPendingPrivateOrganizationUsers(mView, getActivity(), mLoading,
                                R.id.list_remove_view_while_loading, mOrgOfUsers.getmObjectId(), 0, 20, new FunctionCallback<HashMap<Boolean, Object>>() {
                            @Override
                            public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {
                                if (e == null){
                                    ArrayList<User> usersLoaded = (ArrayList<User>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                                    HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                                    mUsers.clear();
                                    mUsers.addAll(usersLoaded);
                                    mTypeOfUsers.clear();
                                    mTypeOfUsers.putAll(typeOfUsers);
                                    mUserAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    } else {
                        //refresh users
                        OrgsDataSource.getFollowersFollowRequestsAndAdminsForOrganizationInRange(mView, getActivity(),
                                mLoading, R.id.list_remove_view_while_loading, mOrgOfUsers.getmObjectId(), 0, 20, mIsAdmin, new FunctionCallback<HashMap<Boolean, Object>>() {
                                    @Override
                                    public void done(HashMap<Boolean, Object> booleanObjectHashMap, ParseException e) {
                                        if (e == null){
                                            ArrayList<User> usersLoaded = (ArrayList<User>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_LIST_KEY);
                                            HashMap<User, Integer> typeOfUsers = (HashMap<User, Integer>)booleanObjectHashMap.get(OrgsDataSource.MAP_USER_TYPES_KEY);

                                            mUsers.clear();
                                            mUsers.addAll(usersLoaded);
                                            mTypeOfUsers.clear();
                                            mTypeOfUsers.putAll(typeOfUsers);
                                            mUserAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                }
                break;
        }
    }

    public interface ListFabListener extends Serializable {
        void searchForAdmins(Organization organization);
    }

}
