package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;

public class SearchableFrag extends Fragment implements UserListAdapter.UserListInteractionListener {
    private final static String ARG_PARENT_ORG = "parent_org";
    private final static String ARG_INTERFACE = "interface";

    private Organization mOrg;
    private SearchInterface mListener;

    public static SearchableFrag newInstance(Organization parentOrganization, SearchInterface listener) {
        SearchableFrag fragment = new SearchableFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARENT_ORG, parentOrganization);
        args.putSerializable(ARG_INTERFACE, listener);
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
            mOrg = (Organization)getArguments().getSerializable(ARG_PARENT_ORG);
            mListener = (SearchInterface)getArguments().getSerializable(ARG_INTERFACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_searchable, container, false);

        ArrayList<User> users = new ArrayList<>();
        final ListFragment searchListFrag = ListFragment.newInstance(false, null, true, null, null, null, null, users, this, null, mOrg);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(ft.isEmpty())
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.searchable_frag, searchListFrag).commit();

        SearchView searchView = (SearchView)v.findViewById(R.id.searchable_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: query results from database
                ArrayList<User> users = new ArrayList<>();
                users.add(new User(query, query, "lol", "lola", "wat", 5));
                ListFragment searchListFrag = ListFragment.newInstance(false, null, true, null, null, null, null, users, SearchableFrag.this, null, mOrg);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.searchable_frag, searchListFrag).commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return v;
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

    public interface SearchInterface extends Serializable {
        void searchUserPressed(User userPressed);
    }
}
