package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.adminClasses.AdminMainFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment implements Serializable {
    private static final String MAIN_ADMIN_TAG = "main_admin_frag";

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin, container, false);
        setRetainInstance(true);

        AdminMainFragment adminMain = AdminMainFragment.newInstance("test", "test");
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (ft.isEmpty()) {
            ft.add(R.id.admin_framelayout, adminMain, MAIN_ADMIN_TAG).commit();
        }
        return v;
    }


}
