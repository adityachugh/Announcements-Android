package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Notification;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.reusableFrags.ListFragment;
import io.mindbend.android.announcements.reusableFrags.NotifsListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment implements NotifsListAdapter.NotifInteractionListener {


    private static final String NOTIFS_FRAG = "notifications_list_fragment";
    private Fragment mNotifsList;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View v =  inflater.inflate(R.layout.fragment_notifications, container, false);

        //the following is test data
        ArrayList<Notification> testNotifs = new ArrayList<>();

        mNotifsList = ListFragment.newInstance(null, null, testNotifs, this, null, null);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.notifications_container, mNotifsList).addToBackStack(NOTIFS_FRAG).commit();
        return v;
    }

    public Fragment getmNotifsList() {
        return mNotifsList;
    }

    @Override
    public void notifPressed(Notification notifPressed) {
        //TODO: open corresponding frag
    }
}
